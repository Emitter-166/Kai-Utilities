

package org.example;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Help extends ListenerAdapter {
    EmbedBuilder help = null;
    EmbedBuilder mod_help = null;
    @Override
    public void onReady(ReadyEvent e){
        help = new EmbedBuilder()
                .setTitle("Help commands for Kai Utilities")
                .setDescription("`.l help` **Help commands for leaderboards** \n" +
                        "`.td help` **Help commands for truth or dare**");
        mod_help = new EmbedBuilder()
                .setTitle("Help commands for Kai Utilities")
                .setDescription("`.l help` **Help commands for leaderboards** \n" +
                        "`$help` **help commands for role activity logging** \n" +
                        "`.smileGiver help` **see help commands for bulk smile giver** \n" +
                        "`.stats help` **see help commands with engagement statistics monitoring system** \n" +
                        "`.eventMonitor help` **see all the help command for event monitor feature** \n" +
                        "`.td help` **Help commands for truth or dare** \n" +
                        "**`true or false help` **Help commands for truth or false**");
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        long past = System.currentTimeMillis();
        if(!e.getMessage().getContentRaw().equalsIgnoreCase(".help")) return;
        /*
        this is a gateway for help commands, since this bot contains
        more than one feature, it can be hard to remember all the prefixes
        and other things, basically this will show up when you use .help
        and will redirect you to the one you are looking for
         */

        e.getMessage().replyEmbeds(help.build())
                .mentionRepliedUser(false)
                .queue();

                if (!e.getMember().hasPermission(Permission.MODERATE_MEMBERS)) return;
                //for admins and mods, the ones who have set up perms (not applicable for all the features)
                e.getMessage().replyEmbeds(mod_help.build())
                .mentionRepliedUser(false)
                .queue();

    }
}

