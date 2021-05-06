package me.noahvdaa.uqueue.commands;

import me.noahvdaa.uqueue.UQueue;
import me.noahvdaa.uqueue.api.util.QueueablePlayer;
import me.noahvdaa.uqueue.api.util.QueueableServer;
import me.noahvdaa.uqueue.util.ChatUtil;
import me.noahvdaa.uqueue.util.PerServerConfigUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class QueueCommand extends Command implements TabExecutor {

	private final UQueue plugin;

	public QueueCommand(UQueue plugin) {
		super("queue");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(ChatUtil.colorizeAsPrefixedComponent(plugin, "&cThis command can only be used by players."));
			return;
		}
		ProxiedPlayer p = (ProxiedPlayer) sender;
		QueueablePlayer queueablePlayer = plugin.getPlayer(p);

		if (args.length != 1) {
			sender.sendMessage(ChatUtil.colorizeAsPrefixedComponent(plugin, "&cUsage: /queue <server>"));
			return;
		}

		ServerInfo server = ProxyServer.getInstance().getServerInfo(args[0].toLowerCase());

		if (server == null) {
			sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.ServerDoesntExist"));
			return;
		}

		QueueableServer queueableServer = plugin.getServer(server);

		if (!queueableServer.mayQueue(queueablePlayer)) {
			sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.NotAllowedToQueue"));
			return;
		}

		if (p.hasPermission("uqueue.bypass." + server.getName())) {
			sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Notifications.SendingYou", queueableServer.getDisplayName()));
			p.connect(server);
			return;
		}

		if (server.getName().equals(p.getServer().getInfo().getName())) {
			sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.AlreadyHere"));
			return;
		}

		if (queueablePlayer.isQueued()) {
			String queuedFor = queueablePlayer.getQueuedServer().getName();
			if (queuedFor.equals(server.getName())) {
				sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.AlreadyQueuedForSameServer"));
				return;
			}
			queueablePlayer.getQueuedServer().removeFromQueue(queueablePlayer);
			sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.LeftQueueFor", queueableServer.getDisplayName()));
		}

		queueableServer.addToQueue(queueablePlayer);
		queueablePlayer.setConnectionAttempts(0);
		sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.NowQueuedFor", queueableServer.getDisplayName()));
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		List<String> suggestions = new ArrayList<>();

		if (!(sender instanceof ProxiedPlayer)) return suggestions;

		ProxiedPlayer p = (ProxiedPlayer) sender;
		QueueablePlayer queueablePlayer = plugin.getPlayer(p);

		for (ServerInfo server : plugin.getProxy().getServers().values()) {
			QueueableServer queueableServer = plugin.getServer(server);
			if (queueableServer.mayQueue(queueablePlayer))
				suggestions.add(server.getName());
		}

		return suggestions;
	}
}
