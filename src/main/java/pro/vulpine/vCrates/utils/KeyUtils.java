package pro.vulpine.vCrates.utils;

import it.vulpinefriend87.easyutils.Colorize;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class KeyUtils {

    private static final NamespacedKey keyIdentifier = new NamespacedKey("vcrates", "identifier");

    public static ItemStack generateKey(String identifier, String name, Material item) {

        ItemStack key = new ItemStack(item, 1);

        ItemMeta meta = key.getItemMeta();

        meta.setDisplayName(Colorize.color(name));

        meta.getPersistentDataContainer().set(keyIdentifier, PersistentDataType.STRING, identifier);

        key.setItemMeta(meta);

        return key;

    }

    public static boolean isValidKey(ItemStack key, String identifier) {

        ItemMeta meta = key.getItemMeta();

        boolean hasIdentifier = meta.getPersistentDataContainer().has(keyIdentifier, PersistentDataType.STRING);

        String storedIdentifier = hasIdentifier ? meta.getPersistentDataContainer().get(keyIdentifier, PersistentDataType.STRING) : null;

        boolean matchesIdentifier = storedIdentifier != null && storedIdentifier.equals(identifier);

        return hasIdentifier && matchesIdentifier;

    }

    public static String getKeyIdentifier(ItemStack key) {

        ItemMeta meta = key.getItemMeta();

        if (meta == null) return null;

        return meta.getPersistentDataContainer().get(keyIdentifier, PersistentDataType.STRING);

    }

}
