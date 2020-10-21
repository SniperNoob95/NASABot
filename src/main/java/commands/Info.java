package commands;

import bot.NASABot;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;

public class Info extends NASACommand {

    public Info() {
        this.name = "info";
        this.help = "Displays information, GitHub, and invite link for the bot.";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        this.insertCommand(commandEvent);

        List<Guild> guildList = NASABot.jda.getGuilds();
        int numServers = guildList.size();
        int numPlayers  = 0;
        for (Guild guild : guildList) {
            numPlayers += guild.getMembers().size();
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("NASABot");
        embedBuilder.setDescription("Information about the bot.");
        embedBuilder.addField("GitHub", "https://github.com/SniperNoob95/NASABot", false);
        embedBuilder.addField("Servers Present", String.format("%s",numServers), false);
        embedBuilder.addField("Players Served", String.format("%s", numPlayers), false);
        embedBuilder.addField("NASABot Discord Server", "https://discord.gg/b4wS5q4", false);
        embedBuilder.addField("Top.gg Link - Give us an upvote!", "https://top.gg/bot/748775876077813881", false);
        embedBuilder.setFooter("Created by Sniper Noob", "https://i.imgur.com/ilKsNnn.png");

        commandEvent.reply(embedBuilder.build());
    }
}
