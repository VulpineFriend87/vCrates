package pro.vulpine.vCrates.instance;

import pro.vulpine.vCrates.manager.ProfileManager;

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

    public CompletableFuture<Void> updateKey(String identifier, int amount) {

        keys.put(identifier, amount);

        return profileManager.getPlugin().getProfileManager().updateProfile(this);

    }

    public CompletableFuture<Void> giveKey(String identifier, int amount) {

        keys.put(identifier, keys.getOrDefault(identifier, 0) + amount);

        return profileManager.getPlugin().getProfileManager().updateProfile(this);

    }

    public CompletableFuture<Void> takeKey(String identifier, int amount) {

        keys.put(identifier, keys.getOrDefault(identifier, 0) - amount);

        return profileManager.getPlugin().getProfileManager().updateProfile(this);

    }

    public void useKey(String identifier) {

        takeKey(identifier, 1);

    }

    public void useKey(List<String> identifiers) {

        boolean used = false;

        for (String identifier : identifiers) {

            if (hasKey(identifier) && !used) {

                useKey(identifier);

                used = true;

            }

        }

    }

    public int getKeyCount(String identifier) {

        return keys.getOrDefault(identifier, 0);

    }

    public boolean hasKey(String identifier) {

        return keys.containsKey(identifier) && keys.get(identifier) > 0;

    }

    public boolean hasKey(List<String> identifiers) {

        for (String identifier : identifiers) {

            if (!hasKey(identifier)) {

                return false;

            }

        }

        return true;

    }

    public void setStatistic(String type, String identifier, int value) {

        for (StatisticEntry entry : statistics) {

            if (entry.getType().equals(type) && entry.getIdentifier().equals(identifier)) {

                entry.setValue(value);
                return;

            }

        }

        statistics.add(new StatisticEntry(type, identifier, value));

    }

    public void incrementStatistic(StatisticEntry entry) {

        entry.setValue(entry.getValue() + 1);

        profileManager.getPlugin().getProfileManager().updateProfile(this);

    }

    public void incrementStatistic(String type, String identifier) {

        for (StatisticEntry entry : statistics) {

            if (entry.getType().equals(type) && entry.getIdentifier().equals(identifier)) {

                entry.setValue(entry.getValue() + 1);
                return;

            }

        }

        statistics.add(new StatisticEntry(type, identifier, 1));

        profileManager.getPlugin().getProfileManager().updateProfile(this);

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

    public Integer getStatistic(String type, String identifier) {

        for (StatisticEntry entry : statistics) {

            if (entry.getType().equals(type) && entry.getIdentifier().equals(identifier)) {

                return entry.getValue();

            }

        }

        return null;

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
