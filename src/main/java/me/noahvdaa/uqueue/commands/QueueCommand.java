package me.noahvdaa.uqueue.commands;

import me.noahvdaa.uqueue.UQueue;
import me.noahvdaa.uqueue.util.ChatUtil;
import me.noahvdaa.uqueue.util.PermissionUtil;
import me.noahvdaa.uqueue.util.QueueUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class QueueCommand extends Command {

	private final UQueue plugin;

	public QueueCommand(UQueue plugin) {
		super("queue");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&cThis command can only be used by players."));
			return;
		}
		ProxiedPlayer p = (ProxiedPlayer) sender;

		if (args.length != 1) {
			sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&cUsage: /queue <server>"));
			return;
		}

		String target = args[0].toLowerCase();

		if (!PermissionUtil.mayQueueForServer(plugin, p, target)) {
			sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.NotAllowedToQueue"));
			return;
		}

		ServerInfo server = ProxyServer.getInstance().getServerInfo(args[0]);

		if (server == null) {
			sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.ServerDoesntExist"));
			return;
		}

		if (server.getName().equals(p.getServer().getInfo().getName())) {
			sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.AlreadyHere"));
			return;
		}

		if (plugin.queuedFor.containsKey(p.getUniqueId())) {
			String queuedFor = plugin.queuedFor.get(p.getUniqueId());
			if (queuedFor.equals(server.getName())) {
				sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.AlreadyQueuedForSameServer"));
				return;
			}
			QueueUtil.removeFromQueue(plugin, p.getUniqueId());
			sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.LeftQueueFor", queuedFor));
		}

		QueueUtil.insertIntoQueue(plugin, server.getName(), p.getUniqueId(), PermissionUtil.getQueuePriority(p, server.getName()));
		sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.NowQueuedFor", server.getName()));
	}

}
