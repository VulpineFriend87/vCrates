package pro.vulpine.vCrates.instance;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {

    void execute(CommandSender sender, String[] args);

    List<String> executeTabComplete(CommandSender sender, String[] args);

}
