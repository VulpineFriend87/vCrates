package pro.vulpine.vCrates.command;

import it.vulpinefriend87.easyutils.Colorize;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.command.subcommand.BalanceSubCommand;
import pro.vulpine.vCrates.command.subcommand.HelpSubCommand;
import pro.vulpine.vCrates.command.subcommand.KeySubCommand;
import pro.vulpine.vCrates.command.subcommand.ReloadSubCommand;
import pro.vulpine.vCrates.instance.SubCommand;
import pro.vulpine.vCrates.utils.PermissionChecker;

import java.util.*;
import java.util.stream.Collectors;

public class VCratesCommand implements CommandExecutor, TabCompleter {

    private final VCrates plugin;

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public VCratesCommand(VCrates plugin) {

        this.plugin = plugin;

        subCommands.put("help", new HelpSubCommand(this));
        subCommands.put("reload", new ReloadSubCommand(this));
        subCommands.put("key", new KeySubCommand(this));
        subCommands.put("balance", new BalanceSubCommand(this));

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        executeSubCommand(sender, args);

        return false;
    }

    public void executeSubCommand(CommandSender sender, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(Colorize.color(
                    "&r\n&7 This server is running\n&r\n&3 v&bCrates &7[v" + plugin.getDescription().getVersion() + "] " +
                    "\n&7 By " + String.join(", ", plugin.getDescription().getAuthors()) +
                    "\n&r\n&7 Use &b/vcrates help &7for a command list.\n&r"
            ));
            return;
        }

        if (!PermissionChecker.hasPermission(sender, args[0].toLowerCase())) {

            sender.sendMessage(Colorize.color(
                    plugin.getResponsesConfiguration().getString("unknown_command")
            ));

            return;

        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());

        if (subCommand != null) {
            subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));

        } else {
            sender.sendMessage(plugin.getResponsesConfiguration().getString("unknown_command"));
        }

    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return subCommands.keySet().stream()
                    .filter(cmd -> PermissionChecker.hasPermission(sender, cmd.toLowerCase()))
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand != null) {

            return subCommand.executeTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
        }

        return Collections.emptyList();
    }

    public VCrates getPlugin() {
        return plugin;
    }

    public Map<String, SubCommand> getSubCommands() {
        return subCommands;
    }
}