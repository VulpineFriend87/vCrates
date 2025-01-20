package pro.vulpine.vCrates.manager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.instance.Crate;
import pro.vulpine.vCrates.instance.Reward;
import pro.vulpine.vCrates.instance.RewardItem;
import pro.vulpine.vCrates.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateManager {

    private final VCrates plugin;

    private final Map<String, Crate> crates = new HashMap<>();

    public CrateManager(VCrates plugin) {
        this.plugin = plugin;

        loadCrates(plugin.getConfig());
    }

    private void loadCrates(FileConfiguration config) {

        ConfigurationSection cratesSection = config.getConfigurationSection("crates");

        if (cratesSection == null) {

            Logger.error("No crates found in config.yml, please add them.", "CrateManager");
            return;

        }

        Logger.info("Found " + cratesSection.getKeys(false).size() + " crate(s) in config.yml", "CrateManager");

        for (String identifier : cratesSection.getKeys(false)) {

            ConfigurationSection crateSection = cratesSection.getConfigurationSection(identifier);

            if (crateSection == null) {
                Logger.warn("Crate " + identifier + " is null, skipping.", "CrateManager");
                continue;
            }

            String name = crateSection.getString("name");
            Logger.info("Loading crate " + identifier + " with name " + name, "CrateManager");

            List<Location> blocks = new ArrayList<>();
            List<String> blocksStrings = crateSection.getStringList("blocks");
            Logger.info("Found " + blocksStrings.size() + " blocks for crate " + identifier, "CrateManager");
            for (String loc : blocksStrings) {
                String[] parts = loc.split(",");

                if (parts.length == 4) {
                    String worldName = parts[0];
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);

                    Location location = new Location(plugin.getServer().getWorld(worldName), x, y, z);
                    blocks.add(location);
                    Logger.info("Added block " + loc + " to crate " + identifier, "CrateManager");
                } else {
                    Logger.warn("Block " + loc + " is not a valid location, skipping.", "CrateManager");
                }
            }

            List<Reward> rewards = new ArrayList<>();

            ConfigurationSection rewardsSection = crateSection.getConfigurationSection("rewards");

            if (rewardsSection != null) {

                Logger.info("Loading rewards for crate " + identifier, "CrateManager");

                for (String rewardIdentifier : rewardsSection.getKeys(false)) {

                    ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(rewardIdentifier);

                    if (rewardSection == null) {
                        Logger.warn("Reward " + rewardIdentifier + " is null, skipping.", "CrateManager");
                        continue;
                    }

                    String rewardName = rewardSection.getString("name");
                    Material displayItem = Material.getMaterial(rewardSection.getString("displayItem", "STONE").toUpperCase());

                    List<RewardItem> rewardItems = new ArrayList<>();
                    List<Map<?, ?>> itemsList = rewardSection.getMapList("items");

                    Logger.info("Found " + itemsList.size() + " items for reward " + rewardIdentifier, "CrateManager");

                    for (Map<?, ?> itemData : itemsList) {

                        String itemName = (String) itemData.get("name");
                        List<String> itemLore = (List<String>) itemData.get("lore");
                        Material itemType = Material.getMaterial(itemData.get("type").toString().toUpperCase());
                        int amount = (int) itemData.get("amount");

                        if (itemType != null) {
                            rewardItems.add(new RewardItem(itemName, itemLore, itemType, amount));
                            Logger.info("Added item " + itemName + " to reward " + rewardIdentifier, "CrateManager");
                        } else {
                            Logger.warn("Item " + itemData.get("type") + " is not a valid material, skipping.", "CrateManager");
                        }

                    }

                    List<String> rewardCommands = rewardSection.getStringList("commands");

                    rewards.add(new Reward(rewardIdentifier, rewardName, displayItem, rewardItems, rewardCommands));
                    Logger.info("Added reward " + rewardIdentifier + " to crate " + identifier, "CrateManager");

                }

            }

            Crate crate = new Crate(identifier, name, blocks, rewards);
            crates.put(identifier, crate);

        }

        Logger.info("Loaded " + crates.size() + " crate(s).", "CrateManager");

    }

    public Crate getCrate(String identifier) {
        return crates.get(identifier);
    }

    public Crate getCrate(Location location) {
        for (Crate crate : crates.values()) {
            if (crate.getBlocks().contains(location)) {
                return crate;
            }
        }
        return null;
    }

    public Map<String, Crate> getCrates() {
        return crates;
    }

}
