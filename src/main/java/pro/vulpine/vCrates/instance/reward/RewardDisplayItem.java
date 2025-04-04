package pro.vulpine.vCrates.instance.reward;

import org.bukkit.Material;

import java.util.List;

public class RewardDisplayItem {

    private final Material type;
    private final int amount;
    private final List<String> lore;

    public RewardDisplayItem(Material type, int amount, List<String> lore) {
        this.type = type;
        this.amount = amount;
        this.lore = lore;
    }

    public Material getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public List<String> getLore() {
        return lore;
    }

}
