package pro.vulpine.vCrates.manager;

import org.bukkit.configuration.ConfigurationSection;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.configuration.RaritiesConfiguration;
import pro.vulpine.vCrates.instance.Rarity;
import pro.vulpine.vCrates.utils.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RarityManager {

    private final VCrates plugin;

    private final List<Rarity> rarities = new ArrayList<>();

    public RarityManager(VCrates plugin) {
        this.plugin = plugin;

        loadRarities(plugin.getRaritiesConfiguration());
    }

    public void reload() {

        rarities.clear();

        loadRarities(plugin.getRaritiesConfiguration());

    }

    private void loadRarities(RaritiesConfiguration config) {

        Set<String> rarityConfigurationKeys = config.getKeys(false);

        if (rarityConfigurationKeys.isEmpty()) {

            Logger.error("No rarities found, please add them.", "RarityManager");
            return;

        }

        Logger.info("Found " + rarityConfigurationKeys.size() + " rarit(y/ies)", "RarityManager");

        for (String identifier : rarityConfigurationKeys) {

            ConfigurationSection raritySection = config.getConfigurationSection(identifier);

            if (raritySection == null) {
                Logger.warn("Rarity " + identifier + " is null, skipping.", "RarityManager");
                continue;
            }

            String name = raritySection.getString("name");
            int weight = raritySection.getInt("weight");

            Rarity rarity = new Rarity(identifier, name, weight);
            rarities.add(rarity);

        }

        Logger.info("Loaded " + rarities.size() + " rarit(y/ies).", "RarityManager");

    }

    public Rarity getRarity(String identifier) {

        for (Rarity rarity : rarities) {
            if (rarity.getIdentifier().equals(identifier)) {
                return rarity;
            }
        }

        return null;
    }

    public List<Rarity> getRarities() {
        return rarities;
    }

    public VCrates getPlugin() {
        return plugin;
    }
}
