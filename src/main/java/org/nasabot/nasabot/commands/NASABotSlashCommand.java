package org.nasabot.nasabot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.nasabot.nasabot.clients.DBClient;
import org.nasabot.nasabot.clients.ErrorLoggingClient;
import org.nasabot.nasabot.clients.NASAClient;
import org.nasabot.nasabot.managers.EntitlementManager;

import java.util.List;

public abstract class NASABotSlashCommand {
    protected final DBClient dbClient = DBClient.getInstance();
    protected final ErrorLoggingClient errorLoggingClient = ErrorLoggingClient.getInstance();
    protected final NASAClient nasaClient = NASAClient.getInstance();
    protected final EntitlementManager entitlementManager = EntitlementManager.getInstance();
    private final String name;
    private final String description;
    private final List<OptionData> optionData;
    private final boolean isOwnerCommand;
    private final boolean isPremium;
    private final boolean isGuildPremiumOnly;

    public NASABotSlashCommand(String name, String description, List<OptionData> optionData) {
        this.name = name;
        this.description = description;
        this.optionData = optionData;
        this.isOwnerCommand = false;
        this.isPremium = false;
        this.isGuildPremiumOnly = false;
    }

    public NASABotSlashCommand(String name, String description, List<OptionData> optionData, boolean isPremium, boolean isGuildPremiumOnly) {
        this.name = name;
        this.description = description;
        this.optionData = optionData;
        this.isOwnerCommand = false;
        this.isPremium = isPremium;
        this.isGuildPremiumOnly = isGuildPremiumOnly;
    }

    public NASABotSlashCommand(String name, String description, List<OptionData> optionData, boolean isOwnerCommand) {
        this.name = name;
        this.description = description;
        this.optionData = optionData;
        this.isOwnerCommand = isOwnerCommand;
        this.isPremium = false;
        this.isGuildPremiumOnly = false;
    }

    public abstract void execute(@NotNull SlashCommandInteractionEvent slashCommandEvent);

    public boolean isAuthorized(@NotNull SlashCommandInteractionEvent slashCommandEvent, boolean guildOnly) {
        return true;
        /*
        if (guildOnly) {
            return entitlementManager.isGuildEntitled(slashCommandEvent);
        } else {
            return entitlementManager.isGuildEntitled(slashCommandEvent) || entitlementManager.isUserEntitled(slashCommandEvent);
        }
         */
    }

    public String getArgumentsString() {
        /*
            <name> - <description>
                <argument 1> - (Required)
                <argument 2> - (Optional)
         */
        StringBuilder stringBuilder = new StringBuilder(String.format("\n```/%s - %s", name, description));

        for (OptionData option : optionData) {
            stringBuilder.append(String.format("\n\t%s - (%s)", option.getName(), option.isRequired() ? "Required" : "Optional"));
        }

        stringBuilder.append("```");

        return stringBuilder.toString();
    }

    public CommandData getCommandData() {
        return Commands.slash(name, description)
                .addOptions(optionData)
                .setContexts(InteractionContextType.GUILD);
    }

    public void insertCommand(SlashCommandInteractionEvent slashCommandEvent) {
        dbClient.insertCommand(slashCommandEvent, name);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<OptionData> getOptionData() {
        return optionData;
    }

    public boolean isOwnerCommand() {
        return isOwnerCommand;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public boolean isGuildPremiumOnly() {
        return isGuildPremiumOnly;
    }

    @Override
    public String toString() {
        JSONObject commandJSON = new JSONObject()
                .put("name", name)
                .put("description", description)
                .put("isOwnerCommand", isOwnerCommand);
        JSONObject options = new JSONObject();
        optionData.forEach(option -> {
            options.put(option.getName(), new JSONObject()
                    .put("optionType", option.getType().toString())
                    .put("description", option.getDescription())
                    .put("isRequired", option.isRequired()));
        });
        commandJSON.put("optionData", options);
        return commandJSON.toString(4);
    }
}
