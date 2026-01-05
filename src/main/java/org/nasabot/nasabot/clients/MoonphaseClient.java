package org.nasabot.nasabot.clients;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.shredzone.commons.suncalc.MoonIllumination;
import org.shredzone.commons.suncalc.MoonPhase;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class MoonphaseClient {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL dd, yyyy");

    private static class MoonphaseClientSingleton {
        private static final MoonphaseClient INSTANCE = new MoonphaseClient();
    }

    public static MoonphaseClient getInstance() {
        return MoonphaseClient.MoonphaseClientSingleton.INSTANCE;
    }

    public MessageEmbed getMoonPhase() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneOffset.UTC);
        MoonIllumination illumination = MoonIllumination.compute().on(zonedDateTime).execute();
        double phaseNumber = illumination.getPhase();
        double fraction = illumination.getFraction() * 100;

        Emoji phaseEmoji;
        String phaseName;

        /*
            Phase Number:
                -180 (new) -> 0 (full) ->  180 (new)
                Giving 8 margin on each end and the middle to account for instantaneous calculation.
            Illumination:
                0 (new) -> 100 (full)
                Giving 0.8 margin on each end to account for instantaneous calculation.
                Giving 0.8 margin in the middle (first/last quarters) to account for instantaneous calculation.
         */

        if (phaseNumber >= 8.0d) {
            if (fraction > 52.8d) {
                phaseEmoji = Emoji.fromUnicode("U+1F316");
                phaseName = "Waning Gibbous";
            } else if (fraction <= 50.8d && fraction >= 49.2d) {
                phaseEmoji = Emoji.fromUnicode("U+1F317");
                phaseName = "Last Quarter";
            } else if (fraction < 49.2d && fraction > 0.8d) {
                phaseEmoji = Emoji.fromUnicode("U+1F318");
                phaseName = "Waning Crescent";
            } else {
                phaseEmoji = Emoji.fromUnicode("U+1F311");
                phaseName = "New Moon";
            }
        } else if (phaseNumber <= -8.0d) {
            if (fraction > 52.8d) {
                phaseEmoji = Emoji.fromUnicode("U+1F314");
                phaseName = "Waxing Gibbous";
            } else if (fraction <= 50.8d && fraction >= 49.2d) {
                phaseEmoji = Emoji.fromUnicode("U+1F313");
                phaseName = "First Quarter";
            } else if (fraction < 49.2d && fraction > 0.8d) {
                phaseEmoji = Emoji.fromUnicode("U+1F312");
                phaseName = "Waxing Crescent";
            } else {
                phaseEmoji = Emoji.fromUnicode("U+1F311");
                phaseName = "New Moon";
            }
        } else {
            phaseEmoji = Emoji.fromUnicode("U+1F315");
            phaseName = "Full Moon";
        }

        MoonPhase.Parameters parametersNew = MoonPhase.compute().on(zonedDateTime)
                .phase(MoonPhase.Phase.NEW_MOON);
        MoonPhase.Parameters parametersFull = MoonPhase.compute().on(zonedDateTime)
                .phase(MoonPhase.Phase.FULL_MOON);

        ZonedDateTime nextNewMoon = parametersNew.execute().getTime();
        ZonedDateTime nextFullMoon = parametersFull.execute().getTime();

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setTitle("Current Moon Phase")
                .setDescription(zonedDateTime.format(formatter))
                .addField("Current Phase", phaseName + " " + phaseEmoji.getFormatted(), false)
                .addField("Illumination", String.format("%.1f%%", fraction), false)
                .addField("Next New Moon", nextNewMoon.format(formatter) + " - " + ChronoUnit.DAYS.between(zonedDateTime, nextNewMoon.plusDays(1)) + " days", false)
                .addField("Next Full Moon", nextFullMoon.format(formatter) + " - " + ChronoUnit.DAYS.between(zonedDateTime, nextFullMoon.plusDays(1)) + " days", false)
                .addField("Note", "Keep in mind these calculations are performed at the exact moment you send the command. There is some margin of error built in for phases which occur at exact percentages (New/Full/Quarters)," +
                        " but the phase identified may not match up exactly with what other sources report depending on the moment at which the command is sent.", false)
                .build();

        return embed;
    }
}
