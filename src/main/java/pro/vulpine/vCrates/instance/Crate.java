package pro.vulpine.vCrates.instance;

import it.vulpinefriend87.easyutils.Colorize;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pro.vulpine.vCrates.manager.CrateManager;

import java.util.*;

public class Crate {

    private final CrateManager crateManager;

    private final CrateKeys crateKeys;

    private final String identifier;
    private final String name;
    private final int cooldown;
    private final List<Location> blocks;
    private final List<Reward> rewards;
    private final Map<UUID, Long> cooldowns;

    public Crate(CrateManager crateManager, CrateKeys crateKeys, String identifier, String name, int cooldown, List<Location> blocks, List<Reward> rewards) {
        this.crateManager = crateManager;

        this.crateKeys = crateKeys;

        this.identifier = identifier;
        this.name = name;
        this.cooldown = cooldown;
        this.blocks = blocks;
        this.rewards = rewards;
        this.cooldowns = new HashMap<>();
    }

    public void open(Player player, String keyIdentifier) {

        if (!crateKeys.isKeyAllowed(keyIdentifier) || keyIdentifier == null) {
            player.sendMessage(Colorize.color(
                    crateManager.getPlugin().getMessagesConfiguration().getString("keys.missing")
            ));
            return;
        }

        if (!isCooledDown(player)) {
            player.sendMessage(Colorize.color(
                    crateManager.getPlugin().getMessagesConfiguration().getString("crates.cooldown")
                            .replace("%time%", String.valueOf(getRemainingCooldown(player)))
            ));
            return;
        }

        Reward reward = rewards.get((int) (Math.random() * rewards.size()));

        for (RewardItem item : reward.getItems()) {
            player.getInventory().addItem(RewardItem.toItemStack(item));
        }

        crateManager.getPlugin().getActionParser().executeActions(reward.getActions(), player, 0);

        if (cooldown > 0) {
            setCooldown(player);
        }

    }

    private double getRemainingCooldown(Player player) {
        long remaining = cooldown - (System.currentTimeMillis() - cooldowns.get(player.getUniqueId()));
        return (double) Math.round(remaining / 1000.0 * 100.0) / 100.0;
    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private boolean isCooledDown(Player player) {

        if (cooldowns.containsKey(player.getUniqueId())) {
            return System.currentTimeMillis() - cooldowns.get(player.getUniqueId()) > cooldown;
        }

        return true;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public CrateKeys getCrateKeys() {
        return crateKeys;
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