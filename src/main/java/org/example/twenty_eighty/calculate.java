package org.example.twenty_eighty;


import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.example.twenty_eighty.Database.*;


public class calculate implements Runnable{

    List<Float> result = new ArrayList<>();
    List<Integer> percentages  = new ArrayList<>(Arrays.asList( 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100));

    public calculate(String serverId) {
        this.serverId = serverId;
    }

    String serverId;

    List<Integer> messages_by_users = new ArrayList<>();

    @Override
    public void run() {
        System.out.println("calculate running");
        try {
            Arrays.stream(Database.get(serverId, "serverId").get("users").toString().split(" ")).forEach(user -> {

                try {
                    if(Database.get(user, "userId") != null)
                        messages_by_users.add((Integer) Database.get(user, "userId").get("total"));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        int totalMessages = 0;
        int totalUsers = 0;
        try {
            AtomicInteger sum = new AtomicInteger();
            Arrays.stream(Database.get(serverId, "serverId").get("users").toString().split(" ")).forEach(userId ->{
                try {
                    if(Database.get(userId, "userId") != null)
                        sum.set(sum.get() + (Integer)get(userId, "userId").get("total"));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            totalMessages = sum.get();
            totalUsers =  get(serverId, "serverId").get("users").toString().split(" ").length -1;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        int temp_total;
        int user_count;
        for(int i = 0; i <= 100; i += 5){
            if(i != 0) {
                temp_total = 0;
                user_count = 0;
                List tempListOfMessagesByUsers = messages_by_users.stream().sorted().collect(Collectors.toList());

                //main algorithm
                double i_percent_messages = ((float) i / 100) * totalMessages;
                System.out.printf("%s percent of %s messages is %s \n", i, totalMessages, i_percent_messages);
                System.out.println("List of messages by user: " + tempListOfMessagesByUsers);
                while (temp_total < i_percent_messages) {
                    user_count++;
                    if (tempListOfMessagesByUsers.size() != 0) {
                        temp_total += (Integer) tempListOfMessagesByUsers.get(tempListOfMessagesByUsers.size() - 1);
                        tempListOfMessagesByUsers.remove(tempListOfMessagesByUsers.size() - 1);
                        System.out.println("temp total messages: " + temp_total + " user count: " + user_count + " percentages of users: " + ((float) user_count / totalUsers) * 100);
                    } else {
                        break;
                    }
                }
                result.add(((float) user_count / totalUsers) * 100);
            }else{
                result.add(0.0f);
            }
        }
        System.out.println("result: " + result);
        XYChart chart = QuickChart.getChart("Message stats", "Messages(%)", "Users(%)", " ", percentages, result);
        try {
            BitmapEncoder.saveBitmapWithDPI(chart, "output", BitmapEncoder.BitmapFormat.PNG, 300);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("End of Calculate");
    }
}

