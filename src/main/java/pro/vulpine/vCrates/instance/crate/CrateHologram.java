package pro.vulpine.vCrates.instance.crate;

import it.vulpinefriend87.easyutils.Colorize;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class CrateHologram {

    private final boolean enabled;
    private final List<Location> blocks;
    private final List<String> lines;
    private final double yOffset;

    private final List<ArmorStand> armorStands = new ArrayList<>();

    public CrateHologram(boolean enabled, List<Location> blocks, List<String> lines, double yOffset) {
        this.enabled = enabled;
        this.blocks = blocks;
        this.lines = lines;
        this.yOffset = yOffset;

        if (enabled) {
            spawnHolograms();
        }
    }

    private void spawnHolograms() {

        remove();

        for (Location location : blocks) {

            spawn(location);

        }

    }

    private void spawn(Location location) {

        double lineSpacing = 0.3;
        double baseHeightOffset = 0.8;

        for (int i = 0; i < lines.size(); i++) {

            double yOffset = this.yOffset + baseHeightOffset + (lines.size() - 1 - i) * lineSpacing;
            Location hologramLocation = location.clone().add(0.5, yOffset, 0.5);

            ArmorStand armorStand = spawnHologramLine(hologramLocation, lines.get(i));
            armorStands.add(armorStand);

        }

    }

    private ArmorStand spawnHologramLine(Location location, String text) {

        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

        armorStand.setCustomName(Colorize.color(text));
        armorStand.setCustomNameVisible(true);

        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setMarker(true);
        armorStand.setInvulnerable(true);

        return armorStand;

    }

    public void remove() {
        for (ArmorStand armorStand : armorStands) {

            if (!armorStand.isDead()) {
                armorStand.remove();
            }

        }

        armorStands.clear();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<Location> getBlocks() {
        return blocks;
    }

    public List<String> getLines() {
        return lines;
    }

    public double getYOffset() {
        return yOffset;
    }

    public List<ArmorStand> getArmorStands() {
        return armorStands;
    }
}
