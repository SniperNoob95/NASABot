package org.nasabot.nasabot.objects.eonet;

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

public class EONETEventsData {
    private final Map<String, EONETEvent> eventsById;

    public EONETEventsData(Map<String, EONETEvent> eventsById) {
        this.eventsById = eventsById;
    }

    public Map<String, EONETEvent> getEvents() {
        return eventsById;
    }

    public Container renderContainer() {
        List<ContainerChildComponent> children = new ArrayList<>();

        children.add(TextDisplay.of(String.format("# %s EONET Recent Events", Emoji.fromUnicode("U+1F30F").getFormatted())));
        children.add(TextDisplay.of("### Select an event to view details:"));

        for (EONETEvent event : eventsById.values()) {
            children.add(Section.of(
                    Button.primary(makeEventButtonID(event.getId()), "View"),
                    TextDisplay.of(String.format("**%s**\n%s", trim(event.getTitle()), event.getPrettyDate()))
            ));
        }

        return Container.of(children).withAccentColor(new Color(0, 162, 232));
    }

    private String trim(String s) {
        return s.length() > 80 ? s.substring(0, 80 - 3) + "..." : s;
    }

    private String makeEventButtonID(String id) {
        return "EONET:" + id;
    }
}
