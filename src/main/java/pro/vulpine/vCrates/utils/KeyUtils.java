package pro.vulpine.vCrates.utils;

import it.vulpinefriend87.easyutils.Colorize;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class KeyUtils {

    private static final NamespacedKey keyIdentifier = new NamespacedKey("vcrates_key", "identifier");

    public static ItemStack generateKey(String identifier, String name, Material item) {

        ItemStack key = new ItemStack(item, 1);

        ItemMeta meta = key.getItemMeta();

        meta.setDisplayName(Colorize.color(name));

        meta.getPersistentDataContainer().set(keyIdentifier, PersistentDataType.STRING, identifier);

        key.setItemMeta(meta);

        return key;

    }

    public static NamespacedKey getKeyIdentifier() {
        return keyIdentifier;
    }
}
