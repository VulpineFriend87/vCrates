package pro.vulpine.vCrates.instance.milestone;

import org.bukkit.entity.Player;
import pro.vulpine.vCrates.instance.crate.Crate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Milestone {

    private Crate crate;

    private final int after;
    private final List<String> actions;
    private final MilestoneRepeats repeats;
    private final List<MilestoneReward> rewards;

    public Milestone(int after, List<String> actions, MilestoneRepeats repeats, List<MilestoneReward> rewards) {
        this.after = after;
        this.actions = actions;
        this.repeats = repeats;
        this.rewards = rewards;
    }

    public void setCrate(Crate crate) {
        this.crate = crate;
    }

    public void giveRewards(Player player) {

        for (MilestoneReward reward : rewards) {
            reward.giveReward(player);
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%crate%", crate.getName());
        placeholders.put("%player%", player.getName());
        placeholders.put("%times_opened%", String.valueOf(crate.getCrateManager().getPlugin().getProfileManager()
                .getProfile(player.getUniqueId()).getStatistic("crates-opened", crate.getIdentifier())));

        crate.getCrateManager().getPlugin().getActionParser().executeActions(
                actions, player, 0, placeholders
        );

    }

    public Crate getCrate() {
        return crate;
    }

    public List<String> getActions() {
        return actions;
    }

    public int getAfter() {
        return after;
    }

    public MilestoneRepeats getRepeats() {
        return repeats;
    }

    public List<MilestoneReward> getRewards() {
        return rewards;
    }

}