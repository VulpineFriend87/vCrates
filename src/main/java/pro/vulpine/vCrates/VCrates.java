package pro.vulpine.vCrates;

import org.bukkit.plugin.java.JavaPlugin;
import pro.vulpine.vCrates.command.VCratesCommand;
import pro.vulpine.vCrates.configuration.CratesConfiguration;
import pro.vulpine.vCrates.configuration.KeysConfiguration;
import pro.vulpine.vCrates.configuration.MainConfiguration;
import pro.vulpine.vCrates.configuration.ResponsesConfiguration;
import pro.vulpine.vCrates.instance.StorageMethod;
import pro.vulpine.vCrates.listener.CrateListener;
import pro.vulpine.vCrates.listener.PlayerListener;
import pro.vulpine.vCrates.manager.CrateManager;
import pro.vulpine.vCrates.manager.KeyManager;
import pro.vulpine.vCrates.manager.ProfileManager;
import pro.vulpine.vCrates.manager.StorageManager;
import pro.vulpine.vCrates.utils.logger.Level;
import pro.vulpine.vCrates.utils.logger.Logger;
import pro.vulpine.vCrates.utils.ActionParser;

public final class VCrates extends JavaPlugin {

    private MainConfiguration mainConfiguration;
    private ResponsesConfiguration responsesConfiguration;
    private CratesConfiguration cratesConfiguration;
    private KeysConfiguration keysConfiguration;

    private ActionParser actionParser;

    private CrateManager crateManager;
    private KeyManager keyManager;

    private StorageManager storageManager;
    private ProfileManager profileManager;

    @Override
    public void onEnable() {

        mainConfiguration = new MainConfiguration(this);
        responsesConfiguration = new ResponsesConfiguration(this);
        cratesConfiguration = new CratesConfiguration(this);
        keysConfiguration = new KeysConfiguration(this);

        try {

            Logger.initialize(Level.valueOf(mainConfiguration.getString("logging_level", "INFO")));

        } catch (IllegalArgumentException e) {

            Logger.initialize(Level.INFO);

        }

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
            Logger.system(line);
        }

        actionParser = new ActionParser(this);

        crateManager = new CrateManager(this);
        keyManager = new KeyManager(this);

        storageManager = new StorageManager(this,
                StorageMethod.fromString(mainConfiguration.getString("storage.method", "H2")),
                mainConfiguration.getString("storage.host", "localhost"),
                mainConfiguration.getString("storage.port", "3306"),
                mainConfiguration.getString("storage.database", "vcrates"),
                mainConfiguration.getString("storage.username", "vcrates"),
                mainConfiguration.getString("storage.password", ""));

        profileManager = new ProfileManager(this);

        getCommand("vcrates").setExecutor(new VCratesCommand(this));

        getServer().getPluginManager().registerEvents(new CrateListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

    }

    @Override
    public void onDisable() {
        crateManager.unloadCrates();
        storageManager.close();
    }

    public MainConfiguration getMainConfiguration() {
        return mainConfiguration;
    }

    public ResponsesConfiguration getResponsesConfiguration() {
        return responsesConfiguration;
    }

    public CratesConfiguration getCratesConfiguration() {
        return cratesConfiguration;
    }

    public KeysConfiguration getKeysConfiguration() {
        return keysConfiguration;
    }

    public ActionParser getActionParser() {
        return actionParser;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }
}
