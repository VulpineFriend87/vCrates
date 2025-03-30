package pro.vulpine.vCrates.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.instance.CrateHolder;
import pro.vulpine.vCrates.instance.crate.Crate;

public class CrateListener implements Listener {

    private final VCrates plugin;

    public CrateListener(VCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if(event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        Crate crate;

        if (event.getClickedBlock() != null) {

            crate = plugin.getCrateManager().getCrate(event.getClickedBlock().getLocation());

        } else {

            return;

        }

        if (crate != null) {

            event.setCancelled(true);

        } else {

            return;

        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            crate.open(event.getPlayer());

        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {

            crate.preview(event.getPlayer(), 0);

        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (inventoryHolder == null) {
            return;
        }

        int slot = event.getRawSlot();

        if (inventoryHolder instanceof CrateHolder) {

            CrateHolder holder = (CrateHolder) inventoryHolder;

            Crate crate = holder.getCrate();
            if (crate == null) {
                return;
            }

            String identifier = holder.getIdentifier();
            if (identifier == null) {
                return;
            }

            int page = holder.getPage();
            int totalPages = holder.getTotalPages();

            if (identifier.equals("crate_preview")) {

                event.setCancelled(true);

                int closeSlot = plugin.getMainConfiguration().getInt("gui.preview.content.close.slot");
                if (slot == closeSlot) {
                    event.getWhoClicked().closeInventory();
                }

                if (page > 0) {

                    int previousPageSlot = plugin.getMainConfiguration().getInt("gui.preview.content.previous_page.slot");
                    if (slot == previousPageSlot) {
                        crate.preview(player, page - 1);
                    }

                }

                if (page < totalPages - 1) {

                    int nextPageSlot = plugin.getMainConfiguration().getInt("gui.preview.content.next_page.slot");
                    if (slot == nextPageSlot) {
                        crate.preview(player, page + 1);
                    }

                }

            }

        }

    }

}
