package org.example.captionMe;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class setup extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        try{
            if(e.getChannel().getType().equals(ChannelType.PRIVATE) || e.getAuthor().isSystem() || e.getAuthor().isSystem() || !e.getMember().hasPermission(Permission.MODERATE_MEMBERS)) return;
        } catch (NullPointerException exception){return;}

        String[] args = e.getMessage().getContentRaw().split(" ");
        switch (args[0]){
            case "help":

        }
    }
}
