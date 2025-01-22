package pro.vulpine.vCrates.manager;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.configuration.KeysConfiguration;
import pro.vulpine.vCrates.instance.Key;
import pro.vulpine.vCrates.utils.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KeyManager {

    private final VCrates plugin;

    private final Map<String, Key> keys = new HashMap<>();

    public KeyManager(VCrates plugin) {
        this.plugin = plugin;
    }

    private void loadKeys(KeysConfiguration config) {

        keys.clear();

        Set<String> keyKeys = config.getKeys(false);

        if (keyKeys.isEmpty()) {

            Logger.error("No keys found, please add them.", "KeyManager");
            return;

        }

        Logger.info("Found " + keyKeys.size() + " key(s)", "KeyManager");

        for (String identifier : keyKeys) {

            ConfigurationSection keySection = config.getConfigurationSection(identifier);

            if (keySection == null) {
                Logger.warn("Key " + identifier + " is null, skipping.", "KeyManager");
                continue;
            }

            String name = keySection.getString("name");
            Material item = Material.valueOf(keySection.getString("item"));

            Logger.info("Loading key " + identifier + " with name " + name, "KeyManager");

            Key key = new Key(identifier, name, item);
            keys.put(identifier, key);

        }

    }

    public Key getKey(String identifier) {
        return keys.get(identifier);
    }

    public Map<String, Key> getKeys() {
        return keys;
    }

    public VCrates getPlugin() {
        return plugin;
    }
}