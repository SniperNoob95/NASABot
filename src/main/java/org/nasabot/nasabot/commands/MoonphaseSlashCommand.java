package org.nasabot.nasabot.commands;

import java.text.MessageFormat;
import java.time.LocalDate;

import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.nasabot.nasabot.utils.AstronomyCalc;
import org.nasabot.nasabot.utils.AstronomyCalc.MoonType;

/**
 * Command to calculate the current moonphase.
 * 
 * @author Kyle Smith (kjsmita6)
 */
public class MoonphaseSlashCommand extends NASASlashCommand {

    public MoonphaseSlashCommand() {
        super();
        this.name = "moonphase";
        this.help = "Displays the current phase of the moon.";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        insertCommand(event);
        LocalDate now= LocalDate.now();

        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();

        MoonType phase = AstronomyCalc.getMoonphase(year, month, day);
        Emoji phaseEmoji = null;
        switch (phase) {
            case NEW:
                phaseEmoji = Emoji.fromUnicode("U+1F311");
                break;
            case WANING_CRES:
                phaseEmoji = Emoji.fromUnicode("U+1F318");
                break;
            case THIRD_QUART:
                phaseEmoji = Emoji.fromUnicode("U+1F317");
                break;
            case WANING_GIBB:
                phaseEmoji = Emoji.fromUnicode("U+1F316");
                break;
            case FULL:
                phaseEmoji = Emoji.fromUnicode("U+1F315");
                break;
            case WAXING_GIBB:
                phaseEmoji = Emoji.fromUnicode("U+1F314");
                break;
            case FIRST_QUART:
                phaseEmoji = Emoji.fromUnicode("U+1F313");
                break;
            case WAXING_CRES:
                phaseEmoji = Emoji.fromUnicode("U+1F312");
                break;
        }
        double days = AstronomyCalc.getDaysSinceNewMoon(year, month, day);
        event.reply(MessageFormat.format("The current moon phase is {0} {1} ({2} days until new moon).", phase, phaseEmoji.getFormatted(), (int) AstronomyCalc.MOON_PHASE_LENGTH - (int) days)).queue();;
    }

}
