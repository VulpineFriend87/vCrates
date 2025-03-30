package pro.vulpine.vCrates.instance.reward;

import org.bukkit.Material;
import pro.vulpine.vCrates.instance.Rarity;

import java.util.List;

public class Reward {

    private final String identifier;
    private final String name;
    private final List<String> lore;
    private final Material displayItem;
    private final Rarity rarity;
    private final List<RewardItem> items;
    private final List<String> actions;

    public Reward(String identifier, String name, List<String> lore, Material displayItem, Rarity rarity, List<RewardItem> items, List<String> actions) {
        this.identifier = identifier;
        this.name = name;
        this.lore = lore;
        this.displayItem = displayItem;
        this.rarity = rarity;
        this.items = items;
        this.actions = actions;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public Material getDisplayItem() {
        return displayItem;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public List<RewardItem> getItems() {
        return items;
    }

    public List<String> getActions() {
        return actions;
    }

}
