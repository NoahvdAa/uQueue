package me.noahvdaa.uqueue.commands.uqueuecommand;

import me.noahvdaa.uqueue.UQueue;
import me.noahvdaa.uqueue.util.ChatUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class UnPauseSubCommand {

	public static void run(UQueue plugin, CommandSender sender, String[] args) {
		if(args.length != 2){
			sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&cUsage: /uqueue unpause <server>"));
			return;
		}
		String server = args[1];
		ServerInfo info = ProxyServer.getInstance().getServerInfo(server);
		if (info == null) {
			sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&cThat server doesn't exist!"));
			return;
		}
		if (!plugin.disabledServers.contains(info.getName())) {
			sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&cThat server is already unpaused!"));
			return;
		}
		plugin.disabledServers.remove(info.getName());
		sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&aUnpaused queue for " + info.getName() + "."));
	}

}
