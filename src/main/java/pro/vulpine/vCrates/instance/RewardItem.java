package pro.vulpine.vCrates.instance;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RewardItem {

    private String name;
    private List<String> lore;
    private Material type;
    private int amount;

    public RewardItem(String name, List<String> lore, Material type, int amount) {
        this.name = name;
        this.lore = lore;
        this.type = type;
        this.amount = amount;
    }

    public static ItemStack toItemStack(RewardItem rewardItem) {

        ItemStack item = new ItemStack(rewardItem.getType(), rewardItem.getAmount());

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(rewardItem.getName());
        meta.setLore(rewardItem.getLore());

        item.setItemMeta(meta);

        return item;

    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public Material getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

}
