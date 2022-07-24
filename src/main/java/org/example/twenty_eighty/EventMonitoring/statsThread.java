package org.example.twenty_eighty.EventMonitoring;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.Document;
import org.example.Main;
import org.example.twenty_eighty.Database;

import java.awt.*;

public class statsThread implements Runnable{

    String serverId;
    String eventChannelId;
    String channel_to_send;

    public statsThread(String serverId, String eventChannelId, String channel_to_send) {
        this.serverId = serverId;
        this.eventChannelId = eventChannelId;
        this.channel_to_send = channel_to_send;
    }


   void sendSummary(int amount_of_users, int total_messages, int duration_of_event_in_minutes, String host_id){
        String duration = null;
       if(duration_of_event_in_minutes/60 < 0){
           duration = duration_of_event_in_minutes + " minutes";
       }else{
           duration = String.format("%.1f", duration_of_event_in_minutes/60f) + " hours";
       }

       EmbedBuilder summary = new EmbedBuilder()
                .setTitle("Event stats")
                .setDescription(String.format("**Channel:** `%s` /n", eventChannelId))
                .appendDescription(String.format("**Host:** <@%s> /n", host_id))
                .appendDescription(String.format("**Event duration:** `%s`", duration))
                .appendDescription(String.format("**Total user participated:** `%s`", amount_of_users))
                .appendDescription(String.format("**Total messages from event:** `%s`", total_messages))
                .appendDescription(String.format("**Average messages per user:** `%s`", total_messages/amount_of_users))
                .setColor(Color.WHITE);
       Main.jda.getTextChannelById(channel_to_send).sendMessageEmbeds(summary.build()).queue();


   }

    @Override
    public void run() {

        Document document;
        try {
            document = Database.get(eventChannelId, "eventChannelId");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int amount_of_users = document.get("Users").toString().split(" ").length;
        int total_messages = (int) document.get("totalMessages");
        String hostId = document.get("HostId").toString();
        int duration_in_minutes = (int) (((System.currentTimeMillis() - (long) document.get("timeStarted"))) / 60000);

        sendSummary(amount_of_users, total_messages, duration_in_minutes, hostId);
    }
}
