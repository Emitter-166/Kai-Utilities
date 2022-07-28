package org.example.question_games;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.example.Main;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;


class process implements Runnable{
    public process(String channelId, String guildId) {
        this.channelId = channelId;
        this.guildId = guildId;
    }

    String channelId;
    String guildId;

    @Override
    public void run() {
        HttpResponse<JsonNode> response;
        try {
            response = Unirest.get("https://opentdb.com/api.php?amount=1&category=9&difficulty=easy&type=boolean").asJson();
        } catch (UnirestException ex) {
            throw new RuntimeException(ex);
        }
        String question = (String) ((JSONObject) (((JSONArray) response.getBody().getObject().get("results")).get(0)) ).get("question");
        boolean answer = Boolean.parseBoolean((String) ((JSONObject) (((JSONArray) response.getBody().getObject().get("results")).get(0))).get("correct_answer"));
        Main.jda.getTextChannelById(channelId).sendMessageEmbeds(new EmbedBuilder()
                .setTitle("True or False?")
                .setColor(Color.CYAN)
                .setDescription("**" + question.replaceAll("&quot;", "\"") + "**")
                .build()).queue();

        correct_answers.isRunning = true;
        correct_answers.true_or_false_channel_id = channelId;
        correct_answers.answer = answer;

        try {
            Thread.sleep(20_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        StringBuilder winners = new StringBuilder();
        correct_answers.userIds.forEach(user -> {
            winners.append(String.format("<@%s> \n", user));
        });
        if(winners.length() == 0){
            winners.append("`No winners`");
        }
        Main.jda.getTextChannelById(channelId).sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Correct answer is " + answer + "!!")
                .setColor(Color.CYAN)
                .setDescription("**Winners are:** \n" +
                        winners)
                .build()).queue();

        Main.jda.getTextChannelById(channelId).sendMessage(winners.toString()).queue(
                message -> message.delete().queue()
        );
        correct_answers.isRunning = false;
        correct_answers.true_or_false_channel_id = "";
        correct_answers.answer = false;
        correct_answers.userIds.clear();

        if(correct_answers.isRepeating){
            Main.jda.getTextChannelById(channelId).sendMessage("**New question in 10 seconds!** do `true or false stop` to stop repeating").queue(
                    message -> {
                        try {
                            Thread.sleep(10_000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        message.delete();
                        Main.jda.getTextChannelById(channelId).sendMessage("true or false").queue(
                                starter -> starter.delete().queue()
                        );
                    }
            );
        }
    }
}

public class true_or_false extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        try{
            if(e.getChannel().getType().equals(ChannelType.PRIVATE) || e.getAuthor().isSystem()) return;
        } catch (NullPointerException exception){return;}
        if(e.getMessage().getContentRaw().equalsIgnoreCase("true or false")){
            if(correct_answers.isRunning){
                e.getChannel().sendMessageFormat("`true or false is already running on` <#%s>", correct_answers.true_or_false_channel_id).queue();
                return;
            }
            Thread thread = new Thread(new process(e.getChannel().getId(), e.getGuild().getId()));
            thread.start();
        }
    }

}
