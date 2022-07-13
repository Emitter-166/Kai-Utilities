package org.example.smileReminder;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class ad extends ListenerAdapter {
    int count = 0;
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if(!e.getChannel().getId().equalsIgnoreCase("969147973210607626")) return;
        count++;
        if(count%20 == 0){
            EmbedBuilder ad = new EmbedBuilder()
                    .setTitle("smile reminder!")
                    .addField("Did you know", "that you can use `.r wish` and `.r wish` to remind you to use wish and work? \n" +
                            "*this feature is exclusively for people with kai bear silver and higher. more on* <#987230669589590076>", false)
                    .setColor(Color.WHITE);
            e.getChannel().sendMessageEmbeds(ad.build()).queue();
        }

    }
}
