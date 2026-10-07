package org.nasabot.nasabot.objects.epic;

import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EPICData {
    private final Map<String, EPICImage> imagesById;
    private final String collection;
    private final String date;

    public EPICData(Map<String, EPICImage> imagesById, String collection, String date) {
        this.imagesById = imagesById;
        this.collection = collection;
        this.date = date;
    }

    public Map<String, EPICImage> getImages() {
        return imagesById;
    }

    public String getCollection() {
        return collection;
    }

    public String getDate() {
        return date;
    }

    public Container renderContainer() {
        String globe = Emoji.fromUnicode("U+1F30D").getFormatted();

        List<ContainerChildComponent> children = new ArrayList<>();
        children.add(TextDisplay.of(String.format("# %s EPIC %s Images", globe,
                collection.substring(0, 1).toUpperCase() + collection.substring(1))));
        children.add(TextDisplay.of("### Select an image to view details:"));

        for (EPICImage image : imagesById.values()) {
            children.add(Section.of(
                    Button.primary(makeEPICButtonID(image.getIdentifier()), "View"),
                    TextDisplay.of(String.format("**%s**\n%s", trim(image.getImageName()), image.getPrettyDate()))
            ));
        }

        return Container.of(children).withAccentColor(new Color(0, 100, 200));
    }

    private String trim(String s) {
        return s.length() > 80 ? s.substring(0, 80 - 3) + "..." : s;
    }

    private String makeEPICButtonID(String id) {
        return "EPIC:" + collection + ":" + date + ":" + id;
    }
}
