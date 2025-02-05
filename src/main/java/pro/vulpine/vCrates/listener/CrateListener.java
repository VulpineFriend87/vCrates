package pro.vulpine.vCrates.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.instance.crate.Crate;

public class CrateListener implements Listener {

    private final VCrates plugin;

    public CrateListener(VCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getHand() == EquipmentSlot.OFF_HAND) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (event.getClickedBlock() != null) {

                Crate crate = plugin.getCrateManager().getCrate(event.getClickedBlock().getLocation());

                if (crate != null) {

                    event.setCancelled(true);

                    crate.open(event.getPlayer());

                }

            }
        }
    }

}
