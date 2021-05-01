package me.noahvdaa.uqueue.commands.uqueuecommand;

import me.noahvdaa.uqueue.UQueue;
import me.noahvdaa.uqueue.util.ChatUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.UUID;

public class ListSubCommand {

	public static void run(UQueue plugin, CommandSender sender, String[] args) {
		if (args.length != 2) {
			sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&cUsage: /uqueue list <server>"));
			return;
		}
		String server = args[1];
		ServerInfo info = ProxyServer.getInstance().getServerInfo(server);
		if (info == null) {
			sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&cThat server doesn't exist!"));
			return;
		}

		BaseComponent message = ChatUtil.colorizeIntoPrefixedComponent(plugin, "&ePeople queued for &b" + info.getName() + "&e (" + (plugin.queues.containsKey(info.getName()) ? plugin.queues.get(info.getName()).size() : 0) + "): &f");

		if (plugin.queues.containsKey(info.getName())) {
			int i = 0;
			for (UUID p : plugin.queues.get(info.getName())) {
				if (i != 0) message.addExtra(", ");
				BaseComponent component = ChatUtil.colorizeIntoComponent(plugin.getProxy().getPlayer(p).getName());
				component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Priority: " + plugin.queuePriority.get(p))));
				message.addExtra(component);
				i++;
			}
		}

		sender.sendMessage(message);
	}
}
