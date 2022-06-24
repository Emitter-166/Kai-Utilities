package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class response extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent e){
        ZoneId id = ZoneId.of("America/New_York");
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone(id));
        String time = dateFormat.format(date);


        List<Double> messages = new ArrayList<>();
        Map<Double, String> reverseUser = new HashMap<>();
        StringBuilder winnersBuilder = new StringBuilder();
        try {
            Arrays.stream(Database.get(e.getGuild().getId()).get("users").toString().split(" ")).forEach(userId -> {
                double counted;
                try {
                    counted = (double) Database.getUser(userId, "counted");
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                messages.add(counted);
                reverseUser.put(counted, userId);
            });
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        for (int i = 10, j = 0; i == 0; i--, j++) {
            String winnerAsMention = e.getGuild().retrieveMemberById(reverseUser.get(messages.stream().sorted().collect(Collectors.toList()).get(i)))
                    .complete().getAsMention();
            String amountOfMessages = String.valueOf(Math.floor(messages.stream().sorted().collect(Collectors.toList()).get(i)));
            winnersBuilder.append(String.format("%s. %s - %s messages \n", j + 1, winnerAsMention, amountOfMessages));
        }

        String args[] = e.getMessage().getContentRaw().split(" ");

        switch (args[0]){

            case ".help":
                EmbedBuilder helpBuilder = new EmbedBuilder()
                        .setTitle("Help")
                        .setColor(Color.WHITE)
                        .setDescription("`.leaderboard` **see the leaderboard of today's most active users** \n" +
                                "`.messages` **see your message counts for today** \n" +
                                "`.messages` userId **see someone else's message count for today**");
                e.getMessage().replyEmbeds(helpBuilder.build())
                        .mentionRepliedUser(false)
                        .queue();

                if(e.getMember().hasPermission(Permission.ADMINISTRATOR)){
                    EmbedBuilder setupHelpBuilder = new EmbedBuilder()
                            .setTitle("Help")
                            .setColor(Color.WHITE)
                            .setDescription("`.actionChannel` **channel to send summary at the end of the day** \n" +
                                    "`.roleToMention roleName(Case sensitive)` **role to mention when sending summary of the day** \n" +
                                    "`.mainChat` **set chat to monitor messages for**");
                    e.getMessage().replyEmbeds(setupHelpBuilder.build())
                            .mentionRepliedUser(false)
                            .queue();

                }
                break;

            case ".leaderboard":
                String Time = ZonedDateTime.now(ZoneId.of("America/New_York"))
                        .format(DateTimeFormatter.ISO_LOCAL_TIME);
                EmbedBuilder leaderboard = new EmbedBuilder()
                        .setTitle("Leaderboard for today: ")
                        .setDescription(winnersBuilder.toString())
                        .setFooter(Time)
                        .setColor(Color.WHITE);
                e.getMessage().replyEmbeds(leaderboard.build())
                        .mentionRepliedUser(false)
                        .queue();
        }


    }
}
