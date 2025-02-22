package pro.vulpine.vCrates.instance.crate;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import pro.vulpine.vCrates.instance.crate.effect.*;

import java.util.HashMap;
import java.util.Map;

public class CrateEffect extends BukkitRunnable {

    private Crate crate;

    private final boolean enabled;

    private final String type;
    private final Particle particle;

    private final double radius;
    private final double speed;

    private final Map<String, Effect> effects = new HashMap<>();

    public CrateEffect(boolean enabled, String type, Particle particle, double radius, double speed) {

        this.enabled = enabled;

        this.type = type;
        this.particle = particle;

        this.radius = radius;
        this.speed = speed;

        effects.put("HELIX", new HelixEffect(this));
        effects.put("SPIRAL", new SpiralEffect(this));
        effects.put("VORTEX", new VortexEffect(this));
        effects.put("RING", new RingEffect(this));
        effects.put("AURA", new AuraEffect(this));
        effects.put("CIRCLE", new CircleEffect(this));
        effects.put("SQUARE", new SquareEffect(this));
    }

    public void setCrate(Crate crate) {
        this.crate = crate;

        long periodTicks = Math.max(1, (long)(speed * 20));
        runTaskTimer(crate.getCrateManager().getPlugin(), 0L, periodTicks);
    }

    @Override
    public void run() {

        if (!enabled) {
            return;
        }

        Effect effect = effects.get(type);

        if (effect == null) {
            return;
        }

        for (Location block : crate.getBlocks()) {

            effect.play(block);
        }

    }

    public Crate getCrate() {
        return crate;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getType() {
        return type;
    }

    public Particle getParticle() {
        return particle;
    }

    public double getRadius() {
        return radius;
    }

    public double getSpeed() {
        return speed;
    }

    public Map<String, Effect> getEffects() {
        return effects;
    }
}
