package me.noahvdaa.uqueue.listener;

import me.noahvdaa.uqueue.UQueue;
import me.noahvdaa.uqueue.api.util.QueueablePlayer;
import me.noahvdaa.uqueue.api.util.QueueableServer;
import me.noahvdaa.uqueue.commands.QueueCommand;
import me.noahvdaa.uqueue.util.ChatUtil;
import me.noahvdaa.uqueue.util.PerServerConfigUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PlayerListener implements Listener {

	private final UQueue plugin;
	private final QueueCommand queueCommand;

	public PlayerListener(UQueue plugin) {
		this.plugin = plugin;
		this.queueCommand = new QueueCommand(plugin);
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		QueueablePlayer queueablePlayer = plugin.getPlayer(e.getPlayer());
		if (queueablePlayer.isQueued()) queueablePlayer.getQueuedServer().removeFromQueue(queueablePlayer);
		plugin.removePlayer(e.getPlayer());
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent e) {
		ProxiedPlayer p = e.getPlayer();
		QueueablePlayer queueablePlayer = plugin.getPlayer(e.getPlayer());

		String vHost = p.getPendingConnection().getVirtualHost().getHostString().toLowerCase().replace('.', '_');
		String forcedHost = plugin.getConfig().getString("ForcedHosts.OTHER");

		if (plugin.getConfig().contains("ForcedHosts." + vHost)) {
			forcedHost = plugin.getConfig().getString("ForcedHosts." + vHost);
		}

		if (forcedHost.equals("")) return;

		ServerInfo server = ProxyServer.getInstance().getServerInfo(forcedHost);
		if (server == null) return;

		QueueableServer queueableServer = plugin.getServer(server);

		if (!queueableServer.mayQueue(queueablePlayer)) return;

		if (p.hasPermission("uqueue.bypass." + server.getName())) {
			p.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Notifications.SendingYou", queueableServer.getDisplayName()));
			p.connect(server);
			return;
		}

		queueableServer.addToQueue(queueablePlayer);
		queueablePlayer.setConnectionAttempts(0);
		p.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.NowQueuedFor", queueableServer.getDisplayName()));
	}

	@EventHandler
	public void onServerConnected(ServerConnectedEvent e) {
		QueueablePlayer queueablePlayer = plugin.getPlayer(e.getPlayer());
		if (!queueablePlayer.isQueued() || !queueablePlayer.getQueuedServer().getName().equals(e.getServer().getInfo().getName()))
			return;
		queueablePlayer.getQueuedServer().removeFromQueue(queueablePlayer);
	}

	@EventHandler
	public void onChat(ChatEvent e) {
		if (!plugin.getConfig().getBoolean("Chat.HijackServerCommand")) return;
		if (!e.getMessage().toLowerCase().startsWith("/server")) return;
		e.setMessage(e.getMessage().replaceAll("^/server", "/queue"));
	}

	@EventHandler
	public void onPluginMessage(PluginMessageEvent e) {
		if (!e.getTag().equals("uqueue:queueplayer")) return;
		String player;
		String server;
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
			player = in.readUTF();
			server = in.readUTF();
		} catch (IOException exception) {
			// Swallow.
			return;
		}
		String[] args = new String[1];
		args[0] = server;

		ProxiedPlayer pPlayer = plugin.getProxy().getPlayer(player);

		queueCommand.execute(pPlayer, args);
	}

	@EventHandler
	public void onServerConnect(ServerConnectEvent e) {
		ServerInfo server = e.getTarget();
		String detectServerSend = PerServerConfigUtil.getString(plugin, server.getName(), "DetectServerSend");
		if (detectServerSend.equalsIgnoreCase("false")) return;

		ProxiedPlayer player = e.getPlayer();
		QueueablePlayer queueablePlayer = plugin.getPlayer(player);
		QueueableServer queueableServer = plugin.getServer(server);

		if (!queueableServer.mayQueue(queueablePlayer) || player.hasPermission("uqueue.bypass." + server.getName()))
			return;

		if (detectServerSend.equalsIgnoreCase("fullonly") && queueableServer.getAvailableSlots() > 0 && queueableServer.getQueueLength() == 0)
			return;

		// Are they already uQueued for this server?
		if (queueablePlayer.isQueued() && queueablePlayer.getQueuedServer().getName().equals(queueableServer.getName()))
			return;

		// Yes, we need to queue.
		e.setCancelled(true);

		if (queueablePlayer.isQueued()) {
			queueablePlayer.getQueuedServer().removeFromQueue(queueablePlayer);
			player.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.LeftQueueFor", queueableServer.getDisplayName()));
		}

		queueableServer.addToQueue(queueablePlayer);
		queueablePlayer.setConnectionAttempts(0);
		player.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.NowQueuedFor", queueableServer.getDisplayName()));
	}

}
