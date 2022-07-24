package org.example.twenty_eighty.EventMonitoring;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.example.twenty_eighty.Database;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
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
        if(e.getChannel().getType().equals(ChannelType.PRIVATE)) return;
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
                    try {
                        System.out.println(Database.get(e.getGuild().getId(), "serverId").get("eventMonitoringChannels"));
                        if(!(Database.get(e.getGuild().getId(), "serverId").get("eventMonitoringChannels").toString().contains(e.getChannel().getId()))
                                || (Database.get(e.getGuild().getId(), "serverId").get("eventMonitoringChannels").toString() == null)) {
                            e.getMessage().reply("`Event will be monitored here! Have fun everyone!` :grin:")
                                    .mentionRepliedUser(false)
                                    .queue();
                            e.getAuthor().openPrivateChannel().flatMap(channel -> channel.sendMessageFormat("`Started Monitoring event on` <#%s> `Do .eventMonitor help for more info`", e.getChannel().getId())).queue();

                            Database.set(e.getGuild().getId(), "serverId", "eventMonitoringChannels", " " + e.getChannel().getId(), true);
                            Database.set(e.getChannel().getId(), "eventChannelId", "timeStarted", System.currentTimeMillis(), false);
                            Database.set(e.getChannel().getId(), "eventChannelId", "HostId", e.getAuthor().getId(), false);
                        }else{
                            e.getMessage().reply("`An event is already being monitored here` :grin:")
                                    .mentionRepliedUser(false)
                                    .queue();
                        }
                    } catch (Exception ex) {
                        e.getAuthor().openPrivateChannel().flatMap(channel -> channel.sendMessageEmbeds(new EmbedBuilder()
                                .setTitle("An error occurred!")
                                .setDescription("Occurred at: ```" + ex.getStackTrace()[0] + "``` \n" +
                                        "**Please try again or contact the developer**").build())).queue();
                        ex.printStackTrace();
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
