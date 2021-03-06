package org.example.Leaderboard;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class leaderBoardThread {
    /*
    this class will retrieve every users message count for a specific channel, and sort it out
    to make a leaderboard embed
     */
    MessageReceivedEvent e;
    String time;
    String ChannelId;
    List<Double> messages = new ArrayList<>();
    Map<Double, String> reverseUser = new HashMap<>();
    CountDownLatch latch = new CountDownLatch(1);
    StringBuilder winnersBuilder;

    public leaderBoardThread(MessageReceivedEvent e, String time, String ChannelId) throws InterruptedException {
        this.e = e;
        this.time = time;
        this.ChannelId = ChannelId;
    }

    public StringBuilder getWinnersBuilder() throws InterruptedException {
        latch.await();
        return winnersBuilder;
    }

    public void run() throws InterruptedException {
        winnersBuilder = new StringBuilder()
                .append(String.format("**Leader board for <#%s>**", ChannelId))
                .append(" \n");

        try {

            try {
                Arrays.stream(Database.get(e.getGuild().getId()).get(ChannelId).toString().split(" ")).forEach(userId -> {
                    double counted;
                    try {
                        try {
                            counted = (double) Database.getUser(userId, ChannelId);
                        } catch (NullPointerException exception) {
                            winnersBuilder = new StringBuilder()
                                    .append("`No Messages for that channel`");
                            return;
                        }
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    messages.add(counted);
                    reverseUser.put(counted, userId);
                });
            } catch (NullPointerException exception) {
                winnersBuilder = new StringBuilder()
                        .append("`No Messages for that channel`");

                latch.countDown();
                return;

            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

        try {
            for (int i = messages.size(), j = 0; i > 0; i--, j++) {
                if (i == 0) break;
                if (j >= 20) break;
                String winnerAsMention = String.format("<@%s>", reverseUser.get(messages.stream().sorted().collect(Collectors.toList()).get(i - 1)));

                String amountOfMessages = String.valueOf(Math.floor(messages.stream().sorted().collect(Collectors.toList()).get(i - 1)));
                winnersBuilder.append(String.format("%s. %s - %s messages \n", j + 1, winnerAsMention, amountOfMessages));
            }
        } catch (IndexOutOfBoundsException exception) {
            exception.printStackTrace();
        }
        latch.countDown();
    }
}
