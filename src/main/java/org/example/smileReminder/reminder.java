package org.example.smileReminder;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class reminder extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent e) {

        String[] args = e.getMessage().getContentRaw().split(" ");
        if (args[0].equalsIgnoreCase(".r")) {
            if (!e.getMember().getRoles().stream().anyMatch(role -> role.getId().equalsIgnoreCase("979641351933141053"))) {
                e.getMessage().replyEmbeds(new EmbedBuilder()
                                .setTitle("You can't do that!")
                                .setDescription("*you must have* **kai bear silver** *or higher in order to do that! \n" +
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

                    Timer workTimer = new Timer();
                    workTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            EmbedBuilder workReminder = new EmbedBuilder();
                            workReminder.setTitle("Time for .work!");
                            workReminder.setColor(Color.WHITE);
                            workReminder.setDescription("**goto** <#969147973210607626> **and do** `.work` \n" +
                                    "**Don't forget to set timer back!**");
                            e.getMessage().replyEmbeds(workReminder.build())
                                    .mentionRepliedUser(true)
                                    .queue();
                        }
                    }, 3600000);

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

                    Timer wishTimer = new Timer();
                    wishTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            EmbedBuilder wishReminder = new EmbedBuilder()
                                    .setTitle("Time for .wish!")
                                    .setColor(Color.WHITE)
                                    .setDescription("**goto** <#969147973210607626> **and do** `.wish` \n" +
                                            "**Don't forget to set timer back!**");
                            e.getMessage().replyEmbeds(wishReminder.build())
                                    .mentionRepliedUser(true)
                                    .queue();
                        }
                    }, 3600000);
            }
        }
    }
}
