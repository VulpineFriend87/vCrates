package pro.vulpine.vCrates.instance.crate.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import pro.vulpine.vCrates.instance.crate.CrateEffect;

public class VortexEffect implements Effect {

    private final CrateEffect crateEffect;

    public VortexEffect(CrateEffect crateEffect) {
        this.crateEffect = crateEffect;
    }

    @Override
    public void play(Location location) {

        Location base = location.clone().add(0.5, 0, 0.5);

        Particle particle = crateEffect.getParticle();
        double radius = crateEffect.getRadius();
        double speed = crateEffect.getSpeed();
        double yOffset = crateEffect.getYOffset();

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

                double currentRadius = radius * (1 - (t / (Math.PI * 2)));
                double x = currentRadius * Math.cos(t);
                double z = currentRadius * Math.sin(t);

                Location loc1 = base.clone().add(x, (t / Math.PI) + yOffset, z);
                Location loc2 = base.clone().add(-x, (t / Math.PI) + yOffset, -z);

                base.getWorld().spawnParticle(particle, loc1, 1, 0, 0, 0, 0);
                base.getWorld().spawnParticle(particle, loc2, 1, 0, 0, 0, 0);

                t += tIncrement;
            }
        }.runTaskTimer(crateEffect.getCrate().getCrateManager().getPlugin(), 0, 1);
    }
}
