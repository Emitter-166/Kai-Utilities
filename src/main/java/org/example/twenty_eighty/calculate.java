package org.example.twenty_eighty;


import org.example.Main;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class calculate implements Runnable{

    List<Float> result = new ArrayList<>();
    List<Integer> percentages  = new ArrayList<>(Arrays.asList( 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100));

    String channel_to_send = "";

    public calculate(String serverId, String channel_to_send) {
        this.serverId = serverId;
        this.channel_to_send = channel_to_send;
    }

    String serverId;

    List<Integer> messages_by_users = new ArrayList<>();

    @Override
    public void run() {
        long past = System.currentTimeMillis();
        System.out.println("calculate running");
        String[] arrayOfUsers;
        try {
            arrayOfUsers = Database.get(serverId, "serverId").get("users").toString().split(" ");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int totalMessages = 0;
        System.out.println(arrayOfUsers.length -1 );
        int arrLength = arrayOfUsers.length;
           for(int i = 0; i < arrLength; i++){
               try {
                   if(Database.get(arrayOfUsers[i], "userId") != null){
                       int messages_by_member = (int) Database.get(arrayOfUsers[i], "userId").get("total");
                       messages_by_users.add(messages_by_member);
                       totalMessages = totalMessages +messages_by_member;
                       System.out.printf("%s. user: %s, messages: %s \n", i,arrayOfUsers[i], messages_by_member);
                   }
               } catch (InterruptedException e) {
                       throw new RuntimeException(e);
                   }
           }



        int temp_total;
        int user_count;

        for(int i = 0; i <= 100; i += 5){
            List tempListOfMessagesByUsers = messages_by_users.stream().sorted().collect(Collectors.toList());
            if(i != 0) {
                temp_total = 0;
                user_count = 0;
                //main algorithm
                double i_percent_messages = ((float) i / 100) * totalMessages;
                System.out.printf("%s percent of %s messages is %s \n", i, totalMessages, i_percent_messages);
                System.out.println("List of messages by user: " + tempListOfMessagesByUsers);
                while (temp_total < i_percent_messages) {
                    user_count++;
                    int size = tempListOfMessagesByUsers.size();
                    if (size != 0) {
                        temp_total += (int) tempListOfMessagesByUsers.get(size - 1);
                        tempListOfMessagesByUsers.remove(size - 1);
                        System.out.println("temp total messages: " + temp_total + " user count: " + user_count + " percentages of users: " + ((float) user_count / (arrLength - 1)) * 100);
                    } else {
                        break;
                    }
                }
                result.add(((float) user_count / (arrLength - 1)) * 100);
            }else{
                result.add(0.0f);
            }
        }
        System.out.println("result: " + result);
        XYChart chart = QuickChart.getChart("Message stats", "Messages(%)", "Users(%)", " ", percentages, result);
        try {
            BitmapEncoder.saveBitmap(chart, "output", BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("End of Calculate");
        File file = new File("output.png");
        Main.jda.getTextChannelById(channel_to_send).sendFile(file).queue(
                message -> {
                    message.editMessageFormat("**Calculation time:** `%s` minutes", (float) (System.currentTimeMillis() - past) / 60000).queue();
                }
        );
    }
}

