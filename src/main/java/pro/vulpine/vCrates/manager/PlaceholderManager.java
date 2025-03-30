package pro.vulpine.vCrates.manager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.instance.Key;
import pro.vulpine.vCrates.instance.Profile;
import pro.vulpine.vCrates.instance.crate.Crate;
import pro.vulpine.vCrates.utils.logger.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlaceholderManager extends PlaceholderExpansion {

    private final VCrates plugin;

    private final Map<String, List<Map.Entry<UUID, Integer>>> leaderboardCache = new HashMap<>();
    private final Map<String, Long> cacheTimestamps = new HashMap<>();
    private final long cacheTimeout = 5000;

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

        if (identifier.startsWith("leaderboard_")) {

            return handleLeaderboardPlaceholder(identifier.substring("leaderboard_".length()));

        }

        return null;

    }

    private String handleLeaderboardPlaceholder(String identifier) {

        String[] parts = identifier.split("_");

        if (parts.length < 2) {

            return null;

        }

        String statType = parts[0] + "_" + parts[1];

        int rankIndex;

        String specificId = null;

        if (parts.length == 3) {

            try {

                rankIndex = Integer.parseInt(parts[2]) - 1;

            } catch (NumberFormatException e) {

                return null;

            }

        } else if (parts.length == 4) {

            specificId = parts[2];

            try {

                rankIndex = Integer.parseInt(parts[3]) - 1;

            } catch (NumberFormatException e) {

                return null;

            }

        } else {

            return null;

        }

        List<Map.Entry<UUID, Integer>> leaderboard;
        try {

            if (specificId != null) {

                leaderboard = getLeaderboard(statType.replace("_", "-"), specificId);

            } else {

                leaderboard = getLeaderboard(statType.replace("_", "-"));

            }

        } catch (Exception e) {

            Logger.error("Error retrieving leaderboard data: " + e.getMessage(), "PlaceholderManager");
            return plugin.getMainConfiguration().getString("placeholders.leaderboard.empty")
                    .replace("%rank%", String.valueOf(rankIndex + 1));

        }

        if (rankIndex < 0 || rankIndex >= leaderboard.size()) {

            return plugin.getMainConfiguration().getString("placeholders.leaderboard.empty")
                    .replace("%rank%", String.valueOf(rankIndex + 1));

        }

        Map.Entry<UUID, Integer> entry = leaderboard.get(rankIndex);
        UUID playerId = entry.getKey();
        int value = entry.getValue();

        String playerName;
        try {

            playerName = Bukkit.getOfflinePlayer(playerId).getName();
            if (playerName == null) {
                playerName = playerId.toString();
            }

        } catch (Exception e) {

            playerName = playerId.toString();

        }

        return plugin.getMainConfiguration().getString("placeholders.leaderboard.entry")
                .replace("%player%", playerName)
                .replace("%value%", String.valueOf(value))
                .replace("%rank%", String.valueOf(rankIndex + 1));

    }

    private List<Map.Entry<UUID, Integer>> getLeaderboard(String statisticType) {

        String cacheKey = "global:" + statisticType;

        Long timestamp = cacheTimestamps.get(cacheKey);
        if (timestamp != null && System.currentTimeMillis() - timestamp < cacheTimeout) {

            return leaderboardCache.get(cacheKey);

        }

        List<Map.Entry<UUID, Integer>> result = plugin.getProfileManager().getLeaderboard(statisticType).join();

        leaderboardCache.put(cacheKey, result);
        cacheTimestamps.put(cacheKey, System.currentTimeMillis());

        return result;

    }

    private List<Map.Entry<UUID, Integer>> getLeaderboard(String statisticType, String identifier) {

        String cacheKey = identifier + ":" + statisticType;

        Long timestamp = cacheTimestamps.get(cacheKey);
        if (timestamp != null && System.currentTimeMillis() - timestamp < cacheTimeout) {

            return leaderboardCache.get(cacheKey);

        }

        List<Map.Entry<UUID, Integer>> result = plugin.getProfileManager().getLeaderboard(statisticType, identifier).join();

        leaderboardCache.put(cacheKey, result);
        cacheTimestamps.put(cacheKey, System.currentTimeMillis());

        return result;

    }

}
