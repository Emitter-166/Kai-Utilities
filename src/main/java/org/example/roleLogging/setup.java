package org.example.roleLogging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

public class setup extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
       try{
           if(!Objects.requireNonNull(e.getMember(), "message didn't happen in a guild").hasPermission(Permission.ADMINISTRATOR)) return;
       }catch (NullPointerException exception){
           return;
       }

        String[] args = e.getMessage().getContentRaw().split(" ");

        switch (args[0]){
            case "$help":
                EmbedBuilder helpBuilder = new EmbedBuilder()
                        .setColor(Color.white)
                        .setTitle("Help")
                        .setDescription("ㅤ\n" +
                                "**setup commands:** \n" +
                                "ㅤ\n" +
                                "`$sensitiveRoles roleNames(cAsE SeNSiTIve, names separated by '-')` **roles to keep an eye on** \n" +
                                "ㅤ\n" +
                                "`$rmSensitiveRole roleName(cAsE SeNSiTIve)` **remove sensitive roles, one at a time** \n" +
                                "ㅤ\n" +
                                "`$roleToPing roleName(cAsE SeNSiTIve)` **when someone messes with sensitive roles, who to ping. Only one can be added** \n" +
                                "ㅤ\n" +
                                "`$loggingChannel` **channel to log changes on** \n" +
                                "ㅤ\n" +
                                "`$config` **see current server settings** \n" +
                                "ㅤ\n" +
                                "`$ignoreBot true/false` **ignore bots when it updates a members role**" +
                                "");
                e.getMessage().replyEmbeds(helpBuilder.build())
                        .mentionRepliedUser(false)
                        .queue();
                break;

            case "$sensitiveRoles":

                e.getMessage().reply("`sensitive roles added! you can add more or remove :), $help for more info`")
                        .mentionRepliedUser(false)
                        .queue();
                StringBuilder roleIdBuilder = new StringBuilder();
                for(int i = 1; i < args.length; i++){
                    roleIdBuilder.append(args[i]).append("-");
                }
                try {
                    Database.set(e.getGuild().getId(), "sensitiveRoles", roleIdBuilder.toString(), true);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                break;

            case "$loggingChannel":
                e.getMessage().reply("`logging channel set!`")
                        .mentionRepliedUser(false)
                        .queue();
                try {
                    Database.set(e.getGuild().getId(), "loggingChannel", e.getChannel().getId(), false);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                break;

            case "$roleToPing":
                e.getMessage().reply("`ping roles set!`")
                        .mentionRepliedUser(false)
                        .queue();
                try {
                    Database.set(e.getGuild().getId(), "roleToPing", args[1], false);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                break;

            case "$rmSensitiveRole":
                e.getMessage().reply("`sensitive role removed!`")
                        .mentionRepliedUser(false)
                        .queue();
                try {
                    StringBuilder roleToRemove = new StringBuilder();
                    for(int i = 1; i < args.length; i++){
                        roleToRemove.append(args[i]);
                    }
                    Database.set(e.getGuild().getId(), "sensitiveRoles", Database.get(e.getGuild().getId()).get("sensitiveRoles").toString().replace(args[1] + "-", ""), false);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                break;

            case "$ignoreBot":
                e.getMessage().reply("`ignore bot set!`")
                        .mentionRepliedUser(false)
                        .queue();
                try {
                    Database.set(e.getGuild().getId(), "ignoreBot", Boolean.parseBoolean(args[1]),false);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                break;

            case "$config":
                StringBuilder sensitiveRoles = new StringBuilder();
                Arrays.stream(Database.get(e.getGuild().getId()).get("sensitiveRoles").toString().split(" "))
                        .forEach(roleName -> {
                            if(!Objects.equals(roleName, ""))
                                sensitiveRoles.append(e.getGuild().getRolesByName(roleName, false).get(0).getAsMention());
                        });
                EmbedBuilder configBuilder = new EmbedBuilder()
                        .setTitle("Settings for this server: ")
                        .setColor(Color.black)
                        .setDescription("" +
                                String.format("**Sensitive roles: %s** \n" +
                                        "**Role to ping: %s** \n" +
                                        "**Logging channel: <#%s> ** \n" +
                                        "**ignore bot?** `%s`", sensitiveRoles,
                                        e.getGuild().getRolesByName(Database.get(e.getGuild().getId()).get("roleToPing").toString(), false).get(0),
                                        Database.get(e.getGuild().getId()).get("loggingChannel").toString(), Database.get(e.getGuild().getId()).get("ignoreBot")));

                e.getMessage().replyEmbeds(configBuilder.build())
                        .mentionRepliedUser(false)
                        .queue();
                break;

        }
    }
}
