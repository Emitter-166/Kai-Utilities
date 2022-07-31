package org.example.smileReminder;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class reminder extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent e) {

        String[] args = e.getMessage().getContentRaw().split(" ");
        if (args[0].equalsIgnoreCase(".r")) {
            if (!e.getMember().getRoles().stream().anyMatch(role -> role.getId().equalsIgnoreCase("979641351933141053"))) {
                e.getMessage().replyEmbeds(new EmbedBuilder()
                                .setTitle("You can't do that!")
                                .setDescription("*you must have* <@&979641351933141053> *or higher in order to do that! \n" +
                                        "more infos on:* <#987230669589590076>")
                                .setColor(Color.BLACK).build())
                        .mentionRepliedUser(false)
                        .queue();
                return;
            }
            switch (args[1]) {
                case "work":
                    EmbedBuilder workBuilder = new EmbedBuilder();
                    workBuilder.setTitle("Reminder set!");
                    workBuilder.setDescription("**work reminder set!** \n" +
                            "**Don't forget to do** `.work` **in** <#969147973210607626> \n" +
                            "**we will remind you soon!**");
                    e.getMessage().replyEmbeds(workBuilder.build())
                            .mentionRepliedUser(false)
                            .queue();
                    try {
                        Database.set(e.getAuthor().getId(), "userId", "work", System.currentTimeMillis(), false);
                        Database.set(e.getAuthor().getId(), "userId", "channelId",e.getChannel().getId(), false);
                        if(!Database.get(e.getGuild().getId(), "serverId").getString("users").contains(e.getAuthor().getId())){
                            Database.set(e.getGuild().getId(), "serverId", "users",e.getAuthor().getId() + " ", true);
                        }
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;

                case "wish":
                    EmbedBuilder wishBuilder = new EmbedBuilder()
                            .setTitle("Reminder set!")
                            .setDescription("**wish reminder set!** \n" +
                                    "**Don't forget to do** `.wish` **in** <#969147973210607626> \n" +
                                    "**we will remind you soon!**")
                            .setColor(Color.black);
                    e.getMessage().replyEmbeds(wishBuilder.build())
                            .mentionRepliedUser(false)
                            .queue();
                    try {
                        Database.set(e.getAuthor().getId(), "userId", "wish", System.currentTimeMillis(), false);
                        Database.set(e.getAuthor().getId(), "userId", "channelId",e.getChannel().getId(), false);
                        if(!Database.get(e.getGuild().getId(), "serverId").getString("users").contains(e.getAuthor().getId())){
                            Database.set(e.getGuild().getId(), "serverId", "users",e.getAuthor().getId() + " ", true);
                        }
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
            }
        }
    }
}
