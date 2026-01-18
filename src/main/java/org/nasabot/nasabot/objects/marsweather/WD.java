package org.nasabot.nasabot.objects.marsweather;

import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.List;

public class WD {
    private final List<WindDirection> windDirections;
    private final WindDirection mostCommon;

    public WD(List<WindDirection> windDirections, WindDirection mostCommon) {
        this.windDirections = windDirections;
        this.mostCommon = mostCommon;
    }

    public List<WindDirection> getWindDirections() {
        return windDirections;
    }

    public WindDirection getMostCommon() {
        return mostCommon;
    }

    public List<TextDisplay> renderSections() {
        if (mostCommon != null) {
            return List.of(
                    TextDisplay.of(String.format("## %s Wind Direction Data", Emoji.fromUnicode("U+1F9ED").getFormatted())),
                    TextDisplay.of(String.format("**Compass Point:** %s", mostCommon.getPoint())),
                    TextDisplay.of(String.format("**Compass Degrees:** %s", mostCommon.getDegrees())),
                    TextDisplay.of(String.format("**Compass Right (horizontal):** %s", mostCommon.getRight())),
                    TextDisplay.of(String.format("**Compass Up:** %s", mostCommon.getUp()))
            );
        } else {
            WindDirection windDirection = windDirections.get(0);
            return List.of(
                    TextDisplay.of(String.format("## %s Wind Direction Data", Emoji.fromUnicode("U+1F9ED").getFormatted())),
                    TextDisplay.of(String.format("**Compass Point:** %s", windDirection.getPoint())),
                    TextDisplay.of(String.format("**Compass Degrees:** %s", windDirection.getDegrees())),
                    TextDisplay.of(String.format("**Compass Right (horizontal):** %s", windDirection.getRight())),
                    TextDisplay.of(String.format("**Compass Up:** %s", windDirection.getUp()))
            );
        }
    }
}
