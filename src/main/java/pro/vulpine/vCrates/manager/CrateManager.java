package pro.vulpine.vCrates.manager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.configuration.CratesConfiguration;
import pro.vulpine.vCrates.instance.crate.*;
import pro.vulpine.vCrates.instance.milestone.Milestone;
import pro.vulpine.vCrates.instance.milestone.MilestoneRepeats;
import pro.vulpine.vCrates.instance.milestone.MilestoneReward;
import pro.vulpine.vCrates.instance.reward.Reward;
import pro.vulpine.vCrates.instance.reward.RewardItem;
import pro.vulpine.vCrates.utils.logger.Logger;

import java.util.*;

public class CrateManager {

    private final VCrates plugin;

    private final Map<String, Crate> crates = new HashMap<>();

    public CrateManager(VCrates plugin) {
        this.plugin = plugin;

        loadCrates(plugin.getCratesConfiguration());
    }

    public void reload() {

        unloadCrates();

        loadCrates(plugin.getCratesConfiguration());

    }

    private void loadCrates(CratesConfiguration config) {

        Set<String> crateKeys = config.getKeys(false);

        if (crateKeys.isEmpty()) {

            Logger.warn("No crates found, please add them.", "CrateManager");
            return;

        }

        Logger.info("Found " + crateKeys.size() + " crate(s)", "CrateManager");

        for (String identifier : crateKeys) {

            ConfigurationSection crateSection = config.getConfigurationSection(identifier);

            if (crateSection == null) {
                Logger.warn("Crate " + identifier + " is null, skipping.", "CrateManager");
                continue;
            }

            String name = crateSection.getString("name");
            int cooldown = crateSection.getInt("cooldown");

            boolean crateKeysRequired = crateSection.getBoolean("keys.required", true);
            List<String> crateKeysAllowed = crateSection.getStringList("keys.allowed");
            CrateKeys crateCrateKeys = new CrateKeys(crateKeysRequired, crateKeysAllowed);

            boolean pushbackEnabled = crateSection.getBoolean("pushback.enabled", true);
            double pushbackYOffset = crateSection.getDouble("pushback.y_offset", -0.4);
            double pushbackMultiply = crateSection.getDouble("pushback.multiply", -1.25);
            CratePushback crateCratePushback = new CratePushback(pushbackEnabled, pushbackYOffset, pushbackMultiply);

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
            CrateEffect crateCrateEffect = new CrateEffect(effectEnabled, effectType, effectParticle, effectRadius, effectSpeed);

            boolean hologramEnabled = crateSection.getBoolean("hologram.enabled", true);
            List<String> hologramLines = crateSection.getStringList("hologram.lines");
            double hologramYOffset = crateSection.getDouble("hologram.y_offset", 0);
            CrateHologram crateCrateHologram = new CrateHologram(hologramEnabled, blocks, hologramLines, hologramYOffset);

            List<Reward> rewards = new ArrayList<>();

            ConfigurationSection rewardsSection = crateSection.getConfigurationSection("rewards");

            if (rewardsSection != null) {

                for (String rewardIdentifier : rewardsSection.getKeys(false)) {

                    ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(rewardIdentifier);

                    if (rewardSection == null) {
                        continue;
                    }

                    String rewardName = rewardSection.getString("name");
                    Material displayItem = Material.getMaterial(rewardSection.getString("display_item").toUpperCase());
                    if (displayItem == null) {
                        Logger.warn("Display item " + rewardSection.getString("display_item") + " is not a valid material, skipping.", "CrateManager");
                        displayItem = Material.STONE;
                    }

                    List<RewardItem> rewardItems = new ArrayList<>();
                    List<Map<?, ?>> itemsList = rewardSection.getMapList("items");

                    for (Map<?, ?> itemData : itemsList) {

                        String itemName = (String) itemData.get("name");

                        List<String> itemLore = (List<String>) itemData.get("lore");

                        Material itemType = Material.getMaterial(itemData.get("type").toString().toUpperCase());
                        int amount = (int) itemData.get("amount");

                        if (itemType != null) {
                            rewardItems.add(new RewardItem(itemName, itemLore, itemType, amount));
                        } else {
                            Logger.warn("Item " + itemData.get("type") + " is not a valid material, skipping.", "CrateManager");
                        }

                    }

                    List<String> rewardActions = rewardSection.getStringList("actions");

                    rewards.add(new Reward(rewardIdentifier, rewardName, displayItem, rewardItems, rewardActions));

                }

            }

            List<Milestone> milestones = new ArrayList<>();

            List<Map<?, ?>> milestonesList = crateSection.getMapList("milestones");

            if (!milestonesList.isEmpty()) {

                for (Map<?, ?> milestoneData : milestonesList) {

                    int after = (int) milestoneData.get("after");
                    List<String> actions = (List<String>) milestoneData.get("actions");

                    MilestoneRepeats repeats = new MilestoneRepeats(
                            (boolean) ((Map<?, ?>) milestoneData.get("repeats")).get("enabled"),
                            (int) ((Map<?, ?>) milestoneData.get("repeats")).get("times")
                    );

                    List<MilestoneReward> milestoneRewards = new ArrayList<>();

                    for (Map<?, ?> rewardData : (List<Map<?, ?>>) milestoneData.get("rewards")) {

                        String rewardName = (String) rewardData.get("name");
                        Material displayItem = Material.getMaterial(rewardData.get("display_item").toString().toUpperCase());
                        if (displayItem == null) {
                            Logger.warn("Display item " + rewardData.get("display_item") + " is not a valid material, skipping.", "CrateManager");
                            displayItem = Material.STONE;
                        }

                        List<RewardItem> rewardItems = new ArrayList<>();

                        for (Map<?, ?> itemData : (List<Map<?, ?>>) rewardData.get("items")) {

                            String itemName = (String) itemData.get("name");
                            List<String> itemLore = (List<String>) itemData.get("lore");
                            Material itemType = Material.getMaterial(itemData.get("type").toString().toUpperCase());
                            if (itemType == null) {
                                Logger.warn("Item type " + itemData.get("type") + " is not a valid material, skipping.", "CrateManager");
                                itemType = Material.STONE;
                            }
                            int amount = (int) itemData.get("amount");

                            rewardItems.add(new RewardItem(itemName, itemLore, itemType, amount));

                        }

                        milestoneRewards.add(new MilestoneReward(rewardName, displayItem, rewardItems));

                    }

                    milestones.add(new Milestone(after, actions, repeats, milestoneRewards));

                }
            }

            Crate crate = new Crate(this, crateCrateKeys, crateCratePushback, crateCrateEffect, crateCrateHologram, identifier, name, cooldown, blocks, milestones, rewards);

            crate.getCrateEffect().setCrate(crate);

            for (Milestone milestone : milestones) {

                milestone.setCrate(crate);

            }

            crates.put(identifier, crate);

        }

        Logger.info("Loaded " + crates.size() + " crate(s).", "CrateManager");

    }

    public void unloadCrates() {

        for (Crate crate : crates.values()) {
            crate.getCrateHologram().remove();
            crate.getCrateEffect().cancel();
        }

        crates.clear();
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

    public VCrates getPlugin() {
        return plugin;
    }
}
