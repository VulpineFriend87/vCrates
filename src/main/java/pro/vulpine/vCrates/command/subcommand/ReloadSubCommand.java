package pro.vulpine.vCrates.command.subcommand;

import pro.vulpine.vCrates.utils.Colorize;
import org.bukkit.command.CommandSender;
import pro.vulpine.vCrates.command.VCratesCommand;
import pro.vulpine.vCrates.instance.SubCommand;

import java.util.Collections;
import java.util.List;

public class ReloadSubCommand implements SubCommand {

    private final VCratesCommand command;

    public ReloadSubCommand(VCratesCommand command) {
        this.command = command;
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

        command.getPlugin().getMainConfiguration().reload();
        command.getPlugin().getMainConfiguration().save();

        command.getPlugin().getRaritiesConfiguration().reload();
        command.getPlugin().getRaritiesConfiguration().save();

        command.getPlugin().getCratesConfiguration().reload();
        command.getPlugin().getCratesConfiguration().save();

        command.getPlugin().getKeysConfiguration().reload();
        command.getPlugin().getKeysConfiguration().save();

        command.getPlugin().getResponsesConfiguration().reload();
        command.getPlugin().getResponsesConfiguration().save();

        sender.sendMessage(Colorize.color(
                "&7[&3v&bCrates&7] &aConfiguration files reloaded in &7(" + (System.currentTimeMillis() - configStartTime) + "ms)"
        ));

        sender.sendMessage(Colorize.color(
                "&7[&3v&bCrates&7] &7Reloading rarity manager..."
        ));

        long rarityManagerStartTime = System.currentTimeMillis();

        command.getPlugin().getRarityManager().reload();

        sender.sendMessage(Colorize.color(
                "&7[&3v&bCrates&7] &aRarity manager reloaded in &7(" + (System.currentTimeMillis() - rarityManagerStartTime) + "ms)"
        ));

        sender.sendMessage(Colorize.color(
                "&7[&3v&bCrates&7] &7Reloading crate manager..."
        ));

        long crateManagerStartTime = System.currentTimeMillis();

        command.getPlugin().getCrateManager().reload();

        sender.sendMessage(Colorize.color(
                "&7[&3v&bCrates&7] &aCrate manager reloaded in &7(" + (System.currentTimeMillis() - crateManagerStartTime) + "ms)"
        ));

        sender.sendMessage(Colorize.color(
                "&7[&3v&bCrates&7] &7Reloading key manager..."
        ));

        long keyManagerStartTime = System.currentTimeMillis();

        command.getPlugin().getKeyManager().reload();

        sender.sendMessage(Colorize.color(
                "&7[&3v&bCrates&7] &aKey manager reloaded in &7(" + (System.currentTimeMillis() - keyManagerStartTime) + "ms)"
        ));

        sender.sendMessage(Colorize.color(
                "&7[&3v&bCrates&7] &aPlugin reload completed! &7(" + (System.currentTimeMillis() - startTime) + "ms)"
        ));

    }

    @Override
    public List<String> executeTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
