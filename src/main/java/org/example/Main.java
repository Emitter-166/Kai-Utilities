package org.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.example.Leaderboard.Database;
import org.example.Leaderboard.counter;
import org.example.Leaderboard.response;
import org.example.roleLogging.Listeners.Logging.onRoleAdd;
import org.example.roleLogging.Listeners.Logging.onRoleRemove;
import org.example.roleLogging.setup;

import javax.security.auth.login.LoginException;

public class Main {
    static JDA jda;
    public static void main(String[] args) throws LoginException {
        jda = JDABuilder.createLight("OTYxNjM3NDA1NTcwNDk4NjUy.G_IVj1.MxjqN9HVOkU3EmtXmx9GLdBMsWshbhWunDQGG4")
                .addEventListeners(new counter())
                .addEventListeners(new Database())
                .addEventListeners(new response())
                .addEventListeners(new onRoleAdd())
                .addEventListeners(new onRoleRemove())
                .addEventListeners(new org.example.roleLogging.Database())
                .addEventListeners(new setup())
                .build();
    }
}