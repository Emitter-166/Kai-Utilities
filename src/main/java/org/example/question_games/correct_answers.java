package org.example.question_games;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class correct_answers extends ListenerAdapter {
    public static  String true_or_false_channel_id  = "";
    public static boolean answer = false;
    public static boolean isRunning = false;
    public static List<String> userIds = new ArrayList<>();
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if (!isRunning) return;
        if(e.getAuthor().isBot()) return;
        if(!e.getChannel().getId().equalsIgnoreCase(true_or_false_channel_id)) return;

        try{
            if(Boolean.parseBoolean(e.getMessage().getContentRaw()) == answer){
                if (!userIds.contains(e.getAuthor().getId())){
                    userIds.add(e.getAuthor().getId());
                }
            }
        }catch (Exception exception){}

    }
}
