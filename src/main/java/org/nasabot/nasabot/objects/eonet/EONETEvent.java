package org.nasabot.nasabot.objects.eonet;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EONETEvent {
    private static final SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy '@' HH:mm:ss");

    private final String id;
    private final String title;
    private final String date;
    private final List<String> categories;

    public EONETEvent(String id, String title, String date, List<String> categories) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getCategories() {
        return categories;
    }

    public String getPrettyDate() {
        try {
            if (date == null) return "Unknown Date";
            Date dt = Date.from(Instant.parse(date));
            return outputDateFormat.format(dt);
        } catch (Exception e) {
            return date != null ? date : "Unknown Date";
        }
    }

    public Container renderContainer() {
        String category = categories.get(0);
        String emoji;
        switch (category) {
            case "Drought":
                emoji = Emoji.fromUnicode("U+1F3DC").getFormatted();
                break;
            case "Dust and Haze":
                emoji = Emoji.fromUnicode("U+1F32B").getFormatted();
                break;
            case "Earthquakes":
                emoji = Emoji.fromUnicode("U+1FAE8").getFormatted();
                break;
            case "Floods":
                emoji = Emoji.fromUnicode("U+1F30A").getFormatted();
                break;
            case "Landslides":
                emoji = Emoji.fromUnicode("U+26F0").getFormatted();
                break;
            case "Manmade":
                emoji = Emoji.fromUnicode("U+1F9CD").getFormatted();
                break;
            case "Sea and Lake Ice":
                emoji = Emoji.fromUnicode("U+1F9CA").getFormatted();
                break;
            case "Severe Storms":
                emoji = Emoji.fromUnicode("U+1F32A").getFormatted();
                break;
            case "Snow":
                emoji = Emoji.fromUnicode("U+2744").getFormatted();
                break;
            case "Temperature Extremes":
                emoji = Emoji.fromUnicode("U+1F321").getFormatted();
                break;
            case "Volcanoes":
                emoji = Emoji.fromUnicode("U+1F30B").getFormatted();
                break;
            case "Water Color":
                emoji = Emoji.fromUnicode("U+1F4A7").getFormatted();
                break;
            case "Wildfires":
                emoji = Emoji.fromUnicode("U+1F525").getFormatted();
                break;
            default:
                emoji = Emoji.fromUnicode("U+2754").getFormatted();
        }

        List<ContainerChildComponent> children = new ArrayList<>();
        children.add(TextDisplay.of(String.format("# %s", trim(title))));
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        children.add(TextDisplay.of("# " + emoji));
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        children.add(TextDisplay.of("### Date"));
        children.add(TextDisplay.of(getPrettyDate()));

        if (!categories.isEmpty()) {
            children.add(Separator.create(true, Separator.Spacing.LARGE));
            children.add(TextDisplay.of("### Categories"));
            children.add(TextDisplay.of(String.join(", ", categories)));
        }

        children.add(Separator.create(true, Separator.Spacing.LARGE));
        children.add(ActionRow.of(Button.success("EONET:HOME", "Back to Events")));

        return Container.of(children).withAccentColor(new Color(0, 162, 232));
    }

    private String trim(String s) {
        return s.length() > 80 ? s.substring(0, 80 - 3) + "..." : s;
    }
}
