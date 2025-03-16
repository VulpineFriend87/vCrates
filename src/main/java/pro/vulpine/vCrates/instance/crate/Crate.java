package pro.vulpine.vCrates.instance.crate;

import pro.vulpine.vCrates.utils.Colorize;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pro.vulpine.vCrates.instance.Profile;
import pro.vulpine.vCrates.instance.milestone.Milestone;
import pro.vulpine.vCrates.instance.reward.Reward;
import pro.vulpine.vCrates.instance.reward.RewardItem;
import pro.vulpine.vCrates.manager.CrateManager;
import pro.vulpine.vCrates.utils.KeyUtils;
import pro.vulpine.vCrates.instance.Rarity;

import java.util.*;

public class Crate {

    private final CrateManager crateManager;

    private final CrateKeys crateKeys;
    private final CratePushback cratePushback;
    private final CrateEffect crateEffect;
    private final CrateHologram crateHologram;

    private final String identifier;
    private final String name;
    private final int cooldown;
    private final List<Location> blocks;
    private final List<Milestone> milestones;
    private final List<Reward> rewards;
    private final Map<UUID, Long> cooldowns;

    public Crate(CrateManager crateManager, CrateKeys crateKeys, CratePushback cratePushback, CrateEffect crateEffect, CrateHologram crateHologram, String identifier, String name, int cooldown, List<Location> blocks, List<Milestone> milestones, List<Reward> rewards) {
        this.crateManager = crateManager;

        this.crateKeys = crateKeys;
        this.cratePushback = cratePushback;
        this.crateEffect = crateEffect;
        this.crateHologram = crateHologram;

        this.identifier = identifier;
        this.name = name;
        this.cooldown = cooldown;
        this.blocks = blocks;
        this.milestones = milestones;
        this.rewards = rewards;
        this.cooldowns = new HashMap<>();
    }

    public void open(Player player) {

        Profile profile = crateManager.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile == null) {

            player.sendMessage(Colorize.color(
                    crateManager.getPlugin().getResponsesConfiguration().getString("no_profile")
            ));

            return;
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%crate%", name);
        placeholders.put("%player%", player.getName());
        placeholders.put("%times_opened%", String.valueOf(crateManager.getPlugin().getProfileManager()
                .getProfile(player.getUniqueId()).getStatistic("crates-opened", identifier)));

        Boolean usedVirtualKey = null;

        if (crateKeys.isRequired()) {

            if (!crateKeys.isKeyAllowed(KeyUtils.getKeyIdentifier(player.getInventory().getItemInMainHand()))) {

                if (!profile.hasKey(crateKeys.getAllowedKeys())) {

                    crateManager.getPlugin().getActionParser().executeActions(
                            crateManager.getPlugin().getResponsesConfiguration().getStringList("keys.missing"),
                            player, 0, placeholders
                    );

                    if (cratePushback.isEnabled()) {
                        cratePushback.execute(player);
                    }

                    return;

                } else {

                    usedVirtualKey = true;

                }

            } else {

                usedVirtualKey = false;

            }

        }

        placeholders.put("%cooldown%", String.valueOf(getRemainingCooldown(player)));

        if (isCooldownActive(player)) {

            crateManager.getPlugin().getActionParser().executeActions(
                    crateManager.getPlugin().getResponsesConfiguration().getStringList("crates.cooldown"),
                    player, 0, placeholders
            );

            return;
        }

        if (!usedVirtualKey) {

            ItemStack key = player.getInventory().getItemInMainHand();

            profile.incrementStatistic("keys-used", KeyUtils.getKeyIdentifier(key));

            player.getInventory().getItemInMainHand().setAmount(key.getAmount() - 1);

        } else {

            String identifier = profile.useKey(crateKeys.getAllowedKeys());

            profile.incrementStatistic("keys-used", identifier);

        }

        // REWARD SELECTION

        int totalWeight = 0;
        Map<Rarity, List<Reward>> rarityRewardMap = new HashMap<>();

        for (Reward reward : rewards) {
            Rarity rarity = reward.getRarity();
            if (rarity != null) {
                rarityRewardMap.computeIfAbsent(rarity, k -> new ArrayList<>()).add(reward);
            }
        }

        for (Rarity rarity : rarityRewardMap.keySet()) {

            totalWeight = totalWeight + rarity.getWeight();

        }

        if (totalWeight <= 0) {

            player.sendMessage(Colorize.color(
                    crateManager.getPlugin().getResponsesConfiguration().getString("no_rewards")
            ));

            return;
        }

        Random random = new Random();
        int randomValue = random.nextInt(totalWeight);

        int currentWeight = 0;
        Rarity selectedRarity = null;
            profile.useKey(crateKeys.getAllowedKeys());

        for (Rarity rarity : rarityRewardMap.keySet()) {

            currentWeight = currentWeight + rarity.getWeight();

            if (randomValue < currentWeight) {

                selectedRarity = rarity;
                break;

            }

        }

        Reward selectedReward = null;

        if (selectedRarity != null) {
            List<Reward> rewardsForRarity = rarityRewardMap.get(selectedRarity);

            if (!rewardsForRarity.isEmpty()) {

                selectedReward = rewardsForRarity.get(random.nextInt(rewardsForRarity.size()));

            }

        }

        if (selectedReward == null) {

            player.sendMessage(Colorize.color(
                    crateManager.getPlugin().getResponsesConfiguration().getString("error")
            ));

            return;

        }

        // OPENING

        placeholders.put("%reward%", selectedReward.getName());

        for (RewardItem item : selectedReward.getItems()) {
            player.getInventory().addItem(RewardItem.toItemStack(item));
        }

        profile.incrementStatistic("crates-opened", identifier);

        crateManager.getPlugin().getActionParser().executeActions(selectedReward.getActions(), player, 0, placeholders);

        for (Milestone milestone : milestones) {

            int cratesOpened = profile.getStatistic("crates-opened", identifier);
            int after = milestone.getAfter();

            if (cratesOpened < after) {
                continue;
            }

            int rewardIndex = cratesOpened / after;

            if (milestone.getRepeats().isEnabled()) {

                if (cratesOpened % after == 0 && rewardIndex <= milestone.getRepeats().getTimes()) {

                    milestone.giveRewards(player);

                }

            } else {

                if (cratesOpened % after == 0) {

                    milestone.giveRewards(player);

                }

            }

        }

        if (cooldown > 0) {
            setCooldown(player);
        }

    }

    private double getRemainingCooldown(Player player) {

        if (cooldowns.containsKey(player.getUniqueId())) {

            long remaining = cooldown - (System.currentTimeMillis() - cooldowns.get(player.getUniqueId()));
            return remaining > 0 ? (double) Math.round(remaining / 1000.0 * 100.0) / 100.0 : 0;

        }

        return 0;

    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private boolean isCooldownActive(Player player) {

        if (cooldowns.containsKey(player.getUniqueId())) {
            return System.currentTimeMillis() - cooldowns.get(player.getUniqueId()) <= cooldown;
        }

        return false;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public CrateKeys getCrateKeys() {
        return crateKeys;
    }

    public CratePushback getCratePushback() {
        return cratePushback;
    }

    public CrateEffect getCrateEffect() {
        return crateEffect;
    }

    public CrateHologram getCrateHologram() {
        return crateHologram;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public long getCooldown() {
        return cooldown;
    }

    public List<Location> getBlocks() {
        return blocks;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

}