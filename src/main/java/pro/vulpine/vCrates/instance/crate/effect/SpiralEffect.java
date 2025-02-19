package pro.vulpine.vCrates.instance.crate.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import pro.vulpine.vCrates.instance.crate.CrateEffect;

public class SpiralEffect implements Effect {

    private final CrateEffect crateEffect;

    public SpiralEffect(CrateEffect crateEffect) {
        this.crateEffect = crateEffect;
    }

    @Override
    public void play(Location location) {

        Location base = location.clone().add(0.5, 0, 0.5);

        Particle particle = crateEffect.getParticle();
        double radius = crateEffect.getRadius();
        double speed = crateEffect.getSpeed();

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

                double x = radius * Math.cos(t);
                double z = radius * Math.sin(t);

                Location loc = base.clone().add(x, t / Math.PI, z);

                base.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 0);

                t += tIncrement;
            }
        }.runTaskTimer(crateEffect.getCrate().getCrateManager().getPlugin(), 0, 1);
    }
}
