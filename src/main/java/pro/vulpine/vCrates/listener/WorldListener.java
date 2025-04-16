package pro.vulpine.vCrates.listener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.instance.crate.Crate;
import pro.vulpine.vCrates.utils.logger.Logger;

public class WorldListener implements Listener {

    private final VCrates plugin;

    public WorldListener(VCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {

        World loadedWorld = event.getWorld();

        Logger.info("LOADED WORLD: " + loadedWorld.getName(), "WorldListener");

        for (Crate crate : plugin.getCrateManager().getCrates()) {

            Logger.info("HANDLING CRATE: " + crate.getName(), "WorldListener");

            for (Location block : crate.getBlocks()) {

                Logger.info("HANDLING BLOCK: " + block.toString(), "WorldListener");
                Logger.info("BLOCK WORLD: " + block.getWorld(), "WorldListener");

                if (block.getWorld() != null && block.getWorld().equals(loadedWorld)) {

                    crate.getCrateHologram().spawn(block);
                    Logger.info("SPAWNED HOLOGRAM FOR CRATE: " + crate.getIdentifier(), "WorldListener");
                    break;

                }

            }

        }
    }

    public VCrates getPlugin() {
        return plugin;
    }
}
