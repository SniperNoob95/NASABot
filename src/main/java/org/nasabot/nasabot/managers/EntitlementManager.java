package org.nasabot.nasabot.managers;

import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import org.nasabot.nasabot.NASABot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EntitlementManager {

    private EntitlementManager() {
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

    public boolean isGuildEntitled(ComponentInteraction interaction) {
        return interaction.getEntitlements().stream()
                .filter(e -> e.getApplicationId().equals("748775876077813881"))
                .anyMatch(e -> e.getGuildId() != null);
    }

    public boolean isUserEntitled(ComponentInteraction interaction) {
        return interaction.getEntitlements().stream()
                .filter(e -> e.getApplicationId().equals("748775876077813881"))
                .anyMatch(e -> e.getGuildId() == null);
    }
}
