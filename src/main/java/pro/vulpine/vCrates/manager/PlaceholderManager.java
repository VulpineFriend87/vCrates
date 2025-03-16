package pro.vulpine.vCrates.manager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.instance.Key;
import pro.vulpine.vCrates.instance.Profile;
import pro.vulpine.vCrates.instance.crate.Crate;
import pro.vulpine.vCrates.utils.logger.Logger;

import java.util.concurrent.CompletableFuture;

public class PlaceholderManager extends PlaceholderExpansion {

    private final VCrates plugin;

    public PlaceholderManager(VCrates plugin) {
        this.plugin = plugin;

        Logger.info("Loaded PlaceholderAPI expansion successfully!", "PlaceholderManager");
    }

    @Override
    public @NotNull String getIdentifier() {
        return "vCrates";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String onPlaceholderRequest(Player player, @NotNull String identifier) {

        if (player == null) {
            return null;
        }

        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (profile == null) {
            return null;
        }

        if (identifier.equals("total_crates_opened")) {

            return String.valueOf(profile.getTotalStatistic("crates-opened"));

        }

        if (identifier.equals("total_keys_used")) {

            return String.valueOf(profile.getTotalStatistic("keys-used"));

        }

        if (identifier.startsWith("crates_opened_")) {

            String crateIdentifier = identifier.replace("crates_opened_", "");
            return String.valueOf(profile.getStatistic("crates-opened", crateIdentifier));

        }

        if (identifier.equals("most_opened_crate")) {

            String crateIdentifier = profile.getTopStatisticIdentifier("crates-opened");

            Crate crate = plugin.getCrateManager().getCrate(crateIdentifier);
            if (crate != null) {

                return crate.getName();

            }

            return crateIdentifier;

        }

        if (identifier.startsWith("keys_used_")) {

            String keyIdentifier = identifier.replace("keys_used_", "");
            return String.valueOf(profile.getStatistic("keys-used", keyIdentifier));

        }

        if (identifier.equals("most_used_key")) {

            String keyIdentifier = profile.getTopStatisticIdentifier("keys-used");

            Key key = plugin.getKeyManager().getKey(keyIdentifier);
            if (key != null) {

                return key.getName();

            }

            return keyIdentifier;

        }

        if (identifier.equals("server_total_crates_opened")) {

            CompletableFuture<Integer> totalCratesOpenedFuture = plugin.getProfileManager().getTotalStatistic("crates-opened");
            return totalCratesOpenedFuture.thenApply(String::valueOf).join();

        }

        if (identifier.equals("server_total_keys_used")) {

            CompletableFuture<Integer> totalKeysUsedFuture = plugin.getProfileManager().getTotalStatistic("keys-used");
            return totalKeysUsedFuture.thenApply(String::valueOf).join();

        }

        if (identifier.equals("server_most_opened_crate")) {

            CompletableFuture<String> mostOpenedCrateFuture = plugin.getProfileManager().getTopStatisticIdentifier("crates-opened");
            String crateIdentifier = mostOpenedCrateFuture.join();

            Crate crate = plugin.getCrateManager().getCrate(crateIdentifier);
            if (crate != null) {

                return crate.getName();

            }

            return crateIdentifier;

        }

        if (identifier.equals("server_most_used_key")) {

            CompletableFuture<String> mostUsedKeyFuture = plugin.getProfileManager().getTopStatisticIdentifier("keys-used");
            String keyIdentifier = mostUsedKeyFuture.join();

            Key key = plugin.getKeyManager().getKey(keyIdentifier);
            if (key != null) {

                return key.getName();

            }

            return keyIdentifier;

        }

        return null;

    }
}
