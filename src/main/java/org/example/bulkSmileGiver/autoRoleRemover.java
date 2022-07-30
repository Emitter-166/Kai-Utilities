package org.example.bulkSmileGiver;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class autoRoleRemover extends ListenerAdapter {
    List<Member> undo_members = new ArrayList<>();
    Role undo_role;
    public void onMessageReceived(MessageReceivedEvent e){
       try{
           if(!e.getMember().hasPermission(Permission.MODERATE_MEMBERS)) return;
       }catch (NullPointerException exception){return;}

        String[] args = e.getMessage().getContentRaw().split(" ");
        if(args.length < 2) return;
        if(!args[0].equalsIgnoreCase(".roles")) return;
        if(args[1].equalsIgnoreCase("help")){
            System.out.println("passed");
            e.getMessage().replyEmbeds(new EmbedBuilder()
                            .setTitle("Role manipulation help")
                            .setDescription("`.roles remove @roleMention` **Remove that role from every member who has it!** \n" +
                                    "`.roles remove undo` **Undo the changes from removing roles, remember this won't work after bot restart**")
                    .build()).mentionRepliedUser(false).queue();
            return;
        }

        if(args[1].equalsIgnoreCase("remove")){
            if(args.length != 3){
                e.getChannel().sendMessageFormat("Correct usage: `.roles remove @roleMention`").queue();
                return;
            }
            if(args[2].equalsIgnoreCase("undo")){
                e.getMessage().reply("`Started undoing, this will take a while. you won't receive any confirmation messages`").mentionRepliedUser(false).queue();
                undo_members.forEach(member ->{
                    e.getGuild().addRoleToMember(member, undo_role).queue();
                    undo_members.remove(member);
                });
                return;
            }


            e.getMessage().reply("`removing role started`").mentionRepliedUser(false).queue();

            undo_role = e.getGuild().getRoleById(args[2]
                    .replace("<", "")
                    .replace("@", "")
                    .replace("&", "")
                    .replace(">", ""));
            undo_members.clear();
            try{
                e.getGuild().findMembersWithRoles(undo_role).onSuccess(members -> {
                    members.forEach(member -> {
                        e.getGuild().removeRoleFromMember(member, undo_role).queue();
                        undo_members.add(member);
                    });
                    e.getMessage().reply("`removing role finished!` \n" +
                            "you can do `.roles remove undo` to undo this action").mentionRepliedUser(false).queue();
                });
            }catch (Exception exception){
                e.getMessage().reply("`An exception occurred, please try again or contact the developer`").mentionRepliedUser(false).queue();
                return;
            }
        }
    }
}
