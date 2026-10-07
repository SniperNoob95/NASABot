package org.nasabot.nasabot.objects.neo;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class NEO {
    private final String id;
    private final String name;
    private final String nasaJplUrl;
    private final double absoluteMagnitude;
    private final double estimatedDiameterMinMeters;
    private final double estimatedDiameterMaxMeters;
    private final boolean isPotentiallyHazardous;
    private final List<String> closeApproachDates;
    private final OrbitalData orbitalData;

    public NEO(String id, String name, String nasaJplUrl, double absoluteMagnitude,
               double estimatedDiameterMinMeters, double estimatedDiameterMaxMeters,
               boolean isPotentiallyHazardous, List<String> closeApproachDates) {
        this(id, name, nasaJplUrl, absoluteMagnitude, estimatedDiameterMinMeters,
                estimatedDiameterMaxMeters, isPotentiallyHazardous, closeApproachDates, null);
    }

    public NEO(String id, String name, String nasaJplUrl, double absoluteMagnitude,
               double estimatedDiameterMinMeters, double estimatedDiameterMaxMeters,
               boolean isPotentiallyHazardous, List<String> closeApproachDates,
               OrbitalData orbitalData) {
        this.id = id;
        this.name = name;
        this.nasaJplUrl = nasaJplUrl;
        this.absoluteMagnitude = absoluteMagnitude;
        this.estimatedDiameterMinMeters = estimatedDiameterMinMeters;
        this.estimatedDiameterMaxMeters = estimatedDiameterMaxMeters;
        this.isPotentiallyHazardous = isPotentiallyHazardous;
        this.closeApproachDates = closeApproachDates;
        this.orbitalData = orbitalData;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNasaJplUrl() {
        return nasaJplUrl;
    }

    public double getAbsoluteMagnitude() {
        return absoluteMagnitude;
    }

    public double getEstimatedDiameterMinMeters() {
        return estimatedDiameterMinMeters;
    }

    public double getEstimatedDiameterMaxMeters() {
        return estimatedDiameterMaxMeters;
    }

    public boolean isPotentiallyHazardous() {
        return isPotentiallyHazardous;
    }

    public List<String> getCloseApproachDates() {
        return closeApproachDates;
    }

    public OrbitalData getOrbitalData() {
        return orbitalData;
    }

    public Container renderContainer(int returnPage) {
        String comet = Emoji.fromUnicode("U+2604").getFormatted();

        List<ContainerChildComponent> children = new ArrayList<>();
        children.add(TextDisplay.of(String.format("# %s %s", comet, name)));
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        children.add(TextDisplay.of("### Name"));
        children.add(TextDisplay.of(name != null ? name : "Unknown"));
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        children.add(TextDisplay.of("### Absolute Magnitude"));
        children.add(TextDisplay.of(String.valueOf(absoluteMagnitude)));
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        children.add(TextDisplay.of("### Estimated Diameter (Meters)"));
        children.add(TextDisplay.of(String.format("%.2f m - %.2f m", estimatedDiameterMinMeters, estimatedDiameterMaxMeters)));
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        children.add(TextDisplay.of("### Potentially Hazardous"));
        children.add(TextDisplay.of(isPotentiallyHazardous ? "Yes" : "No"));
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        children.add(TextDisplay.of("### Approach Dates"));
        if (closeApproachDates == null || closeApproachDates.isEmpty()) {
            children.add(TextDisplay.of("None"));
        } else {
            String datesJoined = String.join(", ", closeApproachDates);
            if (datesJoined.length() > 1000) {
                datesJoined = datesJoined.substring(0, 997) + "...";
            }
            children.add(TextDisplay.of(datesJoined));
        }
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.success("NEO:HOME:" + returnPage, "Back to List"));
        if (orbitalData != null) {
            buttons.add(Button.primary("NEO:ORBIT:" + returnPage + ":" + id, "Orbital Data"));
        }
        if (nasaJplUrl != null && !nasaJplUrl.isEmpty()) {
            buttons.add(Button.link(nasaJplUrl, "NASA JPL Page"));
        }
        children.add(ActionRow.of(buttons));

        return Container.of(children).withAccentColor(new Color(128, 0, 128));
    }
}
