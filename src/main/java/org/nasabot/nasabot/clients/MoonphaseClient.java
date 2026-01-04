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
        MoonIllumination illumination = MoonIllumination.compute().on(localDateTime).execute();
        double phaseNumber = illumination.getPhase();

        Emoji phaseEmoji = Emoji.fromUnicode("U+2753");
        String phaseName = "UNKNOWN";

        if (phaseNumber == -180.0d || phaseNumber == 180.0d) {
            phaseEmoji = Emoji.fromUnicode("U+1F311");
            phaseName = "New Moon";
        } else if (phaseNumber < -90.0d) {
            phaseEmoji = Emoji.fromUnicode("U+1F312");
            phaseName = "Waxing Crescent";
        } else if (phaseNumber == -90.0d) {
            phaseEmoji = Emoji.fromUnicode("U+1F313");
            phaseName = "First Quarter";
        } else if (phaseNumber < -0.8d) {
            phaseEmoji = Emoji.fromUnicode("U+1F314");
            phaseName = "Waxing Gibbous";
        } else if (phaseNumber >= -0.8d && phaseNumber <= 0.8d) {
            phaseEmoji = Emoji.fromUnicode("U+1F315");
            phaseName = "Full Moon";
        } else if (phaseNumber > 90.0d) {
            phaseEmoji = Emoji.fromUnicode("U+1F316");
            phaseName = "Waning Gibbous";
        } else if (phaseNumber == 90.0d) {
            phaseEmoji = Emoji.fromUnicode("U+1F317");
            phaseName = "Last Quarter";
        } else if (phaseNumber > 0.8d) {
            phaseEmoji = Emoji.fromUnicode("U+1F318");
            phaseName = "Waning Crescent";
        }

        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneOffset.UTC);

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
                .addField("Illumination", String.format("%.1f%%", illumination.getFraction() * 100), false)
                .addField("Next New Moon", nextNewMoon.format(formatter) + " - " + ChronoUnit.DAYS.between(zonedDateTime, nextNewMoon.plusDays(1)) + " days", false)
                .addField("Next Full Moon", nextFullMoon.format(formatter) + " - " + ChronoUnit.DAYS.between(zonedDateTime, nextFullMoon.plusDays(1)) + " days", false)
                .build();

        return embed;
    }
}
