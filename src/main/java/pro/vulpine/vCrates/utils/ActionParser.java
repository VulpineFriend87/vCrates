package pro.vulpine.vCrates.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.utils.logger.Logger;

import java.util.List;
import java.util.Map;

public class ActionParser {

    private final VCrates plugin;

    public ActionParser(VCrates plugin) {
        this.plugin = plugin;
    }

    public void executeActions(List<String> actions, Player player, int currentIndex, Map<String, String> placeholders) {

        for (int i = currentIndex; i < actions.size(); i++) {

            String actionString = actions.get(i);

            try {

                int closingBracketIndex = actionString.lastIndexOf("]");
                if (closingBracketIndex == -1) {
                    Logger.warn("Invalid action format: " + actionString, "ActionParser");
                    continue;
                }

                String action = actionString.substring(1, closingBracketIndex).trim();
                String params = actionString.substring(closingBracketIndex + 1).trim();

                switch (action) {

                    case "COMMAND":
                        executeCommand(params, player);
                        break;

                    case "TITLE":
                        executeTitle(params, player, placeholders);
                        break;

                    case "ACTIONBAR":
                        executeActionBar(params, player, placeholders);
                        break;

                    case "MESSAGE":
                        executeMessage(params, player, placeholders);
                        break;

                    case "SOUND":
                        executeSound(params, player);
                        break;

                    case "DELAY":
                        executeDelay(params, actions, player, i + 1, placeholders);
                        return;

                    default:
                        Logger.warn("Unknown action: " + action, "ActionParser");
                }

            } catch (Exception e) {

                Logger.warn("Failed to execute action: " + actionString, "ActionParser");
                e.printStackTrace();

            }

        }

    }

    private void executeCommand(String params, Player player) {

        String[] parts = params.split(";", 2);

        String target = parts[0].trim();
        String command = parts[1].trim().replace("%player%", player.getName());

        if (target.equalsIgnoreCase("console")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        } else if (target.equalsIgnoreCase("player")) {
            player.performCommand(command);
        }

    }

    private void executeTitle(String params, Player player, Map<String, String> placeholders) {

        String[] parts = params.split(";");

        String target = parts[0].trim();

        String title = parts.length > 1 ? Colorize.color(parts[1].trim()) : "";
        String subtitle = parts.length > 2 ? Colorize.color(parts[2].trim()) : "";
        int fadeIn = parts.length > 3 ? Integer.parseInt(parts[3].trim()) : 10;
        int stay = parts.length > 4 ? Integer.parseInt(parts[4].trim()) : 40;
        int fadeOut = parts.length > 5 ? Integer.parseInt(parts[5].trim()) : 10;

        title = replacePlaceholders(player, title, placeholders);
        subtitle = replacePlaceholders(player, subtitle, placeholders);

        if (target.equalsIgnoreCase("global")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
            }
        } else if (target.equalsIgnoreCase("player")) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }

    }

    private void executeActionBar(String params, Player player, Map<String, String> placeholders) {

        String[] parts = params.split(";", 2);

        String target = parts[0].trim();
        String message = Colorize.color(replacePlaceholders(player, parts[1].trim(), placeholders));

        if (target.equalsIgnoreCase("global")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(message));
            }
        } else if (target.equalsIgnoreCase("player")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(message));
        }

    }

    private void executeMessage(String params, Player player, Map<String, String> placeholders) {

        String[] parts = params.split(";", 2);

        String target = parts[0].trim();
        String message = Colorize.color(parts[1].trim());

        message = replacePlaceholders(player, message, placeholders);

        if (target.equalsIgnoreCase("global")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(message);
            }
        } else if (target.equalsIgnoreCase("player")) {
            player.sendMessage(message);
        }

    }

    private void executeSound(String params, Player player) {

        String[] parts = params.split(";", 4);

        String target = parts[0].trim();
        String soundString = parts[1].trim();
        float volume = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0f;
        float pitch = parts.length > 3 ? Float.parseFloat(parts[3]) : 1.0f;

        Sound sound = Sound.valueOf(soundString);

        if (target.equalsIgnoreCase("global")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), sound, volume, pitch);
            }
        } else if (target.equalsIgnoreCase("player")) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }

    }

    private void executeDelay(String params, List<String> actions, Player player, int nextIndex, Map<String, String> placeholders) {

        int delay = Integer.parseInt(params.trim());

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> executeActions(actions, player, nextIndex, placeholders), delay / 50L);

    }

    private String replacePlaceholders(Player player, String str, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            str = Colorize.color(str.replace(entry.getKey(), entry.getValue()));
        }

        str = PlaceholderUtils.replace(player, str);

        return str;
    }

}