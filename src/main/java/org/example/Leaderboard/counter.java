package org.example.Leaderboard;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class counter extends ListenerAdapter{
    public void onMessageReceived(MessageReceivedEvent e){

        try {
            Database.setUser(e.getAuthor().getId(), e.getChannel().getId(), 1 + Math.random(), true);
            Database.set(e.getGuild().getId(), e.getChannel().getId() , e.getAuthor().getId() + " ", true);
            Database.set(e.getGuild().getId(), "channels" , e.getChannel().getId() + " ", true);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
