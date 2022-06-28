package org.example.Leaderboard;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class counter extends ListenerAdapter{
    public void onMessageReceived(MessageReceivedEvent e){
        switch (e.getMessage().getContentRaw().split(" ")[0]){
            case ".clear": return;
        }
        String id = e.getGuild().getId();
        try {
            Database.setUser(e.getAuthor().getId(), e.getChannel().getId(), 1 + Math.random(), true);
            Database.set(id, "channels" , e.getChannel().getId() + " ", true);
            Database.set(id, e.getChannel().getId(), e.getAuthor().getId() + " ", true);
            Database.set(id, "users", e.getAuthor().getId() + " ", true);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
