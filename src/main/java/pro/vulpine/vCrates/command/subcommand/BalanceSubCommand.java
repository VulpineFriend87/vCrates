package pro.vulpine.vCrates.command.subcommand;

import pro.vulpine.vCrates.utils.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pro.vulpine.vCrates.command.VCratesCommand;
import pro.vulpine.vCrates.instance.Key;
import pro.vulpine.vCrates.instance.Profile;
import pro.vulpine.vCrates.instance.SubCommand;
import pro.vulpine.vCrates.utils.PermissionChecker;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BalanceSubCommand implements SubCommand {

    private final VCratesCommand command;

    public BalanceSubCommand(VCratesCommand command) {
        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 0) {

            if (!PermissionChecker.hasPermission(sender, "balance", "self")) {

                sender.sendMessage(Colorize.color(
                        command.getPlugin().getResponsesConfiguration().getString("no_permission")
                ));

                return;

            }

            if (!(sender instanceof Player)) {

                sender.sendMessage(Colorize.color(
                        command.getPlugin().getResponsesConfiguration().getString("only_players")
                ));

                return;

            }

            Player player = (Player) sender;

            Profile profile = command.getPlugin().getProfileManager().getProfile(player.getUniqueId());

            if (profile == null) {

                player.sendMessage(Colorize.color(
                        command.getPlugin().getResponsesConfiguration().getString("no_profile")
                ));

                return;

            }

            player.sendMessage(Colorize.color(
                    command.getPlugin().getResponsesConfiguration().getString("keys.balance.header")
            ));

            Map<String, Integer> keys = profile.getKeys();

            int total = 0;
            for (String identifier : keys.keySet()) {

                Key key = command.getPlugin().getKeyManager().getKey(identifier);

                if (key == null) {
                    continue;
                }

                int amount = keys.get(identifier);

                if (amount == 0) {
                    continue;
                }

                player.sendMessage(Colorize.color(
                        command.getPlugin().getResponsesConfiguration().getString("keys.balance.entry")
                                .replace("%identifier%", identifier)
                                .replace("%key%", key.getName())
                                .replace("%amount%", String.valueOf(keys.get(identifier)))
                ));

                total ++;

            }

            if (total == 0) {

                player.sendMessage(Colorize.color(
                        command.getPlugin().getResponsesConfiguration().getString("keys.balance.empty")
                ));

            }

        } else if (args.length == 1) {

            if (!PermissionChecker.hasPermission(sender, "balance", "other")) {

                sender.sendMessage(Colorize.color(
                        command.getPlugin().getResponsesConfiguration().getString("wrong_arguments")
                ));

                return;

            }

            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[0]);

            Profile targetProfile = command.getPlugin().getProfileManager().getProfile(targetPlayer.getUniqueId());

            if (!targetPlayer.hasPlayedBefore()) {

                sender.sendMessage(Colorize.color(
                        command.getPlugin().getResponsesConfiguration().getString("player_not_found")
                ));

                return;

            }

            boolean wasLoaded = targetProfile != null;

            command.getPlugin().getProfileManager().loadProfile(targetPlayer.getUniqueId(), false).thenCompose(profile -> {

                if (profile == null) {

                    sender.sendMessage(Colorize.color(
                            command.getPlugin().getResponsesConfiguration().getString("player_not_found")
                    ));

                    return CompletableFuture.completedFuture(null);

                }

                sender.sendMessage(Colorize.color(
                        command.getPlugin().getResponsesConfiguration().getString("keys.balance.header")
                ));

                Map<String, Integer> keys = profile.getKeys();

                int total = 0;
                for (String identifier : keys.keySet()) {

                    Key key = command.getPlugin().getKeyManager().getKey(identifier);

                    if (key == null) {
                        continue;
                    }

                    int amount = keys.get(identifier);

                    if (amount == 0) {
                        continue;
                    }

                    sender.sendMessage(Colorize.color(
                            command.getPlugin().getResponsesConfiguration().getString("keys.balance.entry")
                                    .replace("%identifier%", identifier)
                                    .replace("%key%", key.getName())
                                    .replace("%amount%", String.valueOf(keys.get(identifier)))
                    ));

                    total ++;

                }

                if (total == 0) {

                    sender.sendMessage(Colorize.color(
                            command.getPlugin().getResponsesConfiguration().getString("keys.balance.empty")
                    ));

                }

                return CompletableFuture.completedFuture(profile);

            }).thenAccept(profile -> {

                if (profile == null) return;

                if (!wasLoaded) {

                    command.getPlugin().getProfileManager().unloadProfile(targetPlayer.getUniqueId());

                }

            }).exceptionally(exception -> {

                sender.sendMessage(Colorize.color(
                        command.getPlugin().getResponsesConfiguration().getString("player_not_found")
                ));

                return null;

            });

        } else {

            sender.sendMessage(Colorize.color(
                    command.getPlugin().getResponsesConfiguration().getString("wrong_arguments")
            ));

        }

    }

    @Override
    public List<String> executeTabComplete(CommandSender sender, String[] args) {

        if (args.length == 1 && PermissionChecker.hasPermission(sender, "balance", "other")) {

            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());

        }

        return Collections.emptyList();

    }
}
