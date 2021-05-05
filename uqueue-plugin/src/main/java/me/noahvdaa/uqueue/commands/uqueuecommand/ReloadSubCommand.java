package me.noahvdaa.uqueue.commands.uqueuecommand;

import me.noahvdaa.uqueue.UQueue;
import me.noahvdaa.uqueue.config.ConfigValidationHelper;
import me.noahvdaa.uqueue.util.ChatUtil;
import net.md_5.bungee.api.CommandSender;

public class ReloadSubCommand {

	public static void run(UQueue plugin, CommandSender sender) {
		sender.sendMessage(ChatUtil.colorizeAsPrefixedComponent(plugin, "&6Reloading config file, please wait..."));
		plugin.getConfig().forceReload();
		// Verify config.
		ConfigValidationHelper.validateConfig(plugin.getConfig(), plugin.getLogger());
		plugin.getMessages().forceReload();
		plugin.getPerServerConfig().forceReload();
		sender.sendMessage(ChatUtil.colorizeAsPrefixedComponent(plugin, "&aReloaded config!"));
	}

}
