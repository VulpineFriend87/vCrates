package pro.vulpine.vCrates.instance;

import org.bukkit.Material;

import java.util.List;

public class Reward {

    private String identifier;
    private String name;
    private Material displayItem;
    private List<RewardItem> items;
    private List<String> commands;

    public Reward(String identifier, String name, Material displayItem, List<RewardItem> items, List<String> commands) {
        this.identifier = identifier;
        this.name = name;
        this.displayItem = displayItem;
        this.items = items;
        this.commands = commands;
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

    public List<String> getCommands() {
        return commands;
    }

}
