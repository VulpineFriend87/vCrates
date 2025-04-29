package pro.vulpine.vCrates.instance.milestone;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pro.vulpine.vCrates.instance.reward.RewardItem;

import java.util.List;

public class MilestoneReward {
    private final String name;
    private final List<RewardItem> items;

    public MilestoneReward(String name, List<RewardItem> items) {
        this.name = name;
        this.items = items;
    }

    public void giveReward(Player player) {
        player.getInventory().addItem(items.stream().map(RewardItem::toItemStack).toArray(ItemStack[]::new));
    }

    public String getName() {
        return name;
    }

    public List<RewardItem> getItems() {
        return items;
    }

}
