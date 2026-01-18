package org.nasabot.nasabot.commands;

import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.nasabot.nasabot.managers.ButtonManager;
import org.nasabot.nasabot.objects.NASAImage;
import org.nasabot.nasabot.timers.NASAImageTimerTask;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.stream.Collectors;

public class ImageSearchSlashCommand extends NASABotSlashCommand {
    private final ButtonManager buttonManager = ButtonManager.getInstance();

    public ImageSearchSlashCommand() {
        super("image", "⭐ Search for images from the NASA Image Archive.", List.of(
                new OptionData(OptionType.STRING, "search", "Image keywords to search for.").setRequired(true),
                new OptionData(OptionType.INTEGER, "page", "Results page to search within (default 1).").setRequiredRange(1, 100).setRequired(false)),
                true, false);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        if (slashCommandEvent.getOptions().isEmpty()) {
            slashCommandEvent.reply("Missing search term, please retry.").queue();
            return;
        }

        slashCommandEvent.deferReply().queue();
        try {
            int pageNumber = 1;
            if (slashCommandEvent.getOption("page") != null) {
                pageNumber = Objects.requireNonNull(slashCommandEvent.getOption("page")).getAsInt();
            }

            List<NASAImage> images = selectOptions(nasaClient.getNASAImages(Objects.requireNonNull(slashCommandEvent.getOption("search")).getAsString(), pageNumber));
            if (images.isEmpty()) {
                slashCommandEvent.getHook().sendMessage("No results found. Please try a different search term or decrease your page number.").queue();
                return;
            }
            List<Pair<Button, NASAImage>> buttonOptions = getButtonsForOptions(images);
            MessageEmbed embed = formatImageOptions(buttonOptions);
            slashCommandEvent.getHook().sendMessageEmbeds(embed)
                    .addComponents(ActionRow.of(buttonOptions.stream()
                            .map(Pair::getFirst)
                            .collect(Collectors.toList())))
                    .queue(s -> {
                        String messageId = s.getId();
                        for (Pair<Button, NASAImage> pair : buttonOptions) {
                            buttonManager.addButtonToMap(pair.getFirst().getCustomId(), new Pair<>(slashCommandEvent.getUser().getId(), pair.getSecond()));
                        }
                        buttonManager.addButtonsToMap(messageId, buttonOptions.stream()
                                .map(pair -> pair.getFirst().getCustomId())
                                .collect(Collectors.toList()));

                        // Schedule task to delete message in 5 minutes
                        try {
                            TimerTask timerTask = new NASAImageTimerTask(Objects.requireNonNull(slashCommandEvent.getGuild()).getId(), slashCommandEvent.getChannelId(), messageId);
                            Timer timer = new Timer(true);
                            timer.schedule(timerTask, Date.from(Instant.now().plusSeconds(300)));
                        } catch (NullPointerException e) {
                            errorLoggingClient.handleError("ImageSearchSlashCommand", "execute", "Unable to find Guild ID for deletion Timer Task.", e);
                        }
                    });
        } catch (NullPointerException e) {
            errorLoggingClient.handleError("ImageSearchSlashCommand", "execute", "Uncaught NPE.", e);
            slashCommandEvent.getHook().sendMessage("Missing search term, please retry.").queue();
        }
    }

    private List<NASAImage> selectOptions(List<NASAImage> images) {
        if (images.isEmpty()) {
            return Collections.emptyList();
        }
        Collections.shuffle(images);
        return images.subList(0, Math.min(3, images.size()));
    }

    private List<Pair<Button, NASAImage>> getButtonsForOptions(List<NASAImage> images) {
        List<Pair<Button, NASAImage>> buttons = new ArrayList<>();
        int i = 1;
        for (NASAImage image : images) {
            buttons.add(new Pair<>(Button.success("IMAGESEARCH:" + UUID.randomUUID(), "Image " + i), image));
            i++;
        }
        return buttons;
    }

    private MessageEmbed formatImageOptions(List<Pair<Button, NASAImage>> imageOptions) {
        try {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(new Color(192, 32, 232))
                    .setTitle("Image Results")
                    .setDescription("Select an image from the options below.");
            int i = 1;
            for (Pair<Button, NASAImage> pair : imageOptions) {
                NASAImage image = pair.getSecond();
                embedBuilder.addField("Option " + i + " - " + image.getTitle(), image.getDescription().length() > 300
                        ? image.getDescription().substring(0, 300) + "..."
                        : image.getDescription(), false);
                i++;
            }
            embedBuilder.addField("NOTE", "This message will expire in 5 minutes if no option is selected.", false);
            return embedBuilder.build();
        } catch (Exception e) {
            errorLoggingClient.handleError("NASAClient", "formatImageOptions", "Cannot format image options.", e);
            return new EmbedBuilder().setTitle("NASA Image").addField("ERROR", "Unable to format the NASA images.", false).setColor(Color.RED).build();
        }
    }
}
