package pro.vulpine.vCrates;

import org.bukkit.plugin.java.JavaPlugin;
import pro.vulpine.vCrates.listener.CrateListener;
import pro.vulpine.vCrates.manager.CrateManager;
import pro.vulpine.vCrates.utils.Logger;

public final class VCrates extends JavaPlugin {

    private CrateManager crateManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        String[] ascii = {
                "",
                "&b          _________                __                 ",
                "&3    ___  _&b\\_   ___ \\____________ _/  |_  ____   ______",
                "&3    \\  \\/ /&b    \\  \\/\\_  __ \\__  \\\\   __\\/ __ \\ /  ___/",
                "&3     \\   /&b\\     \\____|  | \\// __ \\|  | \\  ___/ \\___ \\ ",
                "&3      \\_/  &b\\______  /|__|  (____  /__|  \\___  >____  >",
                "&b                  \\/            \\/          \\/     \\/ ",
                "",
                "&3    By &b" + String.join(", ", getDescription().getAuthors()),
                "&3    Version &b" + getDescription().getVersion(),
                ""
        };

        for (String line : ascii) {
            Logger.info(line);
        }

        crateManager = new CrateManager(this);

        getServer().getPluginManager().registerEvents(new CrateListener(this), this);

    }

    public CrateManager getCrateManager() {
        return crateManager;
    }
}
