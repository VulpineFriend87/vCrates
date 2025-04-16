package pro.vulpine.vCrates.manager;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.configuration.KeysConfiguration;
import pro.vulpine.vCrates.instance.Key;
import pro.vulpine.vCrates.utils.PlaceholderUtils;
import pro.vulpine.vCrates.utils.logger.Logger;

import java.util.*;

public class KeyManager {

    private final VCrates plugin;

    private final List<Key> keys = new ArrayList<>();

    public KeyManager(VCrates plugin) {
        this.plugin = plugin;

        loadKeys(plugin.getKeysConfiguration());
    }

    public void reload() {

        keys.clear();

        loadKeys(plugin.getKeysConfiguration());

    }

    private void loadKeys(KeysConfiguration config) {

        Set<String> keyConfigurationKeys = config.getKeys(false);

        if (keyConfigurationKeys.isEmpty()) {

            Logger.error("No keys found, please add them.", "KeyManager");
            return;

        }

        Logger.info("Found " + keyConfigurationKeys.size() + " key(s)", "KeyManager");

        for (String identifier : keyConfigurationKeys) {

            ConfigurationSection keySection = config.getConfigurationSection(identifier);

            if (keySection == null) {
                Logger.warn("Key " + identifier + " is null, skipping.", "KeyManager");
                continue;
            }

            boolean virtualAllowed = keySection.getBoolean("virtual_allowed");
            String name = PlaceholderUtils.replace(null, keySection.getString("name"));
            Material item = Material.valueOf(keySection.getString("item"));

            Key key = new Key(virtualAllowed, identifier, name, item);
            keys.add(key);

        }

        Logger.info("Loaded " + keys.size() + " key(s).", "KeyManager");

    }

    public Key getKey(String identifier) {

        for (Key key : keys) {
            if (key.getIdentifier().equals(identifier)) {
                return key;
            }
        }

        return null;
    }

    public List<Key> getKeys() {
        return keys;
    }

    public VCrates getPlugin() {
        return plugin;
    }
}