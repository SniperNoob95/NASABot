package org.nasabot.nasabot.managers;

import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.nasabot.nasabot.NASABot;
import org.nasabot.nasabot.clients.ErrorLoggingClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class EntitlementManager {
    private final ErrorLoggingClient errorLoggingClient = ErrorLoggingClient.getInstance();
    private List<String> whitelistServers;
    private List<String> whitelistUsers;

    private EntitlementManager() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

        try {
            String serverList = resourceBundle.getString("whitelistServers");
            whitelistServers = Arrays.asList(serverList.split(","));
            String userList = resourceBundle.getString("whitelistUsers");
            whitelistUsers = Arrays.asList(userList.split(","));
        } catch (Exception e) {
            errorLoggingClient.handleError("EntitlementManager", "EntitlementManager", "Cannot parse properties.", e);
            System.exit(0);
        }
    }

    private static class EntitlementManagerSingleton {
        private static final EntitlementManager INSTANCE = new EntitlementManager();
    }

    public static EntitlementManager getInstance() {
        return EntitlementManager.EntitlementManagerSingleton.INSTANCE;
    }

    public void getActiveGuildEntitlements(Consumer<List<Entitlement>> callback) {
        List<Entitlement> entitlements = new ArrayList<>();
        NASABot.shardManager.getShards().get(0).retrieveEntitlements().cache(false).excludeEnded(true)
                .forEachAsync(entitlement -> {
                    if (entitlement.getGuildId() != null) {
                        entitlements.add(entitlement);
                    }
                    // Return true to continue fetching, false to stop
                    return true;
                })
                .thenRun(() -> {
                    // This runs after all entitlements are retrieved or the predicate returns false
                    callback.accept(entitlements);
                });
    }

    public boolean isGuildEntitled(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        try {
            if (whitelistServers.contains(Objects.requireNonNull(slashCommandInteractionEvent.getGuild()).getId())) {
                return true;
            }
        } catch (NullPointerException e) {
            errorLoggingClient.handleError("EntitlementManager", "isGuildEntitled", "Could not retrieve Guild from interaction.", e.getClass().getName());
        }
        return slashCommandInteractionEvent.getEntitlements().stream()
                .filter(e -> e.getApplicationId().equals("748775876077813881"))
                .anyMatch(e -> e.getGuildId() != null);
    }

    public boolean isUserEntitled(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        if (whitelistUsers.contains(slashCommandInteractionEvent.getUser().getId())) {
            return true;
        }
        return slashCommandInteractionEvent.getEntitlements().stream()
                .filter(e -> e.getApplicationId().equals("748775876077813881"))
                .anyMatch(e -> e.getGuildId() == null);
    }

    public boolean isWhitelistedGuild(String guildId) {
        return whitelistServers.contains(guildId);
    }

    public boolean isWhitelistedUser(String userId) {
        return whitelistUsers.contains(userId);
    }
}
