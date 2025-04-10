package pro.vulpine.vCrates.instance.crate.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import pro.vulpine.vCrates.instance.crate.CrateEffect;

public class HelixEffect implements Effect {

    private final CrateEffect crateEffect;

    public HelixEffect(CrateEffect crateEffect) {

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

                double angle1 = t + direction;
                double angle2 = t + Math.PI + direction;
                double x1 = radius * Math.cos(angle1);
                double z1 = radius * Math.sin(angle1);
                double x2 = radius * Math.cos(angle2);
                double z2 = radius * Math.sin(angle2);

                Location loc1 = base.clone().add(x1, (t / Math.PI) + yOffset, z1);
                Location loc2 = base.clone().add(x2, (t / Math.PI) + yOffset, z2);

                base.getWorld().spawnParticle(particle, loc1, 1, 0, 0, 0, 0);
                base.getWorld().spawnParticle(particle, loc2, 1, 0, 0, 0, 0);

                t += tIncrement;

            }

        }.runTaskTimer(crateEffect.getCrate().getCrateManager().getPlugin(), 0, 1);

    }

}
