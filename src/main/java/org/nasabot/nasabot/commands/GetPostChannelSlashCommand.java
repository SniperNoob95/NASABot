package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;

public class GetPostChannelSlashCommand extends NASABotSlashCommand {
    public GetPostChannelSlashCommand() {
        super("getpostchannel", "Gets the Post Channel for the server.", Collections.emptyList());
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        String postChannelId;

        try {
            if (!Objects.requireNonNull(slashCommandEvent.getMember()).hasPermission(Permission.ADMINISTRATOR) && !slashCommandEvent.getMember().isOwner()) {
                slashCommandEvent.reply("Only server administrators or the server owner may use this command.").queue();
                return;
            }

            postChannelId = dbClient.getPostChannelForServer(Objects.requireNonNull(slashCommandEvent.getGuild()).getId());
        } catch (NullPointerException e) {
            slashCommandEvent.reply("Unable to find the calling member, please try again. If this issue persists, please contact the owner of the bot.").queue();
            return;
        }

        if (postChannelId != null) {
            try {
                TextChannel textChannel = Objects.requireNonNull(slashCommandEvent.getGuild()).getTextChannelById(postChannelId);
                slashCommandEvent.reply(String.format("This server is using %s as the Post Channel. You can remove it before setting a new one with the following command:" +
                        "\n```/removepostchannel```You can configure the Post Time by using the following command: \n```/setposttime```", Objects.requireNonNull(textChannel).getAsMention())).queue();
            } catch (Exception e) {
                slashCommandEvent.reply("There is already a Post Channel set for this server, but it either no longer exists or the bot does not have permission to view it. " +
                        "Please fix your permission settings or clear the current Post Channel with the following command:\n```/removepostchannel```").queue();
            }

        } else {
            slashCommandEvent.reply("No Post Channel has been set for this server. You can set the Post Channel with the following command:" +
                    "\n```/setpostchannel <#channel>```").queue();
        }
    }
}
