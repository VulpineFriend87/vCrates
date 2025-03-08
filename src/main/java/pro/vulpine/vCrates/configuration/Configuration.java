package pro.vulpine.vCrates.configuration;

import pro.vulpine.vCrates.utils.Colorize;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pro.vulpine.vCrates.VCrates;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Configuration {

    protected FileConfiguration config;
    protected File configFile;

    public Configuration(VCrates plugin, String fileName) {
        this.configFile = new File(plugin.getDataFolder(), fileName);

        if (!configFile.exists()) {
            plugin.saveResource(fileName, false);
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public String getString(String path) {
        return Colorize.color(config.getString(path));
    }

    public String getString(String path, String def) {
        return Colorize.color(config.getString(path, def));
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    public long getLong(String path) {
        return config.getLong(path);
    }

    public long getLong(String path, long def) {
        return config.getLong(path, def);
    }

    public List<String> getStringList(String path) {
        return Colorize.color(config.getStringList(path));
    }

    public Object get(String path) {
        return config.get(path);
    }

    public Object get(String path, Object def) {
        return config.get(path, def);
    }

    public ConfigurationSection getConfigurationSection(String path) {
        return config.getConfigurationSection(path);
    }

    public boolean contains(String path) {
        return config.contains(path);
    }

    public void createSection(String path, Map<?, ?> map) {
        config.createSection(path, map);
    }


    public Set<String> getKeys(boolean deep) {
        return config.getKeys(deep);
    }

    public ConfigurationSection getRoot() {
        return config.getRoot();
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

}
