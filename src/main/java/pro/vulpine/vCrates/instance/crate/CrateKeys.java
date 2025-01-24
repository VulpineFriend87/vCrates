package pro.vulpine.vCrates.instance.crate;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pro.vulpine.vCrates.utils.KeyUtils;

import java.util.List;

public class CrateKeys {

    private final boolean required;
    private final List<String> allowedKeys;

    public CrateKeys(boolean required, List<String> allowedKeys) {
        this.required = required;
        this.allowedKeys = allowedKeys;
    }

    public String getKeyIdentifier(ItemStack key) {

        ItemMeta meta = key.getItemMeta();

        if (meta == null) return null;

        return meta.getPersistentDataContainer().get(KeyUtils.getKeyIdentifier(), PersistentDataType.STRING);

    }

    public boolean isRequired() {
        return required;
    }

    public List<String> getAllowedKeys() {
        return allowedKeys;
    }

    public boolean isKeyAllowed(String identifier) {
        return allowedKeys.contains(identifier);
    }

}
