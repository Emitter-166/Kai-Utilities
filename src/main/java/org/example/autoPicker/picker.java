package org.example.autoPicker;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class picker extends ListenerAdapter {
    boolean isEnabled = false;
    @Override
    public void onMessageReceived( MessageReceivedEvent e) {

        if(isEnabled){

        }



        if(!e.getMember().hasPermission(Permission.MODERATE_MEMBERS)) return;

    }
}
