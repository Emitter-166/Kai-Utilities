package org.example.captionMe;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;
import org.example.Main;
import java.util.Arrays;
import java.util.Objects;

import static org.example.captionMe.Database.get;

public class counter extends ListenerAdapter {
    int counter = 0;
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        counter++;
        if(counter > 50){
            counter = 0;
            sendAds(e.getGuild().getId());
        }
    }


    public static void sendAds(String serverId){

        try {
            Arrays.stream(get(serverId, "serverId").get("adIds").toString().split(", ")).forEach(ad -> {

            try {
                Document doc = Database.get(ad, "adId");
                long time_sent = Long.parseLong(doc.get("last_sent_on").toString());
                long repeat_every = Long.parseLong(doc.get("repeat_every").toString());

                if( System.currentTimeMillis() - time_sent > repeat_every){
                    TextChannel channel = Main.jda.getTextChannelById(doc.get("channel").toString().replace(" ", ""));
                    String[] text = doc.get("text").toString().split(", ");
                    String[] toSend  = text[(int) Math.floor(Math.random() * text.length)].split("::");


                    if(!Objects.equals(toSend[1], "")){
                        channel.sendMessage(toSend[1]).queue();
                    }
                    if(!Objects.equals(toSend[0], "")){
                        channel.sendMessage(toSend[0]).queue();
                    }
                    Database.set(ad, "adId", "last_sent_on", System.currentTimeMillis(), false);

                }else if(time_sent == 0){
                    Database.set(ad, "adId", "last_sent_on", System.currentTimeMillis(), false);
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
