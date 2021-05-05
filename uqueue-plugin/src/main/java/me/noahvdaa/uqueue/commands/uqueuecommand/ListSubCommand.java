package me.noahvdaa.uqueue.commands.uqueuecommand;

import me.noahvdaa.uqueue.UQueue;
import me.noahvdaa.uqueue.api.util.QueueableServer;
import me.noahvdaa.uqueue.util.ChatUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;
import java.util.UUID;

public class ListSubCommand {

	public static void run(UQueue plugin, CommandSender sender, String[] args) {
		if (args.length != 2) {
			sender.sendMessage(ChatUtil.colorizeAsPrefixedComponent(plugin, "&cUsage: /uqueue list <server>"));
			return;
		}
		String server = args[1].toLowerCase();
		ServerInfo info = ProxyServer.getInstance().getServerInfo(server);
		if (info == null) {
			sender.sendMessage(ChatUtil.colorizeAsPrefixedComponent(plugin, "&cThat server doesn't exist!"));
			return;
		}

		QueueableServer queueableServer = plugin.getServer(info);

		List<UUID> queued = queueableServer.getQueuedPlayers();

		BaseComponent message = ChatUtil.colorizeAsPrefixedComponent(plugin, "&ePeople queued for &b" + info.getName() + "&e (" + (queued.size() + "): &f"));

		int i = 0;
		for (UUID p : queued) {
			if (i != 0) message.addExtra(", ");
			BaseComponent component = ChatUtil.colorizeAsComponent(plugin.getProxy().getPlayer(p).getName());
			component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Priority: " + queueableServer.getCachedPriority(p))));
			message.addExtra(component);
			i++;
		}

		sender.sendMessage(message);
	}
}
