package org.example.bulkSmileGiver;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.example.Main;

import java.awt.*;
import java.util.Arrays;

public class giver extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        //checking perms
        if (!e.getMember().hasPermission(Permission.ADMINISTRATOR)) return;
        if (e.getAuthor().equals(Main.jda.getSelfUser())) return;


        String[] args = e.getMessage().getContentRaw().split(" ");
        if (args[0].equalsIgnoreCase(".smileGiver")) {
            switch (args[1]) {
                case "help":
                    //help command
                    EmbedBuilder helpBuilder = new EmbedBuilder()
                            .setTitle("Setup help")
                            .setColor(Color.WHITE)
                            .addField("How to use it?",
                                    "`.smileGiver bulkGive amount userIds(separated by whitespace)` \n" +
                                            "example: `.smileGiver bulkGive 250 861321408357072937 908167053192626196 671016674668838952 857426474704830465` \n" +
                                            "Note: **You can explicitly set an users amount by doing** `userId:amount` **with the user id**\n" +
                                            "example: `.smileGiver bulkGive 250 861321408357072937:300 908167053192626196 671016674668838952 857426474704830465` \n" +
                                            "It will give the first user 300 smiles instead of 250, it can be done with any users", false)
                            .setDescription("**Commands:** \n" +
                                    "note: every command of this feature starts with `.smileGiver`, every command included here must have this at first \n" +
                                    "\n" +
                                    "`bulkGive amount userIds` **Give smile to multiple users, usage below** \n" +
                                    " \n");

                    e.getChannel().sendMessageEmbeds(helpBuilder.build())
                            .mentionRepliedUser(false)
                            .queue();
                    break;

                case "bulkGive":
                    //this command will send a generated command according to the input to the channel, making manual tasks easier
                    String amount = args[2];
                    StringBuilder usersBuilder = new StringBuilder();


                    for (int i = 3; i < args.length; i++) {

                       if(args[i].split(":").length == 1){
                           usersBuilder.append("<@")
                                   .append(args[i])
                                   .append(">")
                                   .append(" ");
                       }else if(args[i].split(":").length == 2){
                           usersBuilder.append("<@")
                                   .append(args[i].split(":")[0])
                                   .append(">")
                                   .append(":")
                                   .append(args[i].split(":")[1])
                                   .append(" ");
                       }
                    }

                    Arrays.stream(usersBuilder.toString().split(" ")).forEach(userMention -> {
                         String tempAmount = userMention.split(":").length == 2 ? userMention.split(":")[1] : amount;
                        e.getChannel().sendMessage(String.format(".money add %s %s", tempAmount, userMention.split(":")[0])).queue();
                    });

                    e.getChannel().sendMessage(String.format("all users are given %s smiles :), requested Admin: %s", amount, e.getAuthor().getAsMention()))
                            .queue();
                    break;
            }
        }
    }
}
