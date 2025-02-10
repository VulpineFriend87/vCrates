package pro.vulpine.vCrates.instance.reward;

import org.bukkit.Material;

import java.util.List;

public class Reward {

    private final String identifier;
    private final String name;
    private final Material displayItem;
    private final List<RewardItem> items;
    private final List<String> actions;

    public Reward(String identifier, String name, Material displayItem, List<RewardItem> items, List<String> actions) {
        this.identifier = identifier;
        this.name = name;
        this.displayItem = displayItem;
        this.items = items;
        this.actions = actions;
    }

    public String getIdentifier() {
        return identifier;
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

    public List<String> getActions() {
        return actions;
    }

}
