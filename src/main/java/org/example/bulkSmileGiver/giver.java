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
        if (!e.getMember().hasPermission(Permission.ADMINISTRATOR)) return;
        if(e.getAuthor().equals(Main.jda.getSelfUser())) return;

        String[] args = e.getMessage().getContentRaw().split(" ");
        if (args[0].equalsIgnoreCase(".smileGiver")) {
            switch (args[1]) {
                case "help":
                    EmbedBuilder helpBuilder = new EmbedBuilder()
                            .setTitle("Setup help")
                            .setColor(Color.WHITE)
                            .addField("How to use it?",
                                    "`.smileGiver bulkGive amount userIds(separated by whitespace)` \n" +
                                            "example: `.smileGiver bulkGive 250 861321408357072937 908167053192626196 671016674668838952 857426474704830465` \n" +
                                            "\n" +
                                            "`.smileGiver manualGive userId:amount (separated by whitespace)` **for each users it will give unique amount of smiles** \n" +
                                            "example: `.smileGiver manualGive 861321408357072937:300 908167053192626196:700 671016674668838952:3034 857426474704830465:260` \n", false)
                            .setDescription("**Commands:** \n" +
                                    "note: every command of this feature starts with `.smileGiver`, every command included here must have this at first \n" +
                                    "\n" +
                                    "`bulkGive amount userIds` **Give smile to multiple users, usage above** \n" +
                                    "`manualGive userId:amount` **it will ask you the amount for each user, easier** \n");

                    e.getChannel().sendMessageEmbeds(helpBuilder.build())
                            .mentionRepliedUser(false)
                            .queue();
                    break;

                case "bulkGive":
                    String amount = args[2];
                    StringBuilder usersBuilder = new StringBuilder();

                    for(int i = 3; i < args.length; i++){
                        usersBuilder.append("<@")
                                .append(args[i])
                                .append(">")
                                .append(" ");
                    }

                    Arrays.stream(usersBuilder.toString().split(" ")).forEach(userMention ->{
                        e.getChannel().sendMessage(String.format(".money add %s %s", amount, userMention)).queue();
                    });

                    e.getChannel().sendMessage(String.format("all users are given %s smiles :), requested Admin: %s", amount,e.getAuthor().getAsMention()))
                            .queue();
                    break;

                case "manualGive":
                     for(int i = 2; i < args.length; i++){
                         String userMention = "<@" + args[i].split(":")[0]+ ">";
                         String Manualamount = args[i].split(":")[1] ;
                         e.getChannel().sendMessage(String.format(".money add %s %s", Manualamount, userMention)).queue();
                     }
                     e.getChannel().sendMessage(String.format("all users are given smiles :), requested Admin: %s", e.getAuthor().getAsMention()))
                            .queue();
                    break;
            }
        }
    }
}
