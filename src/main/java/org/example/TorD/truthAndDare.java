package org.example.TorD;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.awt.*;
import java.io.IOException;
import java.util.Locale;

public class truthAndDare extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        try{
            if(e.getChannel().getType().equals(ChannelType.PRIVATE) || e.getAuthor().isSystem() || e.getAuthor().isSystem()) return;
        } catch (NullPointerException exception){return;}

        //help cmds
        String message = e.getMessage().getContentRaw();
        if(message.equalsIgnoreCase(".td help")) {
            e.getMessage().replyEmbeds(new EmbedBuilder()
                            .setTitle("Help commands for truth or dare")
                            .setColor(Color.CYAN)
                            .setDescription("`.truth` **get a truth question** \n")
                            .appendDescription("`.dare` **get a dare task (disabled)** \n" +
                                    "`.wouldyourather` **get a would you rather question** \n" +
                                    "`.paranoia` **get a paranoia question (disabled)** \n" +
                                    "`.neverhaveiever` **get a never have I ever question (disabled)** \n")
                            .build())
                    .mentionRepliedUser(false)
                    .queue();
        }

        switch (message.toLowerCase(Locale.ROOT)){
            case ".truth":
                try {
                    e.getMessage().replyEmbeds(new EmbedBuilder()
                                    .setTitle("Truth")
                                    .setColor(Color.WHITE)
                                    .setDescription(String.format("**%s**", getTruth()))
                                    .build())
                            .mentionRepliedUser(false)
                            .queue();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (UnirestException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case ".dare":
                if(!e.getMember().hasPermission(Permission.ADMINISTRATOR)) return;
                try {
                    e.getMessage().replyEmbeds(new EmbedBuilder()
                                    .setTitle("Dare")
                                    .setColor(Color.WHITE)
                                    .setDescription(String.format("**%s**", getDare()))
                                    .build())
                            .mentionRepliedUser(false)
                            .queue();
                } catch (UnirestException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case ".wouldyourather":
                try {
                    e.getMessage().replyEmbeds(new EmbedBuilder()
                                    .setTitle("Would you rather...")
                                    .setColor(Color.WHITE)
                                    .setDescription(String.format("**%s**", getWouldYouRather()))
                                    .build())
                            .mentionRepliedUser(false)
                            .queue();
                } catch (UnirestException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case ".paranoia":
                if(!e.getMember().hasPermission(Permission.ADMINISTRATOR)) return;
                try {
                    e.getMessage().replyEmbeds(new EmbedBuilder()
                                    .setTitle("Paranoia!")
                                    .setColor(Color.WHITE)
                                    .setDescription(String.format("**%s**", getParanoia()))
                                    .build())
                            .mentionRepliedUser(false)
                            .queue();
                } catch (UnirestException ex) {
                    throw new RuntimeException(ex);
                }
                break;

            case ".neverhaveiever":
                if(!e.getMember().hasPermission(Permission.ADMINISTRATOR)) return;
                try {
                    e.getMessage().replyEmbeds(new EmbedBuilder()
                                    .setTitle("Never have you ever...")
                                    .setColor(Color.WHITE)
                                    .setDescription(String.format("**%s**", getNeverHaveIEver()))
                                    .build())
                            .mentionRepliedUser(false)
                            .queue();
                } catch (UnirestException ex) {
                    throw new RuntimeException(ex);
                }
                break;
        }
    }



    static String getTruth() throws IOException, UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("https://api.truthordarebot.xyz/v1/truth")
                .asJson();
        return (String) response.getBody().getObject().get("question");
    }

    static String getWouldYouRather() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("https://api.truthordarebot.xyz/api/wyr")
                .asJson();
        return (String) response.getBody().getObject().get("question");
    }

    static String getDare() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("https://api.truthordarebot.xyz/api/dare")
                .asJson();
        return (String) response.getBody().getObject().get("question");
    }

    static String getParanoia() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("https://api.truthordarebot.xyz/api/paranoia")
                .asJson();
        return (String) response.getBody().getObject().get("question");
    }

    static String getNeverHaveIEver() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("https://api.truthordarebot.xyz/api/nhie")
                .asJson();
        return (String) response.getBody().getObject().get("question");
    }
}
