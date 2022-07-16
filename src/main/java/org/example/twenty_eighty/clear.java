package org.example.twenty_eighty;

import org.bson.Document;

import java.util.Arrays;

import static org.example.twenty_eighty.Database.*;

public class clear implements Runnable{
    public clear(String serverId) {
        this.serverId = serverId;
    }

    String serverId;


    @Override
    public void run() {
        clearRunning = true;
        System.out.println("clear thread running");
        try {
            Arrays.stream(Database.get(serverId, "serverId").get("users").toString().split(" ")).forEach(userId ->{
                try {
                    if(Database.get(userId, "userId") != null)
                        Database.collection.deleteOne(new Document("userId", userId));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            Database.set(serverId, "serverId", "users", "", false);
            Database.set(serverId, "serverId", "totalUsers", 0, false);
            Database.set(serverId, "serverId", "messages", 0, false);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        clearRunning = false;
        System.out.println("clear thread stopped");

    }
}
