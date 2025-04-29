package pro.vulpine.vCrates.instance;

import pro.vulpine.vCrates.manager.ProfileManager;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Profile {

    private final ProfileManager profileManager;

    private final UUID owner;
    private final Map<String, Integer> keys;
    private final List<StatisticEntry> statistics;

    public Profile(ProfileManager profileManager, UUID owner, Map<String, Integer> keys, List<StatisticEntry> statistics) {
        this.profileManager = profileManager;

        this.owner = owner;
        this.keys = keys;
        this.statistics = statistics;
    }

    public CompletableFuture<Void> updateKey(String identifier, int amount, boolean updateProfile) {

        keys.put(identifier, amount);

        return updateProfile ? update() : CompletableFuture.completedFuture(null);

    }

    public CompletableFuture<Void> giveKey(String identifier, int amount, boolean updateProfile) {

        keys.put(identifier, keys.getOrDefault(identifier, 0) + amount);

        return updateProfile ? update() : CompletableFuture.completedFuture(null);

    }

    public CompletableFuture<Void> takeKey(String identifier, int amount, boolean updateProfile) {

        keys.put(identifier, keys.getOrDefault(identifier, 0) - amount);

        return updateProfile ? update() : CompletableFuture.completedFuture(null);

    }

    public CompletableFuture<Void> useKey(String identifier, boolean updateProfile) {

        return takeKey(identifier, 1, updateProfile);

    }

    public int getKeyCount(String identifier) {

        return keys.getOrDefault(identifier, 0);

    }

    public boolean hasKey(String identifier) {

        return keys.containsKey(identifier) && keys.get(identifier) > 0;

    }

    public boolean hasKey(List<String> identifiers) {

        if (identifiers == null || identifiers.isEmpty()) {
            return false;
        }

        for (String identifier : identifiers) {
            if (identifier != null && hasKey(identifier)) {
                return true;
            }
        }

        return false;
    }

    public CompletableFuture<Void> setStatistic(String type, String identifier, int value, boolean updateProfile) {

        for (StatisticEntry entry : statistics) {

            if (entry.getType().equals(type) && entry.getIdentifier().equals(identifier)) {

                entry.setValue(value);

                return updateProfile ? update() : CompletableFuture.completedFuture(null);

            }

        }

        statistics.add(new StatisticEntry(type, identifier, value));

        return updateProfile ? update() : CompletableFuture.completedFuture(null);

    }

    public CompletableFuture<Void> incrementStatistic(StatisticEntry entry, boolean updateProfile) {

        entry.setValue(entry.getValue() + 1);

        return updateProfile ? update() : CompletableFuture.completedFuture(null);

    }

    public CompletableFuture<Void> incrementStatistic(String type, String identifier, boolean updateProfile) {
        // Add null check for identifier
        if (identifier == null) {
            return updateProfile ? update() : CompletableFuture.completedFuture(null);
        }

        for (StatisticEntry entry : statistics) {

            if (entry.getType().equals(type) && 
                entry.getIdentifier() != null && 
                entry.getIdentifier().equals(identifier)) {

                entry.setValue(entry.getValue() + 1);
                return updateProfile ? update() : CompletableFuture.completedFuture(null);
            }
        }

        statistics.add(new StatisticEntry(type, identifier, 1));
        return updateProfile ? update() : CompletableFuture.completedFuture(null);
    }

    public void decrementStatistic(StatisticEntry entry) {

        entry.setValue(entry.getValue() - 1);

        profileManager.getPlugin().getProfileManager().updateProfile(this);

    }

    public void decrementStatistic(String type, String identifier) {

        for (StatisticEntry entry : statistics) {

            if (entry.getType().equals(type) && entry.getIdentifier().equals(identifier)) {

                entry.setValue(entry.getValue() - 1);
                return;

            }

        }

        statistics.add(new StatisticEntry(type, identifier, -1));

        profileManager.getPlugin().getProfileManager().updateProfile(this);

    }

    public CompletableFuture<Void> update() {
        return profileManager.getPlugin().getProfileManager().updateProfile(this);
    }

    public Integer getStatistic(String type, String identifier) {

        for (StatisticEntry entry : statistics) {

            if (entry.getType().equals(type) && entry.getIdentifier().equals(identifier)) {

                return entry.getValue();

            }

        }

        return null;

    }

    public Integer getTotalStatistic(String type) {

        return statistics.stream()
                .filter(entry -> entry.getType().equals(type))
                .map(StatisticEntry::getValue)
                .reduce(0, Integer::sum);

    }

    public String getTopStatisticIdentifier(String type) {

        return statistics.stream()
                .filter(entry -> entry.getType().equals(type))
                .max(Comparator.comparing(StatisticEntry::getValue))
                .map(StatisticEntry::getIdentifier)
                .orElse(null);

    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public UUID getOwner() {
        return owner;
    }

    public Map<String, Integer> getKeys() {
        return keys;
    }

    public List<StatisticEntry> getStatistics() {
        return statistics;
    }
}
