package org.example.smileReminder;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.example.Main;

import java.awt.*;
import java.util.Arrays;

class reminding implements Runnable{
    public reminding(String id) {
        this.id = id;
    }

    String id;
    @Override
    public void run() {
        try {
            Arrays.stream(Database.get(id, "serverId").get("users").toString().split(" ")).forEach(user ->{
                try {
                    Document doc = Database.get(user, "userId");
                    long wish = doc.getLong("wish");
                    long work = doc.getLong("work");
                    String channel = doc.getString("channelId");

                    if(System.currentTimeMillis() - wish > 3600000){
                        if(wish == 0L) return;
                        Database.set(user, "userId", "wish", 0L, false);
                        Database.set(id, "serverId", "users", Database.get(id, "serverId").getString("users").replace(user + " ", ""), false);

                        EmbedBuilder wishReminder = new EmbedBuilder()
                                .setTitle("Time for .wish!")
                                .setColor(Color.WHITE)
                                .setDescription("**goto** <#969147973210607626> **and do** `.wish` \n" +
                                        "**Don't forget to set timer back!**");
                        Main.jda.getTextChannelById(channel).sendMessageEmbeds(wishReminder.build())
                                .content(String.format("<@%s>", user))
                                .queue();

                    }

                    if(System.currentTimeMillis() - work > 3600000){
                        if(work == 0L) return;
                        Database.set(user, "userId", "work", 0L, false);
                        Database.set(id, "serverId", "users", Database.get(id, "serverId").getString("users").replace(user + " ", ""), false);
                        EmbedBuilder workReminder = new EmbedBuilder()
                                .setTitle("Time for .work!")
                                .setColor(Color.WHITE)
                                .setDescription("**goto** <#969147973210607626> **and do** `.work` \n" +
                                        "**Don't forget to set timer back!**");
                        Main.jda.getTextChannelById(channel).sendMessageEmbeds(workReminder.build())
                                .content(String.format("<@%s>", user))
                                .queue();

                    }

                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            });
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
public class counter extends ListenerAdapter {
    int counter;

    public void onMessageReceived(MessageReceivedEvent e){
        counter++;
        if(counter >= 50){
            counter = 0;
            Thread thread = new Thread(new reminding(e.getGuild().getId()));
            thread.start();
        }
    }
}
