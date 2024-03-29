package org.example.twenty_eighty.EventMonitoring;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.example.twenty_eighty.Database;

public class EventMonitorCounter extends ListenerAdapter {


    class Counting implements Runnable{
        public Counting(MessageReceivedEvent e) {
            this.e = e;
        }

        MessageReceivedEvent e;
        @Override
        public void run() {
            if(e.getChannel().getType().equals(ChannelType.PRIVATE)) return;
            try {
                if(Database.get(e.getGuild().getId(), "serverId").toString().contains(e.getChannel().getId())){
                    if(!Database.get(e.getChannel().getId(), "eventChannelId").get("Users").toString().contains(e.getAuthor().getId())
                            ||Database.get(e.getChannel().getId(), "eventChannelId").get("Users").toString() == null){
                        Database.set(e.getChannel().getId(), "eventChannelId", "Users", " " + e.getAuthor().getId(), true);
                    }
                    Database.set(e.getChannel().getId(), "eventChannelId", "totalMessages", 1, true);
                }
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        Thread thread = new Thread(new Counting(e));
        thread.start();
    }
}
