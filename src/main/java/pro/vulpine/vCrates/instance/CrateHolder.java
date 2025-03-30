package pro.vulpine.vCrates.instance;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import pro.vulpine.vCrates.instance.crate.Crate;

public class CrateHolder implements InventoryHolder {

    private final Crate crate;

    private final String identifier;
    private final int page;
    private final int totalPages;

    public CrateHolder(Crate crate, String identifier, int page, int totalPages) {
        this.crate = crate;

        this.identifier = identifier;
        this.page = page;
        this.totalPages = totalPages;
    }

    public Crate getCrate() {
        return crate;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
