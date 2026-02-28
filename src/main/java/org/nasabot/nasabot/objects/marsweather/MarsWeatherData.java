package org.nasabot.nasabot.objects.marsweather;

import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MarsWeatherData {
    private static final Emoji sunEmoji = Emoji.fromUnicode("U+1F506");

    private final Map<String, Sol> sols;

    public MarsWeatherData(Map<String, Sol> sols) {
        this.sols = sols;
    }

    public Map<String, Sol> getSols() {
        return sols;
    }

    public Container renderContainer() {
        List<Section> sections = this.getSols().entrySet().stream()
                .map(s -> Section.of(
                        Button.primary(makeSolButtonID(s.getKey()), "View"),
                        TextDisplay.of(String.format("%s  **%s (%s - %s)**",
                                sunEmoji.getFormatted(),
                                "Sol " + s.getKey(),
                                s.getValue().getFirstUTCFormatted(),
                                s.getValue().getLastUTCFormatted()))
                )).collect(Collectors.toList());

        List<ContainerChildComponent> children = new ArrayList<>();
        children.add(MediaGallery.of(
                MediaGalleryItem.fromFile(FileUpload.fromData(new File("MarsInsightLanderBanner.png")))
        ));
        children.add(TextDisplay.of(String.format("# %s Mars InSight Lander Weather Data", Emoji.fromUnicode("U+1F4E1").getFormatted())));
        children.add(TextDisplay.of("### Sol data from the last week:"));
        children.addAll(sections);
        children.add(MediaGallery.of(
                MediaGalleryItem.fromFile(FileUpload.fromData(new File("MarsInsightLanderBanner.png")))
        ));

        return Container.of(children).withAccentColor(Color.ORANGE);
    }

    private String makeSolButtonID(String solId) {
        return "MARSWEATHER:" + solId;
    }
}
