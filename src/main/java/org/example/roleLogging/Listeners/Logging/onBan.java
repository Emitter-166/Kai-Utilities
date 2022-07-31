package org.example.roleLogging.Listeners.Logging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;



public class onBan extends ListenerAdapter {

    @Override
    public void onGuildBan(GuildBanEvent e){
        e.getGuild().retrieveAuditLogs()
                .type(ActionType.BAN) //only retrieving member role update logs
                .limit(1).queue(List -> {
                    if (List.isEmpty()) return; //null safety

                    AuditLogEntry log = List.get(0);

                    String Time = ZonedDateTime.now(ZoneId.of("America/New_York")) //getting est time
                            .format(DateTimeFormatter.ISO_LOCAL_TIME) + "(EST)";

                    String banned_user = e.getUser().getAsMention();
                    String responsible_mod  = log.getUser().getAsMention();
                    String reason = log.getReason();

                    e.getGuild().getTextChannelById("923412358515294218")
                            .sendMessageEmbeds(new EmbedBuilder()
                                    .setTitle(String.format("%s got banned by %s", e.getUser().getName(), log.getUser().getName()))
                                    .setDescription(String.format("**Banned user:** %s \n" +
                                            "**Responsible mod:** %s \n" +
                                            "**Reason:** `%s`", banned_user, responsible_mod, reason))
                                    .setFooter(Time)
                                    .build()).queue();

                });
    }
}
