package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Help extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if(e.getMember().hasPermission(Permission.MODERATE_MEMBERS)){
            if(e.getMessage().getContentRaw().equalsIgnoreCase(".help")){
                EmbedBuilder helpBuilder = new EmbedBuilder()
                        .setTitle("Help commands for Kai Utilities")
                        .setDescription("`.l help` **Help commands for leaderboards** \n" +
                                "`$help` **help commands for role activity logging** \n" +
                                "`.smileGiver help` **see help commands for bulk smile giver**");

                e.getMessage().replyEmbeds(helpBuilder.build())
                        .mentionRepliedUser(false)
                        .queue();

            }
        }else{
            if(e.getMessage().getContentRaw().equalsIgnoreCase(".help")) {
                EmbedBuilder helpBuilder = new EmbedBuilder()
                        .setTitle("Help commands for Kai Utilities")
                        .setDescription("`.l help` **Help commands for leaderboards** \n");
                e.getMessage().replyEmbeds(helpBuilder.build())
                        .mentionRepliedUser(false)
                        .queue();
            }
        }

    }
}
