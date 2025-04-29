package pro.vulpine.vCrates.instance.crate;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import pro.vulpine.vCrates.instance.CrateHolder;
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
    private final boolean preview;
    private final List<Location> blocks;
    private final List<Milestone> milestones;
    private final List<Reward> rewards;
    private final Map<UUID, Long> cooldowns;

    public Crate(CrateManager crateManager, CrateKeys crateKeys, CratePushback cratePushback, CrateEffect crateEffect, CrateHologram crateHologram, String identifier, String name, int cooldown, boolean preview, List<Location> blocks, List<Milestone> milestones, List<Reward> rewards) {
        this.crateManager = crateManager;

        this.crateKeys = crateKeys;
        this.cratePushback = cratePushback;
        this.crateEffect = crateEffect;
        this.crateHologram = crateHologram;

        this.identifier = identifier;
        this.name = name;
        this.cooldown = cooldown;
        this.preview = preview;
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

            if (crateKeys.getAllowedKeys().isEmpty()) {
                crateManager.getPlugin().getActionParser().executeActions(
                        crateManager.getPlugin().getResponsesConfiguration().getStringList("keys.missing"),
                        player, 0, placeholders
                );

                if (cratePushback.isEnabled()) {
                    cratePushback.execute(player);
                }

                return;
            }

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

        int worstCaseSlots = 0;

        for (Reward reward : rewards) {

            int requiredSlots = reward.getItems().size();
            worstCaseSlots = Math.max(worstCaseSlots, requiredSlots);

        }

        int freeSlots = (int) Arrays.stream(player.getInventory().getStorageContents())
                .filter(Objects::isNull).count();

        if (freeSlots < worstCaseSlots) {

            crateManager.getPlugin().getActionParser().executeActions(
                    crateManager.getPlugin().getResponsesConfiguration().getStringList("crates.not_enough_space"),
                    player, 0, placeholders
            );

            return;
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

        if (usedVirtualKey != null) {

            if (!usedVirtualKey) {

                ItemStack key = player.getInventory().getItemInMainHand();

                profile.incrementStatistic("keys-used", KeyUtils.getKeyIdentifier(key), false);

                player.getInventory().getItemInMainHand().setAmount(key.getAmount() - 1);

            } else {
                // Make sure we have a valid key identifier
                String identifier = crateKeys.getAllowedKeys().stream().findFirst().orElse(null);
                
                if (identifier != null) {
                    profile.useKey(identifier, false);
                    profile.incrementStatistic("keys-used", identifier, false);
                }
            }

        }

        // OPENING

        placeholders.put("%reward%", selectedReward.getName());

        for (RewardItem item : selectedReward.getItems()) {

            player.getInventory().addItem(RewardItem.toItemStack(item));

        }

        profile.incrementStatistic("crates-opened", identifier, false);

        crateManager.getPlugin().getActionParser().executeActions(selectedReward.getActions(), player, 0, placeholders);

        profile.update();

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

    public void preview(Player player, int page) {

        if (!preview) {
            return;
        }

        Profile profile = crateManager.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile == null) {

            player.sendMessage(Colorize.color(
                    crateManager.getPlugin().getResponsesConfiguration().getString("no_profile")
            ));

            return;
        }

        int totalWeight = 0;
        Map<Rarity, List<Reward>> rarityRewardMap = new HashMap<>();

        for (Reward reward : rewards) {
            Rarity rarity = reward.getRarity();
            if (rarity != null) {
                rarityRewardMap.computeIfAbsent(rarity, k -> new ArrayList<>()).add(reward);
                totalWeight = totalWeight + rarity.getWeight();
            }
        }

        ConfigurationSection previewSection = crateManager.getPlugin().getMainConfiguration().getConfigurationSection("gui.preview");

        List<Integer> rewardSlots = previewSection.getIntegerList("content.reward.slots");

        List<ItemStack> items = new ArrayList<>();
        for (Reward reward : rewards) {

            ItemStack item = new ItemStack(reward.getRewardDisplayItem().getType(), reward.getRewardDisplayItem().getAmount());

            ItemMeta meta = item.getItemMeta();
            if (meta != null) {

                List<String> lore = new ArrayList<>(reward.getRewardDisplayItem().getLore());

                double chance = (totalWeight > 0) ? (reward.getRarity().getWeight() * 100.0 / totalWeight) : 0;

                List<String> loreAppend = previewSection.getStringList("content.reward.lore_append");
                for (String line : loreAppend) {

                    lore.add(Colorize.color(line
                            .replace("%rarity%", reward.getRarity().getName())
                            .replace("%chance%", String.format("%.2f", chance))
                    ));

                }

                meta.setDisplayName(Colorize.color(reward.getName()));
                meta.setLore(Colorize.color(lore));

            }

            item.setItemMeta(meta);

            items.add(item);

        }

        int pageSize = rewardSlots.size();
        int totalPages = (int) Math.ceil((double) items.size() / pageSize);

        String title = previewSection.getString("title")
                .replace("%page%", String.valueOf(page + 1))
                .replace("%crate%", name)
                .replace("%total_pages%", String.valueOf(totalPages));

        int size = previewSection.getInt("size");

        Inventory gui = Bukkit.createInventory(new CrateHolder(this, "crate_preview", page, totalPages), size, Colorize.color("&8" + title));

        List<?> otherItems = previewSection.getList("content.other");
        if (otherItems != null) {

            for (Object otherItem : otherItems) {

                if (otherItem instanceof Map) {

                    Map<?, ?> itemMap = (Map<?, ?>) otherItem;

                    String type = (String) itemMap.get("type");
                    String name = (String) itemMap.get("name");
                    List<String> lore = (List<String>) itemMap.get("lore");
                    List<Integer> slots = (List<Integer>) itemMap.get("slots");

                    ItemStack item = new ItemStack(Material.valueOf(type));

                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {

                        meta.setDisplayName(Colorize.color(name));
                        meta.setLore(Colorize.color(lore));

                        item.setItemMeta(meta);

                    }

                    for (int slot : slots) {

                        gui.setItem(slot, item);

                    }

                }

            }

        }

        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, items.size());

        for (int i = startIndex, slotIndex = 0; i < endIndex; i++, slotIndex++) {

            gui.setItem(rewardSlots.get(slotIndex), items.get(i));

        }

        ConfigurationSection closeSection = previewSection.getConfigurationSection("content.close");
        if (closeSection != null) {

            ItemStack close = new ItemStack(Material.valueOf(closeSection.getString("type")));

            ItemMeta meta = close.getItemMeta();
            if (meta != null) {

                meta.setDisplayName(Colorize.color(closeSection.getString("name")));
                meta.setLore(Colorize.color(closeSection.getStringList("lore")));

                close.setItemMeta(meta);

            }

            gui.setItem(closeSection.getInt("slot"), close);

        }

        if (page > 0) {

            ConfigurationSection previousPageSection = previewSection.getConfigurationSection("content.previous_page");
            if (previousPageSection != null) {

                ItemStack previousPageItem = new ItemStack(Material.valueOf(previousPageSection.getString("type")));

                ItemMeta meta = previousPageItem.getItemMeta();
                if (meta != null) {

                    meta.setDisplayName(Colorize.color(previousPageSection.getString("name")));
                    meta.setLore(Colorize.color(previousPageSection.getStringList("lore")));

                    previousPageItem.setItemMeta(meta);

                }

                gui.setItem(previousPageSection.getInt("slot"), previousPageItem);

            }

        }

        if (page < totalPages - 1) {

            ConfigurationSection nextPageSection = previewSection.getConfigurationSection("content.next_page");
            if (nextPageSection != null) {

                ItemStack nextPageItem = new ItemStack(Material.valueOf(nextPageSection.getString("type")));

                ItemMeta meta = nextPageItem.getItemMeta();
                if (meta != null) {

                    meta.setDisplayName(Colorize.color(nextPageSection.getString("name")));
                    meta.setLore(Colorize.color(nextPageSection.getStringList("lore")));

                    nextPageItem.setItemMeta(meta);

                }

                gui.setItem(nextPageSection.getInt("slot"), nextPageItem);

            }

        }

        player.openInventory(gui);

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

    public boolean isPreview() {
        return preview;
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
