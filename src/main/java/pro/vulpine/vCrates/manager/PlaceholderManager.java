package pro.vulpine.vCrates.manager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.instance.Profile;
import pro.vulpine.vCrates.instance.StatisticEntry;
import pro.vulpine.vCrates.utils.logger.Logger;

import java.util.Optional;

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
            return "0";
        }

        String[] parts = identifier.split("_", 2);

        if (parts.length != 2) {
            return null;
        }

        String type = parts[0];
        String statisticIdentifier = parts[1];

        Optional<StatisticEntry> statisticEntry = profile.getStatistics().stream()
                .filter(s -> s.getType().equalsIgnoreCase(type) && s.getIdentifier().equalsIgnoreCase(statisticIdentifier))
                .findFirst();

        return statisticEntry.map(entry -> String.valueOf(entry.getValue())).orElse("0");

    }
}
