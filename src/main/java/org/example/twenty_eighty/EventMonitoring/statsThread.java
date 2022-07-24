package org.example.twenty_eighty.EventMonitoring;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.Document;
import org.example.Main;
import org.example.twenty_eighty.Database;

import java.awt.*;
import java.util.NoSuchElementException;

public class statsThread implements Runnable{

    String serverId;
    String eventChannelId;
    String channel_to_send;
    String requested_user_Id;
    public statsThread(String serverId, String eventChannelId, String channel_to_send, String requested_user_Id) {
        this.serverId = serverId;
        this.eventChannelId = eventChannelId;
        this.channel_to_send = channel_to_send;
        this.requested_user_Id = requested_user_Id;
    }


   void sendSummary(int amount_of_users, int total_messages, int duration_of_event_in_minutes){
       String duration = duration_of_event_in_minutes + " minutes";

       amount_of_users -= 1;
       EmbedBuilder summary = new EmbedBuilder()
                .setTitle("Event stats")
                .setDescription(String.format("**Channel:** <#%s> \n", eventChannelId))
                .appendDescription(String.format("**Host:** <@%s> \n", requested_user_Id))
                .appendDescription(String.format("**Event duration:** `%s` \n", duration))
                .appendDescription(String.format("**Total user participated:** `%s` \n", amount_of_users))
                .appendDescription(String.format("**Total messages from event:** `%s` \n", total_messages))
                .appendDescription(String.format("**Average messages per user:** `%.2f` \n", (float)total_messages/amount_of_users))
               .appendDescription(String.format("**Average messages per minute:** `%.2f` \n", (float) total_messages/duration_of_event_in_minutes))
               .setColor(Color.WHITE);

       Main.jda.getTextChannelById(channel_to_send).sendMessageEmbeds(summary.build()).queue();
       try{
           Main.jda.openPrivateChannelById(requested_user_Id).flatMap(channel -> channel.sendMessageEmbeds(summary.build())).queue();
       }catch (Exception exception){}


    }

    @Override
    public void run() {

        Document document = Database.normalGet(eventChannelId, "eventChannelId");
        if((long)document.get("timeStarted") == 0L){
            Main.jda.getTextChannelById(channel_to_send).sendMessage("`Event is not being monitored in that channel!`").queue();
            return;
        }

        int amount_of_users = document.get("Users").toString().split(" ").length;
        int  total_messages = (int) document.get("totalMessages");
        int duration_in_minutes = (int) (((System.currentTimeMillis() - (long)document.get("timeStarted"))) / 60000);

        sendSummary(amount_of_users, total_messages, duration_in_minutes);
    }
}
