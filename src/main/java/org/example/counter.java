package org.example;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class counter extends ListenerAdapter{
    public void onMessageReceived(MessageReceivedEvent e){
        try {
            if(!e.getChannel().getId().equalsIgnoreCase((String) Database.get(e.getGuild().getId()).get("mainChat"))) return; //mainchat id will go here
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        Database.setUser(e.getAuthor().getId(), "counted", 1 + Math.random(), true);
        Database.set(e.getGuild().getId(), "users", e.getAuthor().getId() + " ", true);
    }
}
