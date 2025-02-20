package pro.vulpine.vCrates.instance.crate.effect;

import org.bukkit.Location;
import org.bukkit.Particle;
import pro.vulpine.vCrates.instance.crate.CrateEffect;

public class AuraEffect implements Effect {

    private final CrateEffect crateEffect;

    public AuraEffect(CrateEffect crateEffect) {

        this.crateEffect = crateEffect;

    }

    @Override
    public void play(Location location) {

        Location base = location.clone().add(0.5, 0, 0.5);

        Particle particle = crateEffect.getParticle();
        double radius = crateEffect.getRadius();
        double speed = crateEffect.getSpeed();

        int phiSteps = 10;
        int thetaSteps = 20;

        for (int i = 0; i <= phiSteps; i++) {

            double phi = Math.PI * i / phiSteps;

            for (int j = 0; j < thetaSteps; j++) {

                double theta = 2 * Math.PI * j / thetaSteps;

                double x = radius * Math.sin(phi) * Math.cos(theta);
                double y = radius * Math.cos(phi);
                double z = radius * Math.sin(phi) * Math.sin(theta);

                Location loc = base.clone().add(x, y, z);

                base.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 0);

            }

        }

    }

}
