package org.nasabot.nasabot.objects.marsweather;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.Color;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Sol {
    private static final SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy '@' HH:mm:ss");
    private static final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

    private final String name;
    private final AT temperature;
    private final HWS windSpeed;
    private final PRE pressure;
    private final WD windDirection;
    private final String season;
    private final String firstUTC;
    private final String lastUTC;

    public Sol(String name, AT temperature, HWS windSpeed, PRE pressure, WD windDirection, String season, String firstUTC, String lastUTC) {
        this.name = name;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.pressure = pressure;
        this.windDirection = windDirection;
        this.season = season;
        this.firstUTC = firstUTC;
        this.lastUTC = lastUTC;
    }

    public String getName() {
        return name;
    }

    public AT getTemperature() {
        return temperature;
    }

    public HWS getWindSpeed() {
        return windSpeed;
    }

    public PRE getPressure() {
        return pressure;
    }

    public WD getWindDirection() {
        return windDirection;
    }

    public String getSeason() {
        return season;
    }

    public String getFirstUTC() {
        return firstUTC;
    }

    public String getLastUTC() {
        return lastUTC;
    }

    public String getFirstUTCFormatted() {
        try {
            return outputDateFormat.format(inputDateFormat.parse(firstUTC)) + " UTC";
        } catch (ParseException e) {
            return firstUTC;
        }
    }

    public String getLastUTCFormatted() {
        try {
            return outputDateFormat.format(inputDateFormat.parse(lastUTC)) + " UTC";
        } catch (ParseException e) {
            return lastUTC;
        }
    }

    public Container renderContainer() {
        List<ContainerChildComponent> children = new ArrayList<>();
        children.add(Section.of(
                Thumbnail.fromFile(FileUpload.fromData(new File("SunImage.jpg"))),
                TextDisplay.of(String.format("# %s Sol %s\n%s - %s", Emoji.fromUnicode("U+1F4E1").getFormatted(), name, getFirstUTCFormatted(), getLastUTCFormatted()))
        ));
        children.add(Separator.create(true, Separator.Spacing.LARGE));
        children.addAll(temperature.renderSections());
        children.add(Separator.create(true, Separator.Spacing.LARGE));
        children.addAll(windSpeed.renderSections());
        children.add(Separator.create(true, Separator.Spacing.LARGE));
        children.addAll(windDirection.renderSections());
        children.add(Separator.create(true, Separator.Spacing.LARGE));
        children.addAll(pressure.renderSections());
        children.add(Separator.create(true, Separator.Spacing.LARGE));
        children.add(ActionRow.of(Button.success("MARSWEATHER:HOME", "Back to Sols")));

        return Container.of(children).withAccentColor(Color.ORANGE);
    }
}
