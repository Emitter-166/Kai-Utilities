package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Help extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        /*
        this is a gateway for help commands, since this bot contains
        more than one feature, it can be hard to remember all the prefixes
        and other things, basically this will show up when you use .help
        and will redirect you to the one you are looking for
         */

        try{
            if (e.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {
                //for admins and mods, the ones who have set up perms (not applicable for all the features)
                if (e.getMessage().getContentRaw().equalsIgnoreCase(".help")) {
                    EmbedBuilder helpBuilder = new EmbedBuilder()
                            .setTitle("Help commands for Kai Utilities")
                            .setDescription("`.l help` **Help commands for leaderboards** \n" +
                                    "`$help` **help commands for role activity logging** \n" +
                                    "`.smileGiver help` **see help commands for bulk smile giver**");

                    e.getMessage().replyEmbeds(helpBuilder.build())
                            .mentionRepliedUser(false)
                            .queue();

                }
            } else {
                //help for normal users
                if (e.getMessage().getContentRaw().equalsIgnoreCase(".help")) {
                    EmbedBuilder helpBuilder = new EmbedBuilder()
                            .setTitle("Help commands for Kai Utilities")
                            .setDescription("`.l help` **Help commands for leaderboards** \n");
                    e.getMessage().replyEmbeds(helpBuilder.build())
                            .mentionRepliedUser(false)
                            .queue();
                }
            }
        }catch (NullPointerException exception){}

    }
}
