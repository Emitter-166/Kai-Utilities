package org.example.twenty_eighty;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;

import static org.example.twenty_eighty.Database.*;

public class counter extends ListenerAdapter {
    int messageCounter = 0;
    class count implements Runnable{


        String guildId;
        String userId;

        public count(String guildId, String userId) {
            this.guildId = guildId;
            this.userId = userId;
        }

        @Override
        public void run() {
            try {
                if(Arrays.stream(Database.get(guildId, "serverId").get("users").toString().split(" ")).noneMatch(userId::equalsIgnoreCase)){
                    Database.set(guildId, "serverId", "users", " " + userId, true);
                }
                Database.set(userId, "userId", "total", 1, true);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void onMessageReceived(MessageReceivedEvent e){
        if(clearRunning){
            System.out.println("cleaner running, can't execute this. user: " + e.getAuthor().getName());
        }
        String guildId = e.getGuild().getId();
        String userId = e.getAuthor().getId();
        count count = new count(guildId, userId);
        Thread thread = new Thread(count);
        thread.start();
    }

}
