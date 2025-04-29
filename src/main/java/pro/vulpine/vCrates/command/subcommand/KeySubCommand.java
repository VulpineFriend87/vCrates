package pro.vulpine.vCrates.command.subcommand;

import pro.vulpine.vCrates.instance.Profile;
import pro.vulpine.vCrates.utils.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pro.vulpine.vCrates.command.VCratesCommand;
import pro.vulpine.vCrates.instance.Key;
import pro.vulpine.vCrates.instance.SubCommand;
import pro.vulpine.vCrates.utils.KeyUtils;
import pro.vulpine.vCrates.utils.PermissionChecker;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeySubCommand implements SubCommand {

    public VCratesCommand command;

    public KeySubCommand(VCratesCommand command) {
        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {

            if (!PermissionChecker.hasPermission(sender, "key", "help")) {

                sender.sendMessage(Colorize.color(
                        command.getPlugin().getResponsesConfiguration().getString("wrong_arguments")
                ));

                return;

            }

            List<String> help = command.getPlugin().getResponsesConfiguration().getStringList("keys.help");
            for (String line : help) {
                sender.sendMessage(Colorize.color(line));
            }

            return;

        }

        if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("take")) {

            String action = args[0];

            if (!PermissionChecker.hasPermission(sender, "key", action)) {

                sender.sendMessage(Colorize.color(
                        command.getPlugin().getResponsesConfiguration().getString("wrong_arguments")
                ));

                return;

            }

            if (args.length < 4) {

                sender.sendMessage(Colorize.color(
                        command.getPlugin().getResponsesConfiguration().getString("wrong_arguments")
                ));

                return;
            }

            boolean isTargetAll = args[1].equalsIgnoreCase("all");
            List<OfflinePlayer> players = isTargetAll ? new ArrayList<>(Bukkit.getOnlinePlayers()) : List.of(Bukkit.getOfflinePlayer(args[1]));

            if (!isTargetAll && !players.getFirst().hasPlayedBefore()) {

                sender.sendMessage(Colorize.color(
                    command.getPlugin().getResponsesConfiguration().getString("player_not_found")
                ));

                return;

            }

            String identifier = args[2];

            String type = args[3];
            if (!type.equalsIgnoreCase("physical") && !type.equalsIgnoreCase("virtual")) {

                sender.sendMessage(Colorize.color(
                        command.getPlugin().getResponsesConfiguration().getString("keys.invalid_type")
                ));

                return;

            }

            Key key = command.getPlugin().getKeyManager().getKey(identifier);
            if (key == null) {

                sender.sendMessage(Colorize.color(
                        command.getPlugin().getResponsesConfiguration().getString("keys.invalid_identifier")
                ));

                return;
            }

            if (!isTargetAll && type.equalsIgnoreCase("physical") && !players.getFirst().isOnline()) {

                sender.sendMessage(Colorize.color(
                        command.getPlugin().getResponsesConfiguration().getString("keys.offline")
                ));

                return;

            }

            if (!key.isVirtualAllowed() && type.equalsIgnoreCase("virtual")) {

                sender.sendMessage(Colorize.color(
                        command.getPlugin().getResponsesConfiguration().getString("keys.invalid_type")
                ));

                return;

            }

            int amount = 1;

            if (args.length >= 5) {

                try {

                    amount = Integer.parseInt(args[4]);

                } catch (NumberFormatException e) {

                    sender.sendMessage(Colorize.color(
                            command.getPlugin().getResponsesConfiguration().getString("invalid_amount")
                    ));

                    return;
                }

            }

            String successResponseKey = action.equals("give") ? "keys.received." + type : "keys.taken." + type;

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%amount%", String.valueOf(amount));
            placeholders.put("%key%", key.getName());
            placeholders.put("%identifier%", identifier);
            placeholders.put("%type%", type);

            if (type.equalsIgnoreCase("physical")) {

                List<OfflinePlayer> successfulPlayers = new ArrayList<>();

                for (OfflinePlayer player : players) {

                    if (action.equalsIgnoreCase("give")) {

                        ItemStack keyItem = KeyUtils.generateKey(identifier, key.getName(), key.getItem());

                        for (int i = 0; i < amount; i++) {

                            player.getPlayer().getInventory().addItem(keyItem);

                        }

                        successfulPlayers.add(player);

                    } else if (action.equalsIgnoreCase("take")) {

                        Player onlinePlayer = player.getPlayer();

                        int totalAvailable = 0;
                        for (ItemStack item : onlinePlayer.getInventory().getContents()) {

                            if (item != null && item.getType() != Material.AIR) {

                                String keyIdentifier = KeyUtils.getKeyIdentifier(item);
                                if (keyIdentifier != null && keyIdentifier.equals(identifier)) {

                                    totalAvailable += item.getAmount();

                                }

                            }

                        }

                        if (totalAvailable < amount) {

                            if (!isTargetAll) {

                                sender.sendMessage(Colorize.color(
                                        command.getPlugin().getResponsesConfiguration().getString("keys.not_enough")
                                ));

                                return;

                            }

                            continue;

                        }

                        int itemsRemoved = 0;
                        for (ItemStack item : onlinePlayer.getInventory().getContents()) {

                            if (item != null && item.getType() != Material.AIR && itemsRemoved < amount) {

                                String keyIdentifier = KeyUtils.getKeyIdentifier(item);
                                if (keyIdentifier != null && keyIdentifier.equals(identifier)) {

                                    int available = item.getAmount();
                                    int needed = amount - itemsRemoved;
                                    int removeCount = Math.min(available, needed);

                                    item.setAmount(available - removeCount);

                                    itemsRemoved += removeCount;

                                }

                            }

                        }

                        if (itemsRemoved > 0) {

                            successfulPlayers.add(player);

                        }

                    }

                }

                if (!isTargetAll) {

                    placeholders.put("%player%", players.getFirst().getName());

                }

                placeholders.put("%player_count%", String.valueOf(successfulPlayers.size()));


                String messageKey = isTargetAll ? "keys." + action + "_all" : "keys." + action;
                String message = command.getPlugin().getResponsesConfiguration().getString(messageKey);

                for (Map.Entry<String, String> entry : placeholders.entrySet()) {

                    message = message.replace(entry.getKey(), entry.getValue());

                }

                sender.sendMessage(Colorize.color(message));

                for (OfflinePlayer player : successfulPlayers) {

                    if (player.isOnline()) {

                        command.getPlugin().getActionParser().executeActions(
                                command.getPlugin().getResponsesConfiguration().getStringList(successResponseKey),
                                player.getPlayer(),
                                0,
                                placeholders
                        );

                    }

                }

            } else if (type.equalsIgnoreCase("virtual")) {

                List<OfflinePlayer> successfulPlayers = Collections.synchronizedList(new ArrayList<>());
                List<CompletableFuture<Profile>> futures = new ArrayList<>();

                int finalAmount = amount;

                for (OfflinePlayer player : players) {

                    CompletableFuture<Profile> future = command.getPlugin().getProfileManager()
                            .loadProfile(player.getUniqueId(), false)
                            .thenCompose(profile -> {

                                if (profile == null) {

                                    sender.sendMessage(Colorize.color(
                                            command.getPlugin().getResponsesConfiguration().getString("player_not_found")
                                    ));

                                    return CompletableFuture.completedFuture(null);

                                }

                                if (action.equalsIgnoreCase("give")) {

                                    return profile.giveKey(identifier, finalAmount, true).thenApply(v -> profile);

                                } else {

                                    return profile.takeKey(identifier, finalAmount, true).thenApply(v -> profile);

                                }

                            }).thenApply(profile -> {

                                if (profile != null) {

                                    successfulPlayers.add(player);

                                }

                                return profile;

                            }).exceptionally(exception -> {

                                sender.sendMessage(Colorize.color(
                                        command.getPlugin().getResponsesConfiguration().getString("player_not_found")
                                ));

                                return null;

                            });

                    futures.add(future);

                }

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {

                    if (!isTargetAll) {

                        placeholders.put("%player%", players.getFirst().getName());

                    }

                    placeholders.put("%player_count%", String.valueOf(successfulPlayers.size()));

                    String messageKey = isTargetAll ? "keys." + action + "_all" : "keys." + action;
                    String message = command.getPlugin().getResponsesConfiguration().getString(messageKey);

                    for (Map.Entry<String, String> entry : placeholders.entrySet()) {

                        message = message.replace(entry.getKey(), entry.getValue());

                    }

                    String finalMessage = message;

                    Bukkit.getScheduler().runTask(command.getPlugin(), () -> sender.sendMessage(Colorize.color(finalMessage)));

                    for (OfflinePlayer player : successfulPlayers) {

                        if (player.isOnline()) {

                            command.getPlugin().getActionParser().executeActions(
                                    command.getPlugin().getResponsesConfiguration().getStringList(successResponseKey),
                                    player.getPlayer(),
                                    0,
                                    placeholders
                            );

                        }

                    }

                });

            }

        } else {

            sender.sendMessage(Colorize.color(
                    command.getPlugin().getResponsesConfiguration().getString("wrong_arguments")
            ));

        }

    }

    @Override
    public List<String> executeTabComplete(CommandSender sender, String[] args) {

        if (args.length == 1) {

            List<String> options = new ArrayList<>();

            if (PermissionChecker.hasPermission(sender, "key", "help")) {
                options.add("help");
            }

            if (PermissionChecker.hasPermission(sender, "key", "give")) {
                options.add("give");
            }

            if (PermissionChecker.hasPermission(sender, "key", "take")) {
                options.add("take");
            }

            return options.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());

        }

        if (args[0].equalsIgnoreCase("give") && PermissionChecker.hasPermission(sender, "key", "give") ||
            args[0].equalsIgnoreCase("take") && PermissionChecker.hasPermission(sender, "key", "take")) {

            if (args.length == 2) {

                String input = args[1].toLowerCase();

                return Stream.concat(
                        Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .filter(name -> name.toLowerCase().startsWith(input)),
                        Stream.of("all")
                ).collect(Collectors.toList());

            } else if (args.length == 3) {

                String input = args[2].toLowerCase();

                return command.getPlugin().getKeyManager().getKeys().stream()
                        .map(Key::getIdentifier)
                        .filter(key -> key.toLowerCase().startsWith(input))
                        .collect(Collectors.toList());

            } else if (args.length == 4) {

                List<String> options = new ArrayList<>(List.of("physical"));

                if (command.getPlugin().getKeyManager().getKeys().stream().anyMatch(Key::isVirtualAllowed)) {

                    options.add("virtual");

                }

                return options.stream()
                        .filter(option -> option.toLowerCase().startsWith(args[3].toLowerCase()))
                        .collect(Collectors.toList());

            }

        }

        return Collections.emptyList();

    }

}