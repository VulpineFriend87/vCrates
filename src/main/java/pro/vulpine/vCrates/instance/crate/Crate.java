package pro.vulpine.vCrates.instance.crate;

import it.vulpinefriend87.easyutils.Colorize;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pro.vulpine.vCrates.instance.Profile;
import pro.vulpine.vCrates.manager.CrateManager;
import pro.vulpine.vCrates.utils.KeyUtils;

import java.util.*;

public class Crate {

    private final CrateManager crateManager;

    private final CrateKeys crateKeys;
    private final CratePushback cratePushback;
    private final CrateHologram crateHologram;

    private final String identifier;
    private final String name;
    private final int cooldown;
    private final List<Location> blocks;
    private final List<Reward> rewards;
    private final Map<UUID, Long> cooldowns;

    public Crate(CrateManager crateManager, CrateKeys crateKeys, CratePushback cratePushback, CrateHologram crateHologram, String identifier, String name, int cooldown, List<Location> blocks, List<Reward> rewards) {
        this.crateManager = crateManager;

        this.crateKeys = crateKeys;
        this.cratePushback = cratePushback;
        this.crateHologram = crateHologram;

        this.identifier = identifier;
        this.name = name;
        this.cooldown = cooldown;
        this.blocks = blocks;
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
        placeholders.put("%cooldown%", String.valueOf(getRemainingCooldown(player)));
        placeholders.put("%player%", player.getName());

        Boolean usedVirtualKey = null;

        if (crateKeys.isRequired()) {

            if (!crateKeys.isKeyAllowed(KeyUtils.getKeyIdentifier(player.getInventory().getItemInMainHand()))) {

                if (!profile.hasKey(crateKeys.getAllowedKeys())) {

                    crateManager.getPlugin().getActionParser().executeActions(
                            crateManager.getPlugin().getResponsesConfiguration().getStringList("keys.missing"),
                            player, 0, placeholders
                    );

                    if (cratePushback.isEnabled()) {
                        cratePushback.execute(player, cratePushback.getYOffset(), cratePushback.getMultiply());
                    }

                    return;

                } else {

                    usedVirtualKey = true;

                }

            } else {

                usedVirtualKey = false;

            }

        }

        if (isCooldownActive(player)) {

            crateManager.getPlugin().getActionParser().executeActions(
                    crateManager.getPlugin().getResponsesConfiguration().getStringList("crates.cooldown"),
                    player, 0, placeholders
            );

            return;
        }

        if (!usedVirtualKey) {

            ItemStack key = player.getInventory().getItemInMainHand();

            player.getInventory().getItemInMainHand().setAmount(key.getAmount() - 1);

        } else {

            profile.useKey(crateKeys.getAllowedKeys());

        }

        Reward reward = rewards.get((int) (Math.random() * rewards.size()));

        for (RewardItem item : reward.getItems()) {
            player.getInventory().addItem(RewardItem.toItemStack(item));
        }

        crateManager.getPlugin().getActionParser().executeActions(reward.getActions(), player, 0, placeholders);

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

    public List<Reward> getRewards() {
        return rewards;
    }

}