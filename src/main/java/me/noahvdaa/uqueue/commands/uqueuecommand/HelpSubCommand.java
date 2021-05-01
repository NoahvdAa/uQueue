package me.noahvdaa.uqueue.commands.uqueuecommand;

import me.noahvdaa.uqueue.UQueue;
import me.noahvdaa.uqueue.util.ChatUtil;
import net.md_5.bungee.api.CommandSender;

public class HelpSubCommand {

	public static void run(UQueue plugin, CommandSender sender) {
		sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&eAvailable subcommands:"));
		sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&b/uqueue help &e- Show this list of commands."));
		sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&b/uqueue list <server> &e- Shows all people in queue for a certain server."));
		sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&b/uqueue pause <server> &e- Pause the queue for a certain server."));
		sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&b/uqueue reload &e- Reload the config file."));
		sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&b/uqueue unpause <server> &e- Unpause the queue for a certain server."));
	}

}
