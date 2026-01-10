package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class APODSlashCommand extends NASABotSlashCommand {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public APODSlashCommand() {
        super("apod", "Displays the current Astronomy Picture of the Day, or one from the specified date.",
                Collections.singletonList(new OptionData(OptionType.STRING, "date", "[yyyy-mm-dd] Date of the APOD to retrieve.").setRequired(false)));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        slashCommandEvent.reply("APOD is currently down due to a NASA service outage. Please check https://api.nasa.gov/ for updates. The APOD command will be re-enabled once service is restored.").queue();
        return;

        /*
        slashCommandEvent.deferReply().queue();

        if (slashCommandEvent.getOption("date") == null) {
            EmbedBuilder embedBuilder = nasaClient.getLatestPictureOfTheDay();
            InputStream file;
            Optional<MessageEmbed.Field> imageUrl = embedBuilder.getFields().stream()
                    .filter(field -> field.getName() != null)
                    .filter(field -> field.getName().equals("HD Image Link"))
                    .findFirst();
            if (imageUrl.isPresent()) {
                try {
                    file = new URL(Objects.requireNonNull(imageUrl.get().getValue())).openStream();
                    embedBuilder.setImage("attachment://image.png");
                    slashCommandEvent.getHook().sendFiles(FileUpload.fromData(file, "image.png")).setEmbeds(embedBuilder.build()).queue();
                } catch (NullPointerException | IOException e) {
                    errorLoggingClient.handleError("APODSlashCommand", "execute", "Unable to format embed.", e);
                    slashCommandEvent.getHook().sendMessageEmbeds(new EmbedBuilder().setTitle("Picture of the Day").addField("ERROR", "Unable to obtain Picture of the Day.", false).setColor(Color.RED).build()).queue();
                }
            } else {
                slashCommandEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
            }
        } else {
            try {
                if (simpleDateFormat.parse(Objects.requireNonNull(slashCommandEvent.getOption("date")).getAsString()) != null) {
                    EmbedBuilder embedBuilder = nasaClient.getPictureOfTheDay(Objects.requireNonNull(slashCommandEvent.getOption("date")).getAsString());
                    InputStream file;
                    Optional<MessageEmbed.Field> imageUrl = embedBuilder.getFields().stream()
                            .filter(field -> field.getName() != null)
                            .filter(field -> field.getName().equals("HD Image Link"))
                            .findFirst();
                    if (imageUrl.isPresent()) {
                        try {
                            file = new URL(Objects.requireNonNull(imageUrl.get().getValue())).openStream();
                            embedBuilder.setImage("attachment://image.png");
                            slashCommandEvent.getHook().sendFiles(FileUpload.fromData(file, "image.png")).setEmbeds(embedBuilder.build()).queue();
                        } catch (NullPointerException | IOException e) {
                            errorLoggingClient.handleError("APODSlashCommand", "execute", "Unable to format embed.", e);
                            slashCommandEvent.getHook().sendMessageEmbeds(new EmbedBuilder().setTitle("Picture of the Day").addField("ERROR", "Unable to obtain Picture of the Day.", false).setColor(Color.RED).build()).queue();
                        }
                    } else {
                        slashCommandEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
                    }
                } else {
                    slashCommandEvent.getHook().sendMessage(String.format("Unable to get APOD, please check your formatting: %s", this.getArgumentsString())).queue();
                }
            } catch (ParseException e) {
                errorLoggingClient.handleError("APODSlashCommand", "execute", String.format("Unable to parse date: %s", slashCommandEvent.getOption("date")), e);
                slashCommandEvent.getHook().sendMessage(String.format("Unable to get APOD, please check your formatting: %s", this.getArgumentsString())).queue();
            }
        }

         */
    }
}
