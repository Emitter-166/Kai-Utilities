package org.example.roleLogging.Listeners.Logging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.example.roleLogging.Database;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

public class onRoleRemove extends ListenerAdapter {
    AuditLogEntry log;
    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent e){
        e.getGuild().retrieveAuditLogs()
                .type(ActionType.MEMBER_ROLE_UPDATE)
                .limit(1).queue(List ->{
                    if(List.isEmpty()) return;
                    log = List.get(0);

                    if(log.getUser().isBot() == (boolean) Database.get(e.getGuild().getId()).get("ignoreBot")) return;

                    StringBuilder roles = new StringBuilder();
                    e.getRoles().forEach(role -> roles.append(role.getAsMention()).append(" "));
                    EmbedBuilder  removeBuilder = new EmbedBuilder()
                            .setTitle(String.format("Role removed from %s", e.getMember().getEffectiveName()))
                            .setDescription(String.format("**Removed roles: %s** \n" +
                                    "User: %s \n" +
                                    "Responsible moderator: %s \n" +
                                    "Reasons: %s", roles, e.getMember().getAsMention(), log.getUser(), log.getReason()))
                            .setFooter(log.getTimeCreated().toString().replace('T', ' ').replace('Z', ' ').split(" ")[1] + " (UTC TIME)")
                            .setColor(Color.BLACK);

                    if(!Arrays.stream(Database.get(e.getGuild().getId()).get("sensitiveRoles").toString().split(" "))
                            .anyMatch(name -> e.getRoles().stream().anyMatch(role -> role.getName().equals(name)))){
                        Objects.requireNonNull(e.getGuild().getTextChannelById(Database.get(e.getGuild().getId()).get("loggingChannel").toString()), "Text channel empty")
                                .sendMessageEmbeds(removeBuilder.build())
                                .queue();
                    }else{
                        removeBuilder.setTitle(String.format("Sensitive role removed from %s", e.getMember().getEffectiveName()), "https://youtu.be/iik25wqIuFo");
                        String mentionRoleId = e.getGuild().getRolesByName(Database.get(e.getGuild().getId()).get("roleToPing").toString(), false).get(0).getId();
                        Objects.requireNonNull(e.getGuild().getTextChannelById(Database.get(e.getGuild().getId()).get("loggingChannel").toString()), "Text channel empty")
                                .sendMessageEmbeds(removeBuilder.build())
                                .mentionRoles(mentionRoleId)
                                .content(e.getGuild().getRoleById(mentionRoleId).getAsMention())
                                .queue();
                    }

                });


    }
}
