package org.example.captionMe;

import net.dv8tion.jda.api.EmbedBuilder;
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

        if(!args[0].equalsIgnoreCase(".advertiser")) return;
        switch (args[1]){
            case "help":
                e.getMessage().replyEmbeds(new EmbedBuilder()
                                .setTitle("Help commands for advertiser")
                                .setDescription("`.advertiser add <#channelMention> <repeat_duration> <ad_name>  <image_url:text> ` **Inserts a new ad")
                                .appendDescription("`.advertiser edit <ad_name> <field_name> <value>` **Edit a specific field of an ad** \n")
                                .appendDescription("`.advertiser remove <ad_name>` **remove an ad** \n")
                                .appendDescription("`.advertiser ads` **See all current ads** \n")
                                .appendDescription("`.advertiser adInfo <ad_name>` **see info about a current ad** \n")
                                .appendDescription("`.advertiser showAd <ad_name>` **see the ad example** \n")
                                .addField("**Command description**", "" +
                                        "       \n" +
                                        "**Adding new Ad** \n" +
                                                "Command usage:`.advertiser add <#channelMention> <repeat_duration> <ad_name> <image_url:text>` \n" +
                                                "Example: `.advertiser add` <#961068408663846983> `<60> <example ad> <https://tenor.com/view/rick-roll-rick-ashley-never-gonna-give-you-up-gif-22113173: this is an example text>` " +
                                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                                "**Useful info:** Duration is always on minutes. Leave url or text blank if there is none, but don't forget : in it, " +
                                                "there can be multiple images and texts added, to add multiple images/texts on a single ad, put those in this format: `<image_url:text image_url:text image_url:text>`, like this" +
                                                ". If there is multiple content on a single ad, it will be sent in a random order each time it's time to send an ad. perfect for trivia questions/sending random images from a library ",
                                        false)

                        .build())
                        .mentionRepliedUser(false).queue();

        }
    }
}
