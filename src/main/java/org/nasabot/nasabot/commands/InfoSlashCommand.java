package org.nasabot.nasabot.commands;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.nasabot.nasabot.NASABot;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class InfoSlashCommand extends NASASlashCommand {

    public InfoSlashCommand() {
        this.name = "info";
        this.help = "Displays information, GitHub, and invite link for the bot.";
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        final List<Guild> guilds = NASABot.shardManager.getGuilds();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(String.format("NASABot v%s", NASABot.VERSION));
        embedBuilder.setDescription("Information about the bot.");
        embedBuilder.addField("GitHub", "https://github.com/SniperNoob95/NASABot", false);
        embedBuilder.addField("Servers Present", NumberFormat.getNumberInstance(Locale.US).format(guilds.size()), false);
        embedBuilder.addField("NASABot Discord Server", "https://discord.gg/b4wS5q4", false);
        embedBuilder.addField("Top.gg Link - Give us an upvote!", "https://top.gg/bot/748775876077813881", false);
        embedBuilder.setFooter("Created by Sniper Noob", "https://i.imgur.com/ilKsNnn.png");

        slashCommandEvent.replyEmbeds(embedBuilder.build()).setEphemeral(false).queue();
    }
}
