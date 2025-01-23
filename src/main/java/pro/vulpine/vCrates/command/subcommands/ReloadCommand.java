package pro.vulpine.vCrates.command.subcommands;

import it.vulpinefriend87.easyutils.Colorize;
import org.bukkit.command.CommandSender;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.instance.SubCommand;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand implements SubCommand {

    private final VCrates plugin;

    public ReloadCommand(VCrates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        sender.sendMessage(Colorize.color(
                "&7[&3v&bCrates&7] &fPlugin reload process initiated."
        ));

        long startTime = System.currentTimeMillis();

        sender.sendMessage(Colorize.color(
                "&7[&3v&bCrates&7] &7Reloading configuration files..."
        ));

        long configStartTime = System.currentTimeMillis();

        plugin.getMainConfiguration().reload();
        plugin.getMainConfiguration().save();

        plugin.getCratesConfiguration().reload();
        plugin.getCratesConfiguration().save();

        plugin.getKeysConfiguration().reload();
        plugin.getKeysConfiguration().save();

        plugin.getResponsesConfiguration().reload();
        plugin.getResponsesConfiguration().save();

        sender.sendMessage(Colorize.color(
                "&7[&3v&bCrates&7] &aConfiguration files reloaded in &7(" + (System.currentTimeMillis() - configStartTime) + "ms)"
        ));

        sender.sendMessage(Colorize.color(
                "&7[&3v&bCrates&7] &7Reloading crate manager..."
        ));

        long crateManagerStartTime = System.currentTimeMillis();

        plugin.getCrateManager().reload();

        sender.sendMessage(Colorize.color(
                "&7[&3v&bCrates&7] &aCrate manager reloaded in &7(" + (System.currentTimeMillis() - crateManagerStartTime) + "ms)"
        ));

        sender.sendMessage(Colorize.color(
                "&7[&3v&bCrates&7] &7Reloading key manager..."
        ));

        long keyManagerStartTime = System.currentTimeMillis();

        plugin.getKeyManager().reload();

        sender.sendMessage(Colorize.color(
                "&7[&3v&bCrates&7] &aKey manager reloaded in &7(" + (System.currentTimeMillis() - keyManagerStartTime) + "ms)"
        ));

        sender.sendMessage(Colorize.color(
                "&7[&3v&bCrates&7] &aPlugin reload completed! &7(" + (System.currentTimeMillis() - startTime) + "ms)"
        ));

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
