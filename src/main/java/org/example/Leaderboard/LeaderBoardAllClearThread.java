package org.example.Leaderboard;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;

public class LeaderBoardAllClearThread{
    String args[];
    MessageReceivedEvent e;
    public LeaderBoardAllClearThread(String[] args, MessageReceivedEvent e) {
        this.args = args;
        this.e = e;
    }
    Thread clearOne = new Thread()

    {
        @Override
        public void run() {
        try {
            System.out.println("clear one running");
            Arrays.stream(Database.get(e.getGuild().getId()).get(args[1].replace("<", "")
                    .replace("#", "")
                    .replace(">", "")).toString().split(" ")).forEach(userId -> {
                try {
                    System.out.println("Clear one UserId: " + userId);
                    Database.setUser(userId, args[1].replace("<", "")
                            .replace("#", "")
                            .replace(">", ""), 0.0, false);
                    //setting value to 0 of that channel for that users db
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            });
            //setting value of that channel on server settings to nothing
            Database.set(e.getGuild().getId(), args[1].replace("<", "")
                    .replace("#", "")
                    .replace(">", ""), "", false);
            System.out.println("End of clear one thread");

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    };

    Thread clearAll = new Thread() {
        @Override
        public void run() {
            //this thread is to clear everything in the database and only keep server settings
            try {
                System.out.println("Clear all running");
                Document document = Database.get(e.getGuild().getId());
                //deleting channels from server settings
                try {
                    System.out.println(Database.get(e.getGuild().getId()).get("channels"));
                    Arrays.stream(Database.get(e.getGuild().getId()).get("channels").toString().split(" ")).forEach(channelz->{
                            System.out.println("Channel: " + channelz);
                            Document document1;
                            try {
                                document1 = Database.get(e.getGuild().getId());
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                            Document Updatedocument;
                            try {
                                Updatedocument = new Document(channelz, Database.get(e.getGuild().getId()).get(channelz));
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        System.out.println("Update doc: " + Updatedocument);
                            Bson updateKey = new Document("$unset", Updatedocument);
                            Database.collection.updateOne(document1 , updateKey);
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                //deleting user docs
                Arrays.stream(document.get("users").toString().split(" ")).forEach(user->{

                    try {
                        Database.collection.deleteOne(Database.getUserDoc(user));
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                Database.set(e.getGuild().getId(), "channels", "", false);
                Database.set(e.getGuild().getId(), "users", "", false);
                System.out.println("End of clear all thread");
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    };
}
