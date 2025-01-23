package pro.vulpine.vCrates.command;

import it.vulpinefriend87.easyutils.Colorize;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.command.subcommands.ReloadCommand;
import pro.vulpine.vCrates.instance.SubCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VCratesCommand implements CommandExecutor, TabCompleter {

    private final VCrates plugin;

    private final Map<String, SubCommand> commands = new HashMap<>();

    public VCratesCommand(VCrates plugin) {

        this.plugin = plugin;

        commands.put("reload", new ReloadCommand(plugin));
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

        SubCommand subCommand = commands.get(args[0].toLowerCase());

        if (subCommand != null) {
            subCommand.execute(sender, args);

        } else {
            sender.sendMessage(plugin.getResponsesConfiguration().getString("unknown_command"));
        }

    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return executeTabComplete(commandSender, strings);
    }

    public List<String> executeTabComplete(CommandSender sender, String[] args) {

        if (args.length == 1) {
            return commands.keySet().stream().filter(s -> s.startsWith(args[0])).toList();
        }

        return new ArrayList<>();

    }

    public VCrates getPlugin() {
        return plugin;
    }

    public Map<String, SubCommand> getCommands() {
        return commands;
    }
}