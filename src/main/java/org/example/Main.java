package org.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Main {
    static JDA jda;
    public static void main(String[] args) throws LoginException {
        jda = JDABuilder.createLight(System.getenv("token"))
                .addEventListeners(new counter())
                .addEventListeners(new Database())
                .addEventListeners(new response())
                .build();
    }
}