package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class leaderBoardThread implements Runnable{
    CountDownLatch latch;
    MessageReceivedEvent e;
    String time;

    public leaderBoardThread(MessageReceivedEvent e, String time,CountDownLatch latch ) {
        this.e = e;
        this.time = time;
        this.latch = latch;
    }
    List<Double> messages = new ArrayList<>();
    Map<Double, String> reverseUser = new HashMap<>();

    public StringBuilder getWinnersBuilder() {
        return winnersBuilder;
    }

    StringBuilder winnersBuilder = new StringBuilder();
    @Override
    public void run() {

        try{
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

        try {
            for (int i = 2, j = 0; i > 0; i--, j++) {
                if (i == 0) break;
                String winnerAsMention = e.getGuild().retrieveMemberById(reverseUser.get(messages.stream().sorted().collect(Collectors.toList()).get(i - 1)))
                        .complete().getAsMention();
                String amountOfMessages = String.valueOf(Math.floor(messages.stream().sorted().collect(Collectors.toList()).get(i - 1)));
                winnersBuilder.append(String.format("%s. %s - %s messages \n", j + 1, winnerAsMention, amountOfMessages));
            }
        } catch (IndexOutOfBoundsException exception) {
            exception.printStackTrace();
        }
        latch.countDown();
    }
}
