package org.example.roleLogging.Listeners.Logging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.example.roleLogging.Database;

import java.awt.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

public class onRoleAdd extends ListenerAdapter {
    AuditLogEntry log;
    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent e){
        //it will retrieve audit logs for info whenever this event is triggered
        e.getGuild().retrieveAuditLogs()
                .type(ActionType.MEMBER_ROLE_UPDATE) //only retrieving member role update logs
                .limit(1).queue(List ->{
                    if(List.isEmpty()) return; //null safety
                    log = List.get(0);

                    if(log.getUser().isBot()) //here we check from server settings if we should ignore bot
                        if(log.getUser().isBot() == (boolean) Database.get(e.getGuild().getId()).get("ignoreBot")) return;

                    String Time = ZonedDateTime.now(ZoneId.of("America/New_York")) //getting est time
                            .format(DateTimeFormatter.ISO_LOCAL_TIME) + "(EST)";

                    StringBuilder roles = new StringBuilder();
                    e.getRoles().forEach(role -> roles.append(role.getAsMention()).append(" ")); //getting the added roles as mention and storing them in string builder

                    EmbedBuilder addBuilder = new EmbedBuilder()
                            //log embed
                            .setTitle(String.format("Role added to %s", e.getMember().getEffectiveName()))
                            .setDescription(String.format("**Added roles: %s** \n" +
                                    "User: %s \n" +
                                    "Responsible moderator: %s \n"
                                    , roles, e.getMember().getAsMention(), log.getUser()))
                            .setFooter(Time)
                            .setColor(Color.WHITE);

                    if(!Arrays.stream(Database.get(e.getGuild().getId()).get("sensitiveRoles").toString().split("-"))
                            //checks if the role is sensitive role from server settings and pings pingrole according to that
                            .anyMatch(name -> e.getRoles().stream().anyMatch(role -> role.getName().equals(name)))){
                        Objects.requireNonNull(e.getGuild().getTextChannelById(Database.get(e.getGuild().getId()).get("loggingChannel").toString()), "Text channel empty")
                                .sendMessageEmbeds(addBuilder.build())
                                .queue();
                    }else{
                        //for sensitive roles
                        addBuilder.setTitle(String.format("Sensitive role Added to %s", e.getMember().getEffectiveName()), "https://youtu.be/iik25wqIuFo");
                        String mentionRoleId = e.getGuild().getRolesByName(Database.get(e.getGuild().getId()).get("roleToPing").toString(), false).get(0).getId();
                        Objects.requireNonNull(e.getGuild().getTextChannelById(Database.get(e.getGuild().getId()).get("loggingChannel").toString()), "Text channel empty")
                                .sendMessageEmbeds(addBuilder.build())
                                .mentionRoles(mentionRoleId)
                                .content(e.getGuild().getRoleById(mentionRoleId).getAsMention())
                                .queue();
                    }

                });
    }
}
