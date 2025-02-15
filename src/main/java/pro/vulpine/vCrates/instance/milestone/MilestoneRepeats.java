package pro.vulpine.vCrates.instance.milestone;

public class MilestoneRepeats {

    private final boolean enabled;
    private final int times;

    public MilestoneRepeats(boolean enabled, int times) {
        this.enabled = enabled;
        this.times = times;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getTimes() {
        return times;
    }

}
