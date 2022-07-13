package org.example.smileReminder;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class froggyIsGay extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent e){
        if(e.getMessage().getContentRaw().equalsIgnoreCase(".froggy")){
            EmbedBuilder froggyBuilder = new EmbedBuilder()
                    .setColor(Color.BLACK)
                    .setTitle("Froggy is Gay!!!")
                    .setImage("https://cdn.discordapp.com/attachments/986979518759718944/986983733649371256/unknown.png");
            e.getMessage().replyEmbeds(froggyBuilder.build())
                    .mentionRepliedUser(false)
                    .queue();
        }
    }
}
