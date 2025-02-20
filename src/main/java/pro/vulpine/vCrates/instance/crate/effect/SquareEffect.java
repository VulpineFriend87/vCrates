package pro.vulpine.vCrates.instance.crate.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import pro.vulpine.vCrates.instance.crate.CrateEffect;

public class SquareEffect implements Effect {

    private final CrateEffect crateEffect;

    public SquareEffect(CrateEffect crateEffect) {

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

                double p = (t / (Math.PI * 2)) * 4;
                int segment = (int) Math.floor(p);
                double fraction = p - segment;

                double x = 0;
                double z = 0;

                if (segment == 0) {

                    x = -radius + (2 * radius * fraction);
                    z = -radius;

                }
                else if (segment == 1) {

                    x = radius;
                    z = -radius + (2 * radius * fraction);

                }
                else if (segment == 2) {

                    x = radius - (2 * radius * fraction);
                    z = radius;

                }
                else if (segment == 3) {

                    x = -radius;
                    z = radius - (2 * radius * fraction);

                }

                Location loc = base.clone().add(x, 0, z);

                base.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 0);

                t += tIncrement;

            }

        }.runTaskTimer(crateEffect.getCrate().getCrateManager().getPlugin(), 0, 1);

    }

}
