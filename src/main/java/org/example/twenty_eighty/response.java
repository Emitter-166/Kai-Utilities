package org.example.twenty_eighty;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;

import java.awt.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.example.twenty_eighty.Database.get;


public class response extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent e){
        if(e.getChannel().getType().equals(ChannelType.PRIVATE)) return;
        String time = ZonedDateTime.now(ZoneId.of("America/New_York"))
                .format(DateTimeFormatter.ISO_DATE);
        Document db = null;
        try {
            db = Database.get(e.getGuild().getId(), "serverId");
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
       try{
           if(!e.getMember().hasPermission(Permission.MODERATE_MEMBERS)) return;
       }catch (NullPointerException exception){}
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
                    calculate calculate = new calculate(e.getGuild().getId(), e.getChannel().getId());
                    Thread thread = new Thread(calculate);
                    thread.start();
                    //getting total message count
                    Document finalDb = db;
                    Runnable runnable = () -> {
                        int sum = 0;
                        try {
                            String[] array_of_users = get(e.getGuild().getId(), "serverId").get("users").toString().split(" ");
                            for(int i = 0; i < array_of_users.length; i++) {
                                try {
                                    if (Database.get(array_of_users[i], "userId") != null)
                                        sum = sum + (int) get(array_of_users[i], "userId").get("total");
                                } catch (InterruptedException exception) {
                                    throw new RuntimeException(exception);
                                }
                            }
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        e.getChannel().sendMessageEmbeds(new EmbedBuilder()
                                .setTitle("Summary")
                                .setColor(Color.WHITE)
                                .setDescription(String.format("**Total messages:** %s",sum) + " \n" +
                                        String.format("**Total users:** %s", finalDb.get("users").toString().split(" ").length)).build()).queue();
                    };
                    Thread thread1 = new Thread(runnable);
                    thread1.start();
                    break;

                case "reset":
                    e.getMessage().reply("`Database reset complete!`")
                            .mentionRepliedUser(false)
                            .queue();
                   Thread cleanerThread = new Thread(new clear(e.getGuild().getId()));
                   cleanerThread.start();
                   break;
            }
        }
    }
}
