package me.noahvdaa.uqueue.commands;

import me.noahvdaa.uqueue.UQueue;
import me.noahvdaa.uqueue.commands.uqueuecommand.HelpSubCommand;
import me.noahvdaa.uqueue.commands.uqueuecommand.PauseSubCommand;
import me.noahvdaa.uqueue.commands.uqueuecommand.ReloadSubCommand;
import me.noahvdaa.uqueue.commands.uqueuecommand.UnPauseSubCommand;
import me.noahvdaa.uqueue.util.ChatUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class UQueueCommand extends Command {

	private final UQueue plugin;

	public UQueueCommand(UQueue plugin) {
		super("uqueue", "", "uq");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		String subcommand = "";
		if (args.length != 0 && sender.hasPermission("uqueue.admin")) {
			subcommand = args[0].toLowerCase();
		}
		switch (subcommand) {
			case "help":
				HelpSubCommand.run(plugin, sender);
				break;
			case "unpause":
			case "enable":
				UnPauseSubCommand.run(plugin, sender, args);
				break;
			case "reload":
				ReloadSubCommand.run(plugin, sender);
				break;
			case "pause":
			case "disable":
				PauseSubCommand.run(plugin, sender, args);
				break;
			case "":
				sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&eThis server is running &buQueue v" + plugin.getDescription().getVersion() + " &eby &bNoahvdAa&e."));
				if (!sender.hasPermission("uqueue.admin")) {
					sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.uQueue.NoPermissions"));
				} else {
					sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&eType &b/uqueue help &efor a list of commands."));
				}
				break;
			default:
				sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&cUnknown subcommand. Type /uqueue help for help."));
				break;
		}
	}

}
