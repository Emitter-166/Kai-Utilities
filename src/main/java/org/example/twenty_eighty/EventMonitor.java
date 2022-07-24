package org.example.twenty_eighty;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EventMonitor extends ListenerAdapter {

    List<String> userIds = new ArrayList<>();
    int messageCount = 0;
    String channelId_to_monitor = "";
    boolean isRunning = false;

    public void onMessageReceived(MessageReceivedEvent e){
        //here is the counter
        if(isRunning && e.getChannel().getId().equalsIgnoreCase(channelId_to_monitor)){
            messageCount++;
            if(!userIds.contains(e.getAuthor().getId())){
                userIds.add(e.getAuthor().getId());
            }
        }
        if(!e.getMember().hasPermission(Permission.MODERATE_MEMBERS)) return;

        String[] args = e.getMessage().getContentRaw().split(" ");

        if(args[0].equalsIgnoreCase(".eventMonitor")){
            switch (args[1]){
                case "help":
                     e.getMessage().replyEmbeds(new EmbedBuilder()
                            .setTitle("Help")
                            .setColor(Color.BLACK)
                            .setDescription("`.eventMonitor start` **use this command in the channel you want the bot to look for** \n" +
                                    "`.eventMonitor stats` **use this to see current stats for that channel** \n" +
                                    "`.eventMonitor end` **end monitoring** \n" +
                                    "`.eventMonitor events` **see all the events that are being monitored").build())
                             .mentionRepliedUser(false)
                             .queue();
                     break;

                case "start":
                    if(isRunning){
                        e.getMessage().replyFormat("**you can't do that!** an event is already being monitored on <#%s>", channelId_to_monitor)
                                .mentionRepliedUser(false)
                                .queue();
                    }else{
                        e.getMessage().reply("`Event will be monitored in this channel!, make sure to end the monitoring after event ends :)`")
                                .mentionRepliedUser(false)
                                .queue();
                        isRunning = true;
                        channelId_to_monitor = e.getChannel().getId();
                    }
                    break;

                case "stats":
                    e.getMessage().replyEmbeds(new EmbedBuilder()
                            .setTitle("Stats")
                            .setColor(Color.WHITE)
                            .setDescription(String.format("Total messages: `%d` \n" +
                                    "Total users: `%d` \n", messageCount, userIds.size()))
                            .build())
                            .mentionRepliedUser(false)
                            .queue();
                    break;

                case "end":
                    e.getMessage().replyFormat("**ended monitoring event on** <#%s>", channelId_to_monitor)
                            .mentionRepliedUser(false)
                            .queue();
                    e.getMessage().replyEmbeds(new EmbedBuilder()
                                    .setTitle("Summary of event")
                                    .setColor(Color.WHITE)
                                    .setDescription(String.format("**Total messages:** `%d` \n" +
                                            "**Total users:** `%d` \n", messageCount, userIds.size()))
                                    .build())
                            .mentionRepliedUser(false)
                            .queue();
                    isRunning = false;
                    channelId_to_monitor = "";
                    messageCount = 0;
                    userIds.clear();
                    break;

            }
        }
    }
}
