package me.noahvdaa.uqueue.commands;

import me.noahvdaa.uqueue.config.ConfigValidationHelper;
import me.noahvdaa.uqueue.UQueue;
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
				sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&eAvailable subcommands:"));
				sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&b/uqueue help &e- Show this list of commands."));
				sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&b/uqueue reload &e- Reload the config file."));
				break;
			case "reload":
				sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&6Reloading config file, please wait..."));
				plugin.getConfig().forceReload();
				// Verify config.
				ConfigValidationHelper.validateConfig(plugin.getConfig(), plugin.getLogger());
				sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&aReloaded config!"));
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
