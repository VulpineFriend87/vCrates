package pro.vulpine.vCrates.instance;

import pro.vulpine.vCrates.manager.ProfileManager;

import java.util.Map;
import java.util.UUID;

public class Profile {

    private final ProfileManager profileManager;

    private final UUID owner;
    private final Map<String, Integer> keys;

    public Profile(ProfileManager profileManager, UUID owner, Map<String, Integer> keys) {
        this.profileManager = profileManager;

        this.owner = owner;
        this.keys = keys;
    }

    public void updateKey(String identifier, int amount) {

        keys.put(identifier, amount);

        profileManager.getPlugin().getProfileManager().updateProfile(this);

    }

    public void giveKey(String identifier, int amount) {

        keys.put(identifier, keys.getOrDefault(identifier, 0) + amount);

        profileManager.getPlugin().getProfileManager().updateProfile(this);

    }

    public void takeKey(String identifier, int amount) {

        keys.put(identifier, keys.getOrDefault(identifier, 0) - amount);

        profileManager.getPlugin().getProfileManager().updateProfile(this);

    }

    public int getKeyCount(String identifier) {

        return keys.getOrDefault(identifier, 0);

    }

    public boolean hasKey(String identifier) {

        return keys.containsKey(identifier);

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
}
