package pro.vulpine.vCrates.instance;

import org.bukkit.Material;

public class Key {

    private final String identifier;
    private final String name;
    private final Material item;

    public Key(String identifier, String name, Material item) {
        this.identifier = identifier;
        this.name = name;
        this.item = item;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public Material getItem() {
        return item;
    }

}
