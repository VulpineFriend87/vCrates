package pro.vulpine.vCrates.instance.crate.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import pro.vulpine.vCrates.instance.crate.CrateEffect;

public class RingEffect implements Effect {

    private final CrateEffect crateEffect;

    public RingEffect(CrateEffect crateEffect) {

        this.crateEffect = crateEffect;

    }

    @Override
    public void play(Location location) {

        Location base = location.clone().add(0.5, 0, 0.5);

        Particle particle = crateEffect.getParticle();
        double radius = crateEffect.getRadius();
        double speed = crateEffect.getSpeed();
        double yOffset = crateEffect.getYOffset();
        double direction = Math.toRadians(crateEffect.getDirection());  // Convert degrees to radians

        long totalTicks = Math.max(1, (long)(speed * 20));

        double tIncrement = (Math.PI * 2) / totalTicks;

        new BukkitRunnable() {

            double t = 0;

            @Override
            public void run() {

                if (t > Math.PI * 2) {

                    cancel();
                    return;

                }

                // Calculate position in a vertical ring
                double angle = t;
                
                // Y component changes with sine
                double y = radius * Math.sin(angle) + yOffset;
                
                // X and Z components depend on direction
                double horizontalComponent = radius * Math.cos(angle);
                double x = horizontalComponent * Math.cos(direction);
                double z = horizontalComponent * Math.sin(direction);

                Location loc = base.clone().add(x, y, z);

                base.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 0);

                t += tIncrement;

            }

        }.runTaskTimer(crateEffect.getCrate().getCrateManager().getPlugin(), 0, 1);

    }

}
