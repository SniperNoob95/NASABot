package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.NASABot;

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

        final List<Guild> guilds = NASABot.shardManager.getGuilds();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(String.format("NASABot v%s", NASABot.VERSION));
        embedBuilder.setDescription("Information about the bot.");
        embedBuilder.addField("Servers Present", NumberFormat.getNumberInstance(Locale.US).format(guilds.size()), false);
        embedBuilder.addField("NASABot Discord Server", "https://discord.gg/b4wS5q4", false);
        embedBuilder.addField("Top.gg Link - Give us an upvote!", "https://top.gg/bot/748775876077813881", false);
        embedBuilder.addField("GitHub", "https://github.com/SniperNoob95/NASABot", false);
        embedBuilder.setFooter("Created by Sniper Noob", "https://i.imgur.com/6WHhKrR.png");

        slashCommandEvent.replyEmbeds(embedBuilder.build()).setEphemeral(false).queue();
    }
}
