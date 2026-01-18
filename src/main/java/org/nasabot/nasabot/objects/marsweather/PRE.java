package org.nasabot.nasabot.objects.marsweather;

import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.List;

public class PRE extends NumericReading {

    public PRE(String units, double average, double count, double max, double min) {
        super(units, average, count, max, min);
    }

    public List<TextDisplay> renderSections() {
        return List.of(
                TextDisplay.of(String.format("## %s Pressure Data", Emoji.fromUnicode("U+1F53B").getFormatted())),
                TextDisplay.of(String.format("**High:** %s %s", max, units)),
                TextDisplay.of(String.format("**Low:** %s %s", min, units)),
                TextDisplay.of(String.format("**Average:** %s %s", average, units))
        );
    }
}
