package pro.vulpine.vCrates.instance;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class Crate {

    private String identifier;
    private String name;
    private List<Location> blocks;
    private List<Reward> rewards;

    public Crate(String identifier, String name, List<Location> blocks, List<Reward> rewards) {
        this.identifier = identifier;
        this.name = name;
        this.blocks = blocks;
        this.rewards = rewards;
    }

    public void open(Player player) {

        Reward reward = rewards.get((int) (Math.random() * rewards.size()));

        for (RewardItem item : reward.getItems()) {
            player.getInventory().addItem(RewardItem.toItemStack(item));
        }

        for (String command : reward.getCommands()) {
            player.performCommand(command);
        }

    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public List<Location> getBlocks() {
        return blocks;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

}