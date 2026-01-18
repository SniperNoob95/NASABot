package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.NASABot;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class InfoSlashCommand extends NASABotSlashCommand {

    public InfoSlashCommand() {
        super("info", "Displays information, GitHub, and invite link for the bot.", Collections.emptyList());
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        slashCommandEvent.deferReply().queue();

        final List<Guild> guilds = NASABot.shardManager.getGuilds();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(new Color(192, 32, 232));
        embedBuilder.setTitle(String.format("NASABot v%s", NASABot.VERSION));
        embedBuilder.setDescription("Information about the bot.");
        embedBuilder.addField("Servers Present", NumberFormat.getNumberInstance(Locale.US).format(guilds.size()), false);
        embedBuilder.addField("Help/Questions?", "Join the Discord server to ask questions, get help, request features, " +
                "report bugs, talk with the owner, or just to chat with other users!", false);
        embedBuilder.addField("Top.gg Link - Give us an upvote!", "https://top.gg/bot/748775876077813881", false);
        embedBuilder.addField("GitHub", "https://github.com/SniperNoob95/NASABot", false);
        embedBuilder.setFooter("Created by Sniper Noob", "https://i.imgur.com/6WHhKrR.png");

        slashCommandEvent.getHook().sendMessageEmbeds(embedBuilder.build()).setEphemeral(false).queue();
        slashCommandEvent.getHook().sendMessage("https://discord.gg/b4wS5q4").queue();
    }
}
