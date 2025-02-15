package pro.vulpine.vCrates.instance.milestone;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pro.vulpine.vCrates.instance.reward.RewardItem;

import java.util.List;

public class MilestoneReward {
    private final String name;
    private final Material displayItem;
    private final List<RewardItem> items;

    public MilestoneReward(String name, Material displayItem, List<RewardItem> items) {
        this.name = name;
        this.displayItem = displayItem;
        this.items = items;
    }

    public void giveReward(Player player) {
        player.getInventory().addItem(items.stream().map(RewardItem::toItemStack).toArray(ItemStack[]::new));
    }

    public String getName() {
        return name;
    }

    public Material getDisplayItem() {
        return displayItem;
    }

    public List<RewardItem> getItems() {
        return items;
    }

}
