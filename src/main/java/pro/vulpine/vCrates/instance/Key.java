package pro.vulpine.vCrates.instance;

import org.bukkit.Material;

public class Key {

    private final boolean virtualAllowed;
    private final String identifier;
    private final String name;
    private final Material item;

    public Key(boolean virtualAllowed, String identifier, String name, Material item) {
        this.virtualAllowed = virtualAllowed;
        this.identifier = identifier;
        this.name = name;
        this.item = item;
    }

    public boolean isVirtualAllowed() {
        return virtualAllowed;
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
