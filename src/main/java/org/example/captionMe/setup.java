package org.example.captionMe;

import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;

import java.awt.*;
import java.util.Locale;

public class setup extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        try{
            if(e.getChannel().getType().equals(ChannelType.PRIVATE) || e.getAuthor().isSystem()|| !e.getMember().hasPermission(Permission.MODERATE_MEMBERS)) return;
        } catch (NullPointerException exception){return;}

        String[] args = e.getMessage().getContentRaw().split(" ");

        if(!args[0].equalsIgnoreCase(".advertiser")) return;
        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "help":
                e.getMessage().replyEmbeds(new EmbedBuilder()
                                .setTitle("Help commands for advertiser")
                                .setDescription("`.advertiser add #channelMention <repeat_duration> <ad_name>  <image_url:text> ` **Inserts a new ad** \n")
                                .appendDescription("`.advertiser remove ad_name` **remove an ad** \n")
                                .appendDescription("`.advertiser ads` **See all current ads** \n")
                                .appendDescription("`.advertiser adInfo ad_name` **see info about a current ad** \n")
                                .addField("**Command description**", "" +
                                                "       \n" +
                                                "**Adding new Ad** \n" +
                                                "Command usage:`.advertiser add <#channelMention> <repeat_duration> <ad_name> <image_url:text>` \n" +
                                                "Example: `.advertiser add` <#961068408663846983> `<60> <example ad> <https://tenor.com/view/rick-roll-rick-ashley-never-gonna-give-you-up-gif-22113173: this is an example text>` " +
                                                "ㅤㅤㅤㅤㅤㅤㅤㅤ\n" +
                                                "**Useful info:** Duration is always on minutes. Leave url or text blank if there is none, but don't forget : in it, " +
                                                "there can be multiple images and texts added, to add multiple images/texts on a single ad, put comma + whitespace in between like in this format: `<image_url:text, image_url:text, image_url:text>`, like this" +
                                                ". If there is multiple content on a single ad, it will be sent in a random order each time it's time to send an ad. perfect for trivia questions/sending random images from a library ",
                                        false)

                                .build())
                        .mentionRepliedUser(false).queue();
                break;


            case "add":
                e.getMessage().reply("`Adding new ad...`")
                        .mentionRepliedUser(false)
                        .queue(
                                message -> {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                    message.delete().queue();
                                }
                        );
                String[] values;
                long duration_milliseconds;
                StringBuilder ad_name;
                String content = null;
                String channelId = null;
                try {
                    values = e.getMessage().getContentRaw().replace(">", "").split("<");
                    channelId = values[1].replace("#", "");
                    duration_milliseconds = Long.parseLong(values[2].replace(" ", "")) * 60_000L;
                    ad_name = new StringBuilder(values[3]).delete(values[3].length() - 2, values[3].length());
                    content = values[4];

                } catch (IndexOutOfBoundsException exception) {
                    e.getMessage().reply("`wrong usage!`")
                            .mentionRepliedUser(false)
                            .queue(message ->
                            {
                                try {
                                    Thread.sleep(10000);
                                } catch (InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                                message.delete().queue();
                            });
                    return;
                }
                try {
                    Database.set(e.getGuild().getId(), "serverId", "adIds",   ad_name +", " , true);
                    Database.set(ad_name.toString(), "adId", "channel", channelId, false);
                    Database.set(ad_name.toString(), "adId", "text", content, false);
                    Database.set(ad_name.toString(), "adId", "repeat_every", duration_milliseconds, false);

                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                e.getMessage().reply("`ad successfully added! do .advertiser ads to see all the ads`").queue();
                break;

            case "ads":
                StringBuilder result = new StringBuilder();
                String[] ad_names;
                try {
                   ad_names = Database.get(e.getGuild().getId(), "serverId").get("adIds").toString().split(", ");
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                for(int i = 0; i < ad_names.length; i++){
                    result.append(String.format("`%s.` **%s** \n", i+1, ad_names[i]));
                }
                e.getMessage().replyEmbeds(new EmbedBuilder()
                                .setTitle("Current ads")
                                .setDescription(result.toString())
                        .build()).mentionRepliedUser(false).queue();
                break;

            case "adinfo":
                Document doc;
                StringBuilder adName = new StringBuilder();
                for(int i = 2; i < args.length; i++){
                    adName.append(args[i]).append(" ");
                }

                try {
                    doc = Database.get(new StringBuilder(adName).deleteCharAt(adName.length() - 1).toString(), "adId");
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                String channel = "<#" + doc.get("channel").toString().replace(" ", "") + ">";
                String content_info = doc.get("text").toString();
                String repeating_time_in_minutes = String.valueOf((Long.parseLong(doc.get("repeat_every").toString()) / 60_000));
                String last_sent = String.valueOf((Long.parseLong(doc.get("last_sent_on").toString()) / 60_000));



                e.getMessage().replyEmbeds(new EmbedBuilder()
                                .setTitle("Ad info")
                                .setDescription(String.format("**Ad name:** `%s` \n", adName))
                                .appendDescription(String.format("**Ad channel:** %s \n", channel))
                                .appendDescription(String.format("**Repeat every:** `%s minutes` \n", repeating_time_in_minutes))
                                .appendDescription(String.format("**Last sent:** `%s minutes ago` \n", last_sent))
                                .appendDescription(String.format("**Ad content:** `%s`", content_info))
                                .setColor(Color.WHITE)

                        .build()).mentionRepliedUser(false).queue();
                break;

            case "remove":
                String ad_name_to_remove = "";
                for(int i = 2; i < args.length; i++){
                    ad_name_to_remove += args[i] + " ";
                }
                e.getMessage().reply(String.format("`Deleting %s...`", ad_name_to_remove)).mentionRepliedUser(false).queue(
                        message -> {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                            message.delete().queue();
                        }
                );
                try {
                    Database.set(e.getGuild().getId(), "serverId", "adIds", Database.get(e.getGuild().getId(), "serverId").get("adIds").toString().replace(new StringBuilder(ad_name_to_remove).deleteCharAt(ad_name_to_remove.length() -1) + ", ", ""), false);
                    Database.collection.deleteOne(Filters.eq("adId", new StringBuilder(ad_name_to_remove).deleteCharAt(ad_name_to_remove.length() -1).toString()));
                    e.getMessage().reply("`Successfully removed " + ad_name_to_remove + "`").mentionRepliedUser(false).queue();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                break;
        }
    }
}
