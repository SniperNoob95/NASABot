package org.nasabot.nasabot.objects.epic;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EPICImage {
    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy '@' HH:mm:ss");

    private final String identifier;
    private final String caption;
    private final String imageName;
    private final String date;
    private final double centroidLat;
    private final double centroidLon;
    private final String imageUrl;

    public EPICImage(String identifier, String caption, String imageName, String date,
                     double centroidLat, double centroidLon, String imageUrl) {
        this.identifier = identifier;
        this.caption = caption;
        this.imageName = imageName;
        this.date = date;
        this.centroidLat = centroidLat;
        this.centroidLon = centroidLon;
        this.imageUrl = imageUrl;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getImageName() {
        return imageName;
    }

    public String getPrettyDate() {
        try {
            if (date == null) return "Unknown Date";
            Date dt = INPUT_DATE_FORMAT.parse(date);
            return OUTPUT_DATE_FORMAT.format(dt);
        } catch (Exception e) {
            return date != null ? date : "Unknown Date";
        }
    }

    public Container renderContainer(String collection, String queryDate) {
        String globe = Emoji.fromUnicode("U+1F30D").getFormatted();

        List<ContainerChildComponent> children = new ArrayList<>();
        children.add(TextDisplay.of(String.format("# %s EPIC Image %s", globe, identifier)));
        children.add(TextDisplay.of("### Date"));
        children.add(TextDisplay.of(getPrettyDate()));
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        children.add(MediaGallery.of(MediaGalleryItem.fromUrl(imageUrl)));
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        children.add(TextDisplay.of("### Collection"));
        children.add(TextDisplay.of(collection.substring(0, 1).toUpperCase() + collection.substring(1)));
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        children.add(TextDisplay.of("### Centroid Coordinates"));
        children.add(TextDisplay.of(String.format("Lat: %.4f, Lon: %.4f", centroidLat, centroidLon)));
        children.add(Separator.create(true, Separator.Spacing.LARGE));

        children.add(ActionRow.of(Button.success("EPIC:HOME:" + collection + ":" + queryDate, "Back to Images")));

        return Container.of(children).withAccentColor(new Color(0, 100, 200));
    }
}
