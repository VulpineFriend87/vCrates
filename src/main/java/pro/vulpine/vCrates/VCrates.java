package pro.vulpine.vCrates;

import org.bukkit.plugin.java.JavaPlugin;
import pro.vulpine.vCrates.configuration.CratesConfiguration;
import pro.vulpine.vCrates.configuration.MainConfiguration;
import pro.vulpine.vCrates.configuration.MessagesConfiguration;
import pro.vulpine.vCrates.listener.CrateListener;
import pro.vulpine.vCrates.manager.CrateManager;
import pro.vulpine.vCrates.utils.Logger;
import pro.vulpine.vCrates.utils.ActionParser;

public final class VCrates extends JavaPlugin {

    private MainConfiguration mainConfiguration;
    private MessagesConfiguration messagesConfiguration;
    private CratesConfiguration cratesConfiguration;

    private ActionParser actionParser;

    private CrateManager crateManager;

    @Override
    public void onEnable() {

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

        mainConfiguration = new MainConfiguration(this);
        messagesConfiguration = new MessagesConfiguration(this);
        cratesConfiguration = new CratesConfiguration(this);

        actionParser = new ActionParser(this);

        crateManager = new CrateManager(this);

        getServer().getPluginManager().registerEvents(new CrateListener(this), this);

    }

    public MainConfiguration getMainConfiguration() {
        return mainConfiguration;
    }


    public MessagesConfiguration getMessagesConfiguration() {
        return messagesConfiguration;
    }

    public CratesConfiguration getCratesConfiguration() {
        return cratesConfiguration;
    }

    public ActionParser getActionParser() {
        return actionParser;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

}
