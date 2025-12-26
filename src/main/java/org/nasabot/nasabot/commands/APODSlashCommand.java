package org.nasabot.nasabot.commands;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.nasabot.nasabot.NASABot;
import org.nasabot.nasabot.utils.ErrorLogging;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class APODSlashCommand extends NASASlashCommand {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public APODSlashCommand() {
        super();
        this.name = "apod";
        this.help = "Displays the Picture of the Day from yesterday, or the specified date.";
        this.arguments = "[date (yyyy-mm-dd)]";
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "date", "[yyyy-mm-dd] Date of the APOD to retrieve.").setRequired(false));
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        this.insertCommand(slashCommandEvent);

        slashCommandEvent.deferReply().queue();

        if (!slashCommandEvent.hasOption("date")) {
            EmbedBuilder embedBuilder = NASABot.NASAClient.getPictureOfTheDay(simpleDateFormat.format(System.currentTimeMillis() - 86400000));
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
                    ErrorLogging.handleError("APODSlashCommand", "execute", "Unable to format embed.", e);
                    slashCommandEvent.getHook().sendMessageEmbeds(new EmbedBuilder().setTitle("Picture of the Day").addField("ERROR", "Unable to obtain Picture of the Day.", false).setColor(Color.RED).build()).queue();
                }
            } else {
                slashCommandEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
            }
        } else {
            try {
                if (simpleDateFormat.parse(Objects.requireNonNull(slashCommandEvent.getOption("date")).getAsString()) != null) {
                    EmbedBuilder embedBuilder = NASABot.NASAClient.getPictureOfTheDay(Objects.requireNonNull(slashCommandEvent.getOption("date")).getAsString());
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
                            ErrorLogging.handleError("APODSlashCommand", "execute", "Unable to format embed.", e);
                            slashCommandEvent.getHook().sendMessageEmbeds(new EmbedBuilder().setTitle("Picture of the Day").addField("ERROR", "Unable to obtain Picture of the Day.", false).setColor(Color.RED).build()).queue();
                        }
                    } else {
                        slashCommandEvent.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
                    }
                } else {
                    slashCommandEvent.getHook().sendMessage(String.format("Unable to get APOD, please check your formatting: %s", this.getArgumentsString())).queue();
                }
            } catch (ParseException e) {
                ErrorLogging.handleError("APODSlashCommand", "execute", String.format("Unable to parse date: %s", slashCommandEvent.getOption("date")), e);
                slashCommandEvent.getHook().sendMessage(String.format("Unable to get APOD, please check your formatting: %s", this.getArgumentsString())).queue();
            }
        }
    }
}
