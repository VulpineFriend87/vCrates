package pro.vulpine.vCrates.command.subcommand;

import it.vulpinefriend87.easyutils.Colorize;
import org.bukkit.command.CommandSender;
import pro.vulpine.vCrates.command.VCratesCommand;
import pro.vulpine.vCrates.instance.SubCommand;

import java.util.Collections;
import java.util.List;

public class HelpSubCommand implements SubCommand {

    private final VCratesCommand command;

    public HelpSubCommand(VCratesCommand command) {
        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        List<String> help = command.getPlugin().getResponsesConfiguration().getStringList("help");
        for (String line : help) {
            sender.sendMessage(Colorize.color(line));
        }

    }

    @Override
    public List<String> executeTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    public VCratesCommand getCommand() {
        return command;
    }
}
