package org.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.example.Leaderboard.Database;
import org.example.Leaderboard.counter;
import org.example.Leaderboard.response;
import org.example.bulkSmileGiver.giver;
import org.example.roleLogging.Listeners.Logging.onRoleAdd;
import org.example.roleLogging.Listeners.Logging.onRoleRemove;
import org.example.roleLogging.setup;
import org.example.smileReminder.ad;
import org.example.smileReminder.froggyIsGay;
import org.example.smileReminder.reminder;
import org.example.twenty_eighty.EventMonitor;

import javax.security.auth.login.LoginException;

public class Main {
    public static JDA jda;

    public static void main(String[] args) throws LoginException {
        jda = JDABuilder.createLight(token.getToken()) //token will go here
                .enableIntents(GatewayIntent.GUILD_MEMBERS) //enabling intents
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new counter())
                .addEventListeners(new Database())
                .addEventListeners(new response())
                .addEventListeners(new onRoleAdd())
                .addEventListeners(new onRoleRemove())
                .addEventListeners(new org.example.roleLogging.Database())
                .addEventListeners(new setup())
                .addEventListeners(new Help())
                .addEventListeners(new giver())
                .addEventListeners(new ad())
                .addEventListeners(new reminder())
                .addEventListeners(new froggyIsGay())
                .addEventListeners(new org.example.twenty_eighty.Database())
                .addEventListeners(new org.example.twenty_eighty.counter())
                .addEventListeners(new org.example.twenty_eighty.response())
                .addEventListeners(new EventMonitor())
                .build();
    }
}