package org.example.twenty_eighty;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;

import java.awt.*;
import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.example.twenty_eighty.Database.get;


public class response extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent e){
        String time = ZonedDateTime.now(ZoneId.of("America/New_York"))
                .format(DateTimeFormatter.ISO_DATE);
        Document db = null;
        try {
            db = Database.get(e.getGuild().getId(), "serverId");
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        if(!e.getMember().hasPermission(Permission.MODERATE_MEMBERS)) return;
        String[] args = e.getMessage().getContentRaw().split(" ");
        if(args[0].equalsIgnoreCase(".stats")){
            switch (args[1]){
                case "help":
                    e.getMessage().replyEmbeds(new EmbedBuilder()
                            .setTitle("Help commands")
                            .setColor(Color.yellow)
                            .setDescription("`.stats calculate` **see calculated stats of messages and users** \n" +
                                    "`.stats reset` **reset current data** \n").build())
                            .mentionRepliedUser(false)
                            .queue();
                    break;

                case "calculate":
                    calculate calculate = new calculate(e.getGuild().getId());
                    Thread thread = new Thread(calculate);
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    File file = new File("output.png");

                    //getting total message count
                    AtomicInteger sum = new AtomicInteger();
                    try {
                        Arrays.stream(Database.get(e.getGuild().getId(), "serverId").get("users").toString().split(" ")).forEach(userId ->{
                            try {
                                if(Database.get(userId, "userId") != null)
                                    sum.set(sum.get() + (Integer)get(userId, "userId").get("total"));
                            } catch (InterruptedException exception) {
                                throw new RuntimeException(exception);
                            }
                        });
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }

                    e.getChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setTitle("Summary")
                            .setColor(Color.WHITE)
                            .setDescription(String.format("**Date:** %s", time) + " \n" +
                                    String.format("**Total messages:** %s",sum.get()) + " \n" +
                                    String.format("**Total users:** %s", db.get("users").toString().split(" ").length)).build()).queue();
                    e.getMessage().reply(file).queue();
                    break;

                case "reset":
                    e.getMessage().reply("`Database reset complete!`")
                            .mentionRepliedUser(false)
                            .queue();
                   Thread cleanerThread = new Thread(new clear(e.getGuild().getId()));
                   cleanerThread.start();
                   break;

                case "roleToMention":
                    e.getMessage().reply("`ping role for summary set!`")
                            .mentionRepliedUser(false)
                            .queue();
                    System.out.println(args[2]);
                   String role = args[2].replace("<", "").replace("@", "").replace(">", "").replace("&", "");
                    try {
                        Database.set(e.getGuild().getId(), "serverId", "actionChannel", role, false);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;

                case "summaryChannel":
                    e.getMessage().reply("`summary channel set!`")
                            .mentionRepliedUser(false)
                            .queue();
                    try {
                        Database.set(e.getGuild().getId(), "serverId", "actionChannel", e.getChannel().getId(), false);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
            }
        }
    }
}
