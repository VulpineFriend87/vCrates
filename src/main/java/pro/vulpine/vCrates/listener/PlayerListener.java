package pro.vulpine.vCrates.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pro.vulpine.vCrates.VCrates;

public class PlayerListener implements Listener {

    private final VCrates plugin;

    public PlayerListener(VCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        plugin.getProfileManager().loadProfile(event.getPlayer().getUniqueId(), true);

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {

        plugin.getProfileManager().unloadProfile(event.getPlayer().getUniqueId());

    }

}
