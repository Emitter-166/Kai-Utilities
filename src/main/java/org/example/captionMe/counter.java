package org.example.captionMe;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class counter extends ListenerAdapter {
    int counter = 0;
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        counter++;
        if(counter > 500){
            counter = 0;


        }
    }
}
