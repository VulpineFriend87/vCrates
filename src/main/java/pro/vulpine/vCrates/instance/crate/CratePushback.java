package pro.vulpine.vCrates.instance.crate;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CratePushback {

    private final boolean enabled;

    private final double yOffset;
    private final double multiply;

    public CratePushback(boolean enabled, double yOffset, double multiply) {
        this.enabled = enabled;

        this.yOffset = yOffset;
        this.multiply = multiply;
    }

    public void execute(Player player, double yOffset, double multiply) {

        Vector direction = player.getEyeLocation().getDirection();
        direction.setY(yOffset);
        direction = direction.multiply(multiply);

        player.setVelocity(direction);

    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getYOffset() {
        return yOffset;
    }

    public double getMultiply() {
        return multiply;
    }

}
