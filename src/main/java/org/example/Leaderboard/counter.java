package org.example.Leaderboard;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.CountDownLatch;

import static org.example.Leaderboard.Database.databaseOperationRunning;

class upDater implements Runnable{
    CountDownLatch databaseOperationRunning;
    MessageReceivedEvent e;
    String id;

    public upDater(CountDownLatch databaseOperationRunning, MessageReceivedEvent e, String id) {
        this.databaseOperationRunning = databaseOperationRunning;
        this.e = e;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            /*
            here we add every necessary variables to database when MessageReceived event is triggered
            in the first line we used Math.random to salt the value in order to keep values from
            matching each other, it will come in use in LeaderBoardThread
             */
            Database.setUser(e.getAuthor().getId(), e.getChannel().getId(), 1 + Math.random(), true);
            Database.set(id, "channels", e.getChannel().getId() + " ", true);
            Database.set(id, e.getChannel().getId(), e.getAuthor().getId() + " ", true);
            Database.set(id, "users", e.getAuthor().getId() + " ", true);
            databaseOperationRunning.countDown();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
public class counter extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent e) {
        if(e.getChannel().getType().equals(ChannelType.PRIVATE)) return;
        if(Database.cleanerRunning) {
            System.out.println("Cleaner running, can't perform this operation, code: " + e.getMember().getEffectiveName());
            databaseOperationRunning.countDown();
            return; //sync system so database doesn't get corrupted while cleaning

        }
        databaseOperationRunning = new CountDownLatch(1);
        switch (e.getMessage().getContentRaw().split(" ")[0]) {
            case ".clear":
                return;
        }
        String id = e.getGuild().getId();

       upDater upDater = new upDater(databaseOperationRunning, e, id);
       Thread thread = new Thread(upDater);
       thread.start();
    }
}
