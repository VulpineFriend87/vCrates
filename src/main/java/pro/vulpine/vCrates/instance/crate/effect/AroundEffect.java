package pro.vulpine.vCrates.instance.crate.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import pro.vulpine.vCrates.instance.crate.CrateEffect;

public class AroundEffect implements Effect {

    private final CrateEffect crateEffect;

    public AroundEffect(CrateEffect crateEffect) {

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

        new BukkitRunnable() {

            long tick = 0;

            @Override
            public void run() {

                if (tick >= totalTicks) {

                    cancel();
                    return;

                }

                double randomAngle = Math.random() * Math.PI * 2 + direction;
                double randomRadius = Math.random() * radius;
                double offsetX = randomRadius * Math.cos(randomAngle);
                double offsetZ = randomRadius * Math.sin(randomAngle);
                double offsetY = (Math.random() - 0.5) + yOffset;

                Location loc = base.clone().add(offsetX, offsetY, offsetZ);

                base.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 0);

                tick++;

            }

        }.runTaskTimer(crateEffect.getCrate().getCrateManager().getPlugin(), 0, 1);

    }

}
