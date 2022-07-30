package org.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.example.Leaderboard.Database;
import org.example.Leaderboard.response;
import org.example.bulkSmileGiver.autoRoleRemover;
import org.example.question_games.correct_answers;
import org.example.question_games.true_or_false;
import org.example.question_games.truthAndDare;
import org.example.bulkSmileGiver.giver;
import org.example.captionMe.counter;
import org.example.roleLogging.Listeners.Logging.onBan;
import org.example.roleLogging.Listeners.Logging.onRoleAdd;
import org.example.roleLogging.Listeners.Logging.onRoleRemove;
import org.example.roleLogging.setup;
import org.example.smileReminder.ad;
import org.example.smileReminder.froggyIsGay;
import org.example.smileReminder.reminder;
import org.example.twenty_eighty.EventMonitoring.EventMonitor;
import org.example.twenty_eighty.EventMonitoring.EventMonitorCounter;

import javax.security.auth.login.LoginException;

public class Main {
    public static JDA jda;

    public static void main(String[] args) throws LoginException {
        jda = JDABuilder.createLight(token.getToken()) //token will go here
                .addEventListeners(new Help())
                .addEventListeners(new EventMonitor())
                .addEventListeners(new EventMonitorCounter())
                .addEventListeners(new org.example.twenty_eighty.Database())
                .enableIntents(GatewayIntent.GUILD_MEMBERS) //enabling intents
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new org.example.Leaderboard.counter())
                .addEventListeners(new Database())
                .addEventListeners(new response())
                .addEventListeners(new onRoleAdd())
                .addEventListeners(new onRoleRemove())
                .addEventListeners(new org.example.roleLogging.Database())
                .addEventListeners(new setup())
                .addEventListeners(new giver())
                .addEventListeners(new ad())
                .addEventListeners(new reminder())
                .addEventListeners(new froggyIsGay())
                .addEventListeners(new org.example.twenty_eighty.counter())
                .addEventListeners(new org.example.twenty_eighty.response())
                .addEventListeners(new truthAndDare())
                .addEventListeners(new counter())
                .addEventListeners(new org.example.captionMe.Database())
                .addEventListeners(new org.example.captionMe.setup())
                .addEventListeners(new true_or_false())
                .addEventListeners(new correct_answers())
                .addEventListeners(new autoRoleRemover())
                .addEventListeners(new onBan())
                .build();
    }
}