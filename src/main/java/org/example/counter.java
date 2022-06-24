package org.example;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class counter extends ListenerAdapter{
    public void onMessageReceived(MessageReceivedEvent e){
        if(!e.getChannel().getId().equalsIgnoreCase("961677956399394867")) return; //mainchat id will go here
        Database.setUser(e.getAuthor().getId(), "counted", 1 + Math.random(), true);
        Database.set(e.getGuild().getId(), "users", e.getAuthor().getId() + "-", true);
    }
}
