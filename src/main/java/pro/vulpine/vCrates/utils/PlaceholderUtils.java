package pro.vulpine.vCrates.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderUtils {

    public static String replace(Player player, String input) {

        if (input == null) {
            return null;
        }

        return PlaceholderAPI.setPlaceholders(player, input);

    }

    public static String replace(OfflinePlayer player, String input) {

        if (input == null) {
            return null;
        }

        return PlaceholderAPI.setPlaceholders(player, input);

    }

}
