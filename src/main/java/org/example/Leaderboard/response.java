package org.example.Leaderboard;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.awt.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class response extends ListenerAdapter {
    boolean hasSent = false;


    public void onMessageReceived(MessageReceivedEvent e) {
        String Time = ZonedDateTime.now(ZoneId.of("America/New_York"))
                .format(DateTimeFormatter.ISO_LOCAL_TIME) + "(EST)";


        String args[] = e.getMessage().getContentRaw().split(" ");
        switch (args[0]) {

            case ".l":
                if (args[1].equalsIgnoreCase("help")) {
                    EmbedBuilder helpBuilder = new EmbedBuilder()
                            .setTitle("Help")
                            .setColor(Color.WHITE)
                            .setDescription("`.leaderboard` **see the leaderboard of today's most active users** \n" +
                                    "`.messages` **see your message counts for today** \n" +
                                    "`.messages` userId **see someone else's message count for today**");
                    e.getMessage().replyEmbeds(helpBuilder.build())
                            .mentionRepliedUser(false)
                            .queue();


                    if (e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                        EmbedBuilder setupHelpBuilder = new EmbedBuilder()
                                .setTitle("Help commands for leaderboard setup")
                                .setColor(Color.WHITE)
                                .setDescription("`.actionChannel` **channel to send summary at the end of the day** \n" +
                                        "`.roleToMention roleName(Case sensitive)` **role to mention when sending summary of the day** \n" +
                                        "`.mainChat` **set chat to monitor messages for** \n" +
                                        "`.clear` **reset leaderboard** \n" +
                                        "`.clear all` **reset everything**" +
                                        "`.reset true/false` **set if the leaderboard should reset every 24 hours**");
                        e.getMessage().replyEmbeds(setupHelpBuilder.build())
                                .mentionRepliedUser(false)
                                .queue();

                    }
                }
                break;

            case ".leaderboard":
                if (args.length == 2) {
                    leaderBoardThread leaderboardThread;
                    try {
                        leaderboardThread = new leaderBoardThread(e, Time,
                                args[1].replace("<", "")
                                        .replace("#", "")
                                        .replace(">", ""));
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        leaderboardThread.run();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    EmbedBuilder leaderboard;
                    try {
                        leaderboard = new EmbedBuilder()
                                .setTitle("Leaderboard for today: ")
                                .setDescription(leaderboardThread.getWinnersBuilder().toString())
                                .setFooter(Time)
                                .setColor(Color.WHITE);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    e.getMessage().replyEmbeds(leaderboard.build())
                            .mentionRepliedUser(false)
                            .queue();
                } else {
                    EmbedBuilder leaderboardUsageBuilder = new EmbedBuilder()
                            .setTitle("Usage of this command: ")
                            .setDescription("`.leaderboard channelMention` \n" +
                                    "example: `.leaderboard` <#880331517291794442>");
                    e.getMessage().replyEmbeds(leaderboardUsageBuilder.build())
                            .mentionRepliedUser(false)
                            .queue();
                }

                break;

            case ".mainChat":
                if (!e.getMember().hasPermission(Permission.ADMINISTRATOR)) return;
                e.getMessage().reply("`main chat set! messages will be monitored here!`")
                        .mentionRepliedUser(false)
                        .queue();
                try {
                    Database.set(e.getGuild().getId(), "mainChat", e.getChannel().getId(), false);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                break;

            case ".actionChannel":
                if (!e.getMember().hasPermission(Permission.ADMINISTRATOR)) return;
                e.getMessage().reply("`Action set! Summaries will be sent here!`")
                        .mentionRepliedUser(false)
                        .queue();
                try {
                    Database.set(e.getGuild().getId(), "actionChannel", e.getChannel().getId(), false);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                break;

            case ".roleToMention":
                if (!e.getMember().hasPermission(Permission.ADMINISTRATOR)) return;
                e.getMessage().reply("`ping roles set! this role will be pinged with the summaries!`")
                        .mentionRepliedUser(false)
                        .queue();
                try {
                    Database.set(e.getGuild().getId(), "roleToMention", e.getGuild().getRolesByName(args[1], false).get(0).getId(), false);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                break;

            case ".messages":
                if (args.length == 1) {
                    try {
                        e.getMessage().reply(String.format("You have a total of %s messages today!", Math.floor((Double) Database.getUser(e.getAuthor().getId(), "counted"))))
                                .mentionRepliedUser(false)
                                .queue();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } else if (args.length == 2) {
                    try {
                        e.getMessage().reply(String.format("%s have a total of %s messages today!",
                                        e.getGuild().retrieveMemberById(args[1]).complete().getAsMention(), Math.floor((Double) Database.getUser(args[1], "counted"))))
                                .mentionRepliedUser(false)
                                .queue();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                break;

            case ".clear":
                if (args.length == 2) {
                    if (!e.getMember().hasPermission(Permission.MODERATE_MEMBERS)) return;
                    if(!args[1].equalsIgnoreCase("all")){
                        e.getMessage().reply("Leader board cleared!")
                                .mentionRepliedUser(false)
                                .queue();
                        LeaderBoardAllClearThread leaderBoardAllClearThread = new LeaderBoardAllClearThread(args, e);
                        leaderBoardAllClearThread.clearOne.start();
                        System.out.println("clear one thread running");
                    }else{

                        LeaderBoardAllClearThread leaderBoardAllClearThread = new LeaderBoardAllClearThread(args, e);
                        leaderBoardAllClearThread.clearAll.start();
                        System.out.println("clear All thread running");
                        e.getMessage().reply("Leader board cleared!")
                                .mentionRepliedUser(false)
                                .queue();

                    }


                }else {
                    EmbedBuilder leaderboardUsageBuilder = new EmbedBuilder()
                            .setTitle("Usage of this command: ")
                            .setDescription("`.clear channelMention` \n" +
                                    "example: `.clear` <#880331517291794442> \n" +
                                    "**do** `.clear all` **to reset everything**");
                    e.getMessage().replyEmbeds(leaderboardUsageBuilder.build())
                            .mentionRepliedUser(false)
                            .queue();
                }
                break;

                case ".reset":
                        if (args.length != 2) return;
                        e.getMessage().reply(String.format("Resetting leaderboard every 24 hours is set to %s", args[1]))
                                .mentionRepliedUser(false)
                                .queue();
                        try {
                            Database.set(e.getGuild().getId(), "reset", Boolean.parseBoolean(args[1]), false);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                }

                //summary of the day
                try {
                    if ((boolean) Database.get(e.getGuild().getId()).get("reset"))
                        if (!hasSent) {
                            String[] timeArgs = Time.split(":");
                            if (timeArgs[0].equalsIgnoreCase("00")) {
                                leaderBoardThread leaderboardThread;
                                try {
                                    leaderboardThread = new leaderBoardThread(e, Time, (String) Database.get(e.getGuild().getId()).get("mainChat"));
                                    leaderboardThread.run();
                                } catch (InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                                EmbedBuilder leaderboard;
                                try {
                                    leaderboard = new EmbedBuilder()
                                            .setTitle("Summary of today: ")
                                            .setDescription(leaderboardThread.getWinnersBuilder().toString())
                                            .setFooter(Time)
                                            .setColor(Color.WHITE);
                                } catch (InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                                try {
                                    String mentionRoleId = (String) Database.get(e.getGuild().getId()).get("roleToMention");
                                    e.getGuild().getTextChannelById((String) Database.get(e.getGuild().getId()).get("actionChannel")).sendMessageEmbeds(leaderboard.build())
                                            .content(String.format("%s", e.getGuild().getRoleById(mentionRoleId).getAsMention()))
                                            .mentionRoles(mentionRoleId)
                                            .queue();

                                } catch (InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                                Document document = null;
                                try {
                                    document = Database.get(e.getGuild().getId());
                                } catch (InterruptedException exc) {
                                    throw new RuntimeException(exc);
                                }
                                //deleting datas
                                LeaderBoardAllClearThread allClearThread = new LeaderBoardAllClearThread(args, e);
                                allClearThread.clearAll.start();
                                hasSent = true;
                            } else {
                                hasSent = false;
                            }
                        }
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
        }
    }
