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
