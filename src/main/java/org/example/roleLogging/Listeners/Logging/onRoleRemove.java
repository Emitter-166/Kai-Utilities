package org.example.roleLogging.Listeners.Logging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.example.roleLogging.Database;

import java.awt.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

public class onRoleRemove extends ListenerAdapter {
    AuditLogEntry log;
    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent e){
        //it will retrieve audit logs for info whenever this event is triggered
        e.getGuild().retrieveAuditLogs()
                .type(ActionType.MEMBER_ROLE_UPDATE) //only retrieving member role update logs
                .limit(1).queue(List ->{
                    if(List.isEmpty()) return; //null safety
                    log = List.get(0);

                    String Time = ZonedDateTime.now(ZoneId.of("America/New_York")) //getting est time
                            .format(DateTimeFormatter.ISO_LOCAL_TIME) + "(EST)";

                    if(log.getUser().isBot()) //here we check from server settings if we should ignore bot
                        if(log.getUser().isBot() == (boolean) Database.get(e.getGuild().getId()).get("ignoreBot")) return;

                    StringBuilder roles = new StringBuilder();
                    e.getRoles().forEach(role -> roles.append(role.getAsMention()).append(" "));//getting the removed roles as mention and storing them in string builder
                    EmbedBuilder  removeBuilder = new EmbedBuilder()
                            //log embeds
                            .setTitle(String.format("Role removed from %s", e.getMember().getEffectiveName()))
                            .setDescription(String.format("**Removed roles: %s** \n" +
                                    "User: %s \n" +
                                    "Responsible moderator: %s \n"
                                    ,roles, e.getMember().getAsMention(), log.getUser()))
                            .setFooter(Time)
                            .setColor(Color.BLACK);

                    if(!Arrays.stream(Database.get(e.getGuild().getId()).get("sensitiveRoles").toString().split("-"))
                            //checks if the role is sensitive role from server settings and pings pingrole according to that
                            .anyMatch(name -> e.getRoles().stream().anyMatch(role -> role.getName().equals(name)))){
                        Objects.requireNonNull(e.getGuild().getTextChannelById(Database.get(e.getGuild().getId()).get("loggingChannel").toString()), "Text channel empty")
                                .sendMessageEmbeds(removeBuilder.build())
                                .queue();
                    }else{
                        //for sensitive roles
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
