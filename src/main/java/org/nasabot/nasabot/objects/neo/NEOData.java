package org.nasabot.nasabot.objects.neo;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
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

public class NEOData {
    private final Map<String, NEO> neosById;
    private final int pageNumber;
    private final int pageSize;
    private final int totalPages;
    private final long totalElements;

    public NEOData(Map<String, NEO> neosById, int pageNumber, int pageSize, int totalPages, long totalElements) {
        this.neosById = neosById;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public Map<String, NEO> getNeos() {
        return neosById;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public Container renderContainer() {
        String comet = Emoji.fromUnicode("U+2604").getFormatted();

        List<ContainerChildComponent> children = new ArrayList<>();
        children.add(TextDisplay.of(String.format("# %s Near Earth Objects (NEOs)", comet)));
        children.add(TextDisplay.of(String.format("### Page %d of %d (Total: %d NEOs) — Select one to view details:",
                pageNumber + 1, totalPages, totalElements)));

        for (NEO neo : neosById.values()) {
            children.add(Section.of(
                    Button.primary(makeNeoButtonID(pageNumber, neo.getId()), "View"),
                    TextDisplay.of(String.format("**%s**\nMagnitude: %.1f | Hazardous: %s",
                            trim(neo.getName()), neo.getAbsoluteMagnitude(), neo.isPotentiallyHazardous() ? "Yes" : "No"))
            ));
        }

        List<Button> navButtons = new ArrayList<>();
        Button prevButton = Button.primary("NEO:PAGE:" + (pageNumber - 1), "Previous");
        if (pageNumber <= 0) {
            prevButton = prevButton.asDisabled();
        }
        navButtons.add(prevButton);

        Button nextButton = Button.primary("NEO:PAGE:" + (pageNumber + 1), "Next");
        if (pageNumber >= totalPages - 1) {
            nextButton = nextButton.asDisabled();
        }
        navButtons.add(nextButton);

        children.add(ActionRow.of(navButtons));

        return Container.of(children).withAccentColor(new Color(128, 0, 128));
    }

    private String trim(String s) {
        return s.length() > 80 ? s.substring(0, 80 - 3) + "..." : s;
    }

    private String makeNeoButtonID(int page, String id) {
        return "NEO:VIEW:" + page + ":" + id;
    }
}
