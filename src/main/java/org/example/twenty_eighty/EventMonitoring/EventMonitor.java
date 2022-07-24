package org.example.twenty_eighty.EventMonitoring;

import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.example.twenty_eighty.Database;

import java.awt.*;

public class EventMonitor extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent e){
        try{
            if(!e.getMember().hasPermission(Permission.MODERATE_MEMBERS)) return;
            if(e.getChannel().getType().equals(ChannelType.PRIVATE)) return;
        }catch(NullPointerException exception){}
        
        if(e.getMessage().getContentRaw().contains(".eventMonitor")){

            String[] args = e.getMessage().getContentRaw().split(" ");
            String id = e.getAuthor().getId();
            String channel_id_arg =  args[2].replace("<", "")
                .replace("#", "")
                .replace(">", "");
            String serverId = e.getGuild().getId();

            switch (args[1]){
                case "help":
                     e.getMessage().replyEmbeds(new EmbedBuilder()
                            .setTitle("Help")
                            .setColor(Color.BLACK)
                            .setDescription("`.eventMonitor start` **use this command in the channel you want the bot to look for** \n" +
                                    "`.eventMonitor stats channelMention` **use this to see current stats for that channel** \n" +
                                    "`.eventMonitor end channelMention` **end monitoring** \n" +
                                    "`.eventMonitor events` **see all the events that are being monitored**").build())
                             .mentionRepliedUser(false)
                             .queue();
                     break;

                case "start":
                    try {
                        if(!(Database.get(serverId, "serverId").get("eventMonitoringChannels").toString().contains(e.getChannel().getId()))
                                || (Database.get(serverId, "serverId").get("eventMonitoringChannels").toString() == null)) {
                            e.getMessage().reply("`Event will be monitored here! Have fun everyone!` :grin:")
                                    .mentionRepliedUser(false)
                                    .queue();

                            Database.set(serverId, "serverId", "eventMonitoringChannels", " " + e.getChannel().getId(), true);
                            Database.set(e.getChannel().getId(), "eventChannelId", "timeStarted", System.currentTimeMillis(), false);
                            Database.set(e.getChannel().getId(), "eventChannelId", "HostId", id, false);
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
                    if(args.length == 2){
                        e.getMessage().reply("**Usages:** `.eventMonitor stats channelMention` \n" +
                                "**Example:** `.eventMonitor stats` <#880331517291794442>")
                                .mentionRepliedUser(false)
                                .queue();
                        return;
                    }

                    statsThread stats = null;
                    try {
                        stats = new statsThread(serverId,
                              channel_id_arg
                                , e.getChannel().getId(),Database.get(
                              channel_id_arg, "eventChannelId").get("HostId").toString());
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    Thread statsThread = new Thread(stats);
                    statsThread.start();
                    break;

                case "end":
                    if(args.length == 2){
                        e.getMessage().reply("**Usages:** `.eventMonitor end channelMention` \n" +
                                        "**Example:** `.eventMonitor end` <#880331517291794442>")
                                .mentionRepliedUser(false)
                                .queue();
                        return;
                    }
                    e.getMessage().replyFormat("**ended monitoring event on** %s", args[2])
                            .mentionRepliedUser(false)
                            .queue();
                    statsThread summaryThread;

                    try {
                        summaryThread = new statsThread(serverId,
                                channel_id_arg, e.getChannel().getId(),
                                Database.get(
                                      channel_id_arg, "eventChannelId").get("HostId").toString());
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    Thread thread = new Thread(summaryThread);
                    thread.start();
                    try {
                        thread.join(20_000);
                        Database.collection.deleteOne(Filters.eq("eventChannelId", channel_id_arg));
                        Database.set(serverId, "serverId", "eventMonitoringChannels", Database.get(serverId,
                                "serverId").get("eventMonitoringChannels").toString().replace(
                                      " " + channel_id_arg, ""), false);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;

                case "events":
                    String array_of_event_channels[];
                    StringBuilder eventsBuilder = new StringBuilder();
                    try {
                       array_of_event_channels =  Database.get(serverId, "serverId").get("eventMonitoringChannels").toString().split(" ");
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    if(array_of_event_channels.length == 1){
                        e.getMessage().reply("`No events running atm`")
                                .mentionRepliedUser(false)
                                .queue();
                        return;
                    }
                    for(int i = 1; i < array_of_event_channels.length; i++){
                        eventsBuilder.append(String.format("`%s.` <#%s> \n", i, array_of_event_channels[i]));
                    }
                    e.getMessage().replyEmbeds(new EmbedBuilder()
                            .setTitle("Ongoing events: ")
                            .setDescription(eventsBuilder.toString())
                            .setColor(Color.white).build())
                            .mentionRepliedUser(false)
                            .queue();
            }
        }
    }
}
