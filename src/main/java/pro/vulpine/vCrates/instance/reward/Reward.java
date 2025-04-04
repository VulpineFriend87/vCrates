package pro.vulpine.vCrates.instance.reward;

import pro.vulpine.vCrates.instance.Rarity;

import java.util.List;

public class Reward {

    private final String identifier;
    private final String name;
    private final RewardDisplayItem rewardDisplayItem;
    private final Rarity rarity;
    private final List<RewardItem> items;
    private final List<String> actions;

    public Reward(String identifier, String name, RewardDisplayItem rewardDisplayItem, Rarity rarity, List<RewardItem> items, List<String> actions) {
        this.identifier = identifier;
        this.name = name;
        this.rewardDisplayItem = rewardDisplayItem;
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

    public RewardDisplayItem getRewardDisplayItem() {
        return rewardDisplayItem;
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
