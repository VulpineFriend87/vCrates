package pro.vulpine.vCrates.manager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.configuration.CratesConfiguration;
import pro.vulpine.vCrates.instance.Rarity;
import pro.vulpine.vCrates.instance.crate.*;
import pro.vulpine.vCrates.instance.milestone.Milestone;
import pro.vulpine.vCrates.instance.milestone.MilestoneRepeats;
import pro.vulpine.vCrates.instance.milestone.MilestoneReward;
import pro.vulpine.vCrates.instance.reward.Reward;
import pro.vulpine.vCrates.instance.reward.RewardDisplayItem;
import pro.vulpine.vCrates.instance.reward.RewardItem;
import pro.vulpine.vCrates.utils.logger.Logger;

import java.util.*;

public class CrateManager {

    private final VCrates plugin;

    private final List<Crate> crates = new ArrayList<>();

    public CrateManager(VCrates plugin) {
        this.plugin = plugin;

        loadCrates(plugin.getCratesConfiguration());
    }

    public void reload() {

        unloadCrates();

        loadCrates(plugin.getCratesConfiguration());

    }

    private void loadCrates(CratesConfiguration config) {

        Set<String> crateConfigurationKeys = config.getKeys(false);

        if (crateConfigurationKeys.isEmpty()) {

            Logger.warn("No crates found, please add them.", "CrateManager");
            return;

        }

        Logger.info("Found " + crateConfigurationKeys.size() + " crate(s)", "CrateManager");

        for (String identifier : crateConfigurationKeys) {

            ConfigurationSection crateSection = config.getConfigurationSection(identifier);

            if (crateSection == null) {
                Logger.warn("Crate " + identifier + " is null, skipping.", "CrateManager");
                continue;
            }

            String name = crateSection.getString("name");
            int cooldown = crateSection.getInt("cooldown");
            boolean preview = crateSection.getBoolean("preview", true);

            boolean crateKeysRequired = crateSection.getBoolean("keys.required", true);
            List<String> crateKeysAllowed = crateSection.getStringList("keys.allowed");
            CrateKeys crateKeys = new CrateKeys(crateKeysRequired, crateKeysAllowed);

            boolean pushbackEnabled = crateSection.getBoolean("pushback.enabled", true);
            double pushbackYOffset = crateSection.getDouble("pushback.y_offset", -0.4);
            double pushbackMultiply = crateSection.getDouble("pushback.multiply", -1.25);
            CratePushback cratePushback = new CratePushback(pushbackEnabled, pushbackYOffset, pushbackMultiply);

            // BLOCKS

            List<Location> blocks = new ArrayList<>();
            List<String> blocksStrings = crateSection.getStringList("blocks");
            for (String loc : blocksStrings) {
                String[] parts = loc.split(",");

                if (parts.length == 4) {
                    String worldName = parts[0];
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);

                    Location location = new Location(plugin.getServer().getWorld(worldName), x, y, z);
                    blocks.add(location);
                } else {
                    Logger.warn("Block " + loc + " is not a valid location, skipping.", "CrateManager");
                }
            }

            boolean effectEnabled = crateSection.getBoolean("effect.enabled", false);
            String effectType = crateSection.getString("effect.type", "HELIX");
            Particle effectParticle = Particle.valueOf(crateSection.getString("effect.particle", "ELECTRIC_SPARK"));
            double effectRadius = crateSection.getDouble("effect.radius", 1);
            double effectSpeed = crateSection.getDouble("effect.speed", 1);
            double effectYOffset = crateSection.getDouble("effect.y_offset", 0);
            CrateEffect crateEffect = new CrateEffect(effectEnabled, effectType, effectParticle, effectRadius, effectSpeed, effectYOffset);

            boolean hologramEnabled = crateSection.getBoolean("hologram.enabled", true);
            List<String> hologramLines = crateSection.getStringList("hologram.lines");
            double hologramYOffset = crateSection.getDouble("hologram.y_offset", 0);
            CrateHologram crateHologram = new CrateHologram(hologramEnabled, blocks, hologramLines, hologramYOffset);

            // REWARDS

            List<Reward> rewards = new ArrayList<>();

            ConfigurationSection rewardsSection = crateSection.getConfigurationSection("rewards");

            if (rewardsSection != null) {

                for (String rewardIdentifier : rewardsSection.getKeys(false)) {

                    ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(rewardIdentifier);

                    if (rewardSection == null) {
                        continue;
                    }

                    String rewardName = rewardSection.getString("name");

                    Rarity rewardRarity = plugin.getRarityManager().getRarity(rewardSection.getString("rarity"));
                    if (rewardRarity == null) {
                        Logger.warn("Rarity " + rewardSection.getString("rarity") + " is not a valid rarity, skipping.", "CrateManager");
                        continue;
                    }

                    // DISPLAY ITEM

                    ConfigurationSection rewardDisplayItemSection = rewardSection.getConfigurationSection("display_item");

                    Material displayItemType = Material.getMaterial(rewardDisplayItemSection.getString("type").toUpperCase());
                    if (displayItemType == null) {
                        Logger.warn("Display item type " + rewardDisplayItemSection.getString("type") + " is not a valid material, skipping.", "CrateManager");
                        continue;
                    }

                    int displayItemAmount = rewardDisplayItemSection.getInt("amount", 1);

                    List<String> displayItemLore = rewardDisplayItemSection.getStringList("lore");

                    RewardDisplayItem rewardDisplayItem = new RewardDisplayItem(displayItemType, displayItemAmount, displayItemLore);

                    // ITEMS

                    List<RewardItem> rewardItems = new ArrayList<>();
                    List<Map<?, ?>> itemsList = rewardSection.getMapList("items");

                    for (Map<?, ?> itemData : itemsList) {

                        String itemName = (String) itemData.get("name");

                        List<String> itemLore = (List<String>) itemData.get("lore");

                        Material itemType = Material.getMaterial(itemData.get("type").toString().toUpperCase());
                        int itemAmount = (int) itemData.get("amount");

                        if (itemType == null) {
                            Logger.warn("Item " + itemData.get("type") + " is not a valid material, skipping.", "CrateManager");
                            continue;
                        }

                        rewardItems.add(new RewardItem(itemName, itemLore, itemType, itemAmount));

                    }

                    List<String> rewardActions = rewardSection.getStringList("actions");

                    rewards.add(new Reward(rewardIdentifier, rewardName, rewardDisplayItem, rewardRarity, rewardItems, rewardActions));

                }

            }

            // MILESTONES

            List<Milestone> milestones = new ArrayList<>();

            List<Map<?, ?>> milestonesList = crateSection.getMapList("milestones");

            if (!milestonesList.isEmpty()) {

                for (Map<?, ?> milestoneData : milestonesList) {

                    int milestoneAfter = (int) milestoneData.get("after");
                    List<String> milestoneActions = (List<String>) milestoneData.get("actions");

                    MilestoneRepeats milestoneRepeats = new MilestoneRepeats(
                            (boolean) ((Map<?, ?>) milestoneData.get("repeats")).get("enabled"),
                            (int) ((Map<?, ?>) milestoneData.get("repeats")).get("times")
                    );

                    List<MilestoneReward> milestoneRewards = new ArrayList<>();

                    for (Map<?, ?> rewardData : (List<Map<?, ?>>) milestoneData.get("rewards")) {

                        String rewardName = (String) rewardData.get("name");
                        Material rewardDisplayItem = Material.getMaterial(rewardData.get("display_item").toString().toUpperCase());
                        if (rewardDisplayItem == null) {
                            Logger.warn("Display item " + rewardData.get("display_item") + " is not a valid material, skipping.", "CrateManager");
                            continue;
                        }

                        List<RewardItem> rewardItems = new ArrayList<>();

                        for (Map<?, ?> itemData : (List<Map<?, ?>>) rewardData.get("items")) {

                            String itemName = (String) itemData.get("name");
                            List<String> itemLore = (List<String>) itemData.get("lore");
                            Material itemType = Material.getMaterial(itemData.get("type").toString().toUpperCase());
                            if (itemType == null) {
                                Logger.warn("Item type " + itemData.get("type") + " is not a valid material, skipping.", "CrateManager");
                                continue;
                            }
                            int itemAmount = (int) itemData.get("amount");

                            rewardItems.add(new RewardItem(itemName, itemLore, itemType, itemAmount));

                        }

                        milestoneRewards.add(new MilestoneReward(rewardName, rewardDisplayItem, rewardItems));

                    }

                    milestones.add(new Milestone(milestoneAfter, milestoneActions, milestoneRepeats, milestoneRewards));

                }
            }

            Crate crate = new Crate(this, crateKeys, cratePushback, crateEffect, crateHologram, identifier, name, cooldown, preview, blocks, milestones, rewards);

            crate.getCrateEffect().setCrate(crate);

            for (Milestone milestone : milestones) {

                milestone.setCrate(crate);

            }

            crates.add(crate);

        }

        Logger.info("Loaded " + crates.size() + " crate(s).", "CrateManager");

    }

    public void unloadCrates() {

        for (Crate crate : crates) {
            crate.getCrateHologram().remove();
            crate.getCrateEffect().cancel();
        }

        crates.clear();
    }

    public Crate getCrate(String identifier) {

        for (Crate crate : crates) {
            if (crate.getIdentifier().equals(identifier)) {
                return crate;
            }
        }

        return null;
    }

    public Crate getCrate(Location location) {

        for (Crate crate : crates) {
            if (crate.getBlocks().contains(location)) {
                return crate;
            }
        }

        return null;
    }

    public List<Crate> getCrates() {
        return crates;
    }

    public VCrates getPlugin() {
        return plugin;
    }
}
