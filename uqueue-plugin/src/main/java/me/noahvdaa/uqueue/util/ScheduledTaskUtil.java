package me.noahvdaa.uqueue.util;

import me.noahvdaa.uqueue.UQueue;
import me.noahvdaa.uqueue.api.util.QueueablePlayer;
import me.noahvdaa.uqueue.api.util.QueueableServer;
import me.noahvdaa.uqueue.api.util.ServerStatus;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ScheduledTaskUtil {

	public static void processQueue(UQueue plugin) {
		for (QueueableServer server : plugin.queueableServers.values()) {
			processQueueNotifications(server, plugin);
			processQueueSending(server, plugin);
		}
	}

	private static void processQueueNotifications(QueueableServer server, UQueue plugin) {
		List<UUID> queue = server.getQueuedPlayers();

		String queueSize = Integer.toString(queue.size());
		String serverStatus;
		boolean ignoreFull = PerServerConfigUtil.getBoolean(plugin, server.getName(), "InfiniteSlots");

		if (server.getStatus() == null) {
			// Not pinged yet.
			serverStatus = "online";
		} else if (server.getStatus() != ServerStatus.OFFLINE) {
			// Is it full?
			if (server.getStatus() == ServerStatus.FULL && !ignoreFull) {
				serverStatus = "full";
			} else {
				serverStatus = "online";
			}
		} else {
			long offlineFor = System.currentTimeMillis() - server.getStatusLastUpdated();

			if (offlineFor > PerServerConfigUtil.getInt(plugin, server.getName(), "RestartLength") * 1000L) {
				serverStatus = "offline";
			} else {
				serverStatus = "restarting";
			}
		}

		for (UUID player : queue) {
			String position = Integer.toString(queue.indexOf(player) + 1);
			switch (serverStatus) {
				default:
					ProxyServer.getInstance().getPlayer(player).sendMessage(ChatMessageType.ACTION_BAR, ChatUtil.getConfigMessageAsComponent(plugin, "Notifications.QueuePosition", position, queueSize, PerServerConfigUtil.getServerDisplayName(plugin, server.getName())));
					break;
				case "full":
					ProxyServer.getInstance().getPlayer(player).sendMessage(ChatMessageType.ACTION_BAR, ChatUtil.getConfigMessageAsComponent(plugin, "Notifications.ServerIsFull", PerServerConfigUtil.getServerDisplayName(plugin, server.getName()), position, queueSize));
					break;
				case "offline":
					ProxyServer.getInstance().getPlayer(player).sendMessage(ChatMessageType.ACTION_BAR, ChatUtil.getConfigMessageAsComponent(plugin, "Notifications.ServerIsOffline", PerServerConfigUtil.getServerDisplayName(plugin, server.getName()), position, queueSize));
					break;
				case "restarting":
					ProxyServer.getInstance().getPlayer(player).sendMessage(ChatMessageType.ACTION_BAR, ChatUtil.getConfigMessageAsComponent(plugin, "Notifications.ServerIsRestarting", PerServerConfigUtil.getServerDisplayName(plugin, server.getName()), position, queueSize));
					break;
			}
		}
	}

	private static void processQueueSending(QueueableServer server, UQueue plugin) {
		List<UUID> queue = server.getQueuedPlayers();

		boolean ignoreFull = PerServerConfigUtil.getBoolean(plugin, server.getName(), "InfiniteSlots");

		boolean dontSend = plugin.disabledServers.contains(server.getName()) || (!ignoreFull && server.getStatus() != ServerStatus.SPACE_AVAILABLE);

		ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server.getName());

		int pps = PerServerConfigUtil.getInt(plugin, server.getName(), "PlayersPerSecond");
		String queueServerString = PerServerConfigUtil.getString(plugin, server.getName(), "QueueServer");
		QueueableServer queueServer = null;
		ServerInfo queueServerInfo = null;
		boolean queueIgnoreFull = false;
		if (!queueServerString.equals("")) {
			queueServerInfo = ProxyServer.getInstance().getServerInfo(queueServerString);
			if (queueServerInfo != null) {
				queueServer = plugin.getServer(queueServerInfo);
				queueServer.setHoldServer(true);
				queueIgnoreFull = PerServerConfigUtil.getBoolean(plugin, queueServer.getName(), "InfiniteSlots");
			}
		}

		for (int i = 0; i < queue.size() && i < pps; i++) {
			UUID target = queue.get(i);
			ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(target);
			QueueablePlayer queueablePlayer = plugin.getPlayer(proxiedPlayer);

			if (queueServer != null && !proxiedPlayer.getServer().getInfo().getName().equals(queueServer.getName()) && queueServer.getStatus() != null && queueServer.getStatus() != ServerStatus.OFFLINE && (queueIgnoreFull || queueServer.getAvailableSlots() > 0)) {
				queueServer.setAvailableSlots(queueServer.getAvailableSlots() - 1);

				proxiedPlayer.connect(queueServerInfo);

				// Do not send to both servers in the same second.
				continue;
			}

			if (dontSend) continue;

			if (!ignoreFull && i >= server.getAvailableSlots()) continue;

			if (proxiedPlayer.getServer() != null && proxiedPlayer.getServer().getInfo().getName().equals(server.getName())) {
				server.removeFromQueue(queueablePlayer);
				return;
			}

			queueablePlayer.setConnectionAttempts(queueablePlayer.getConnectionAttempts() + 1);

			proxiedPlayer.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Notifications.SendingYou", PerServerConfigUtil.getServerDisplayName(plugin, server.getName())));
			proxiedPlayer.connect(serverInfo);

			if (queueablePlayer.getConnectionAttempts() > PerServerConfigUtil.getInt(plugin, server.getName(), "MaxSendAttempts")) {
				server.removeFromQueue(queueablePlayer);
				proxiedPlayer.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Notifications.ReachedMaxAttempts", PerServerConfigUtil.getServerDisplayName(plugin, server.getName())));
			}
		}
	}

	public static void processServerPings(UQueue plugin) {
		Collection<QueueableServer> serversToPing = plugin.queueableServers.values();

		for (QueueableServer server : serversToPing) {
			if (PerServerConfigUtil.getBoolean(plugin, server.getName(), "NoPings")) {
				server.setStatus(ServerStatus.SPACE_AVAILABLE);
				server.setAvailableSlots(9999);
				continue;
			}
			if (!server.isHoldServer() && PerServerConfigUtil.getBoolean(plugin, server.getName(), "NoPingIfQueueEmpty") && server.getQueueLength() == 0) {
				server.setStatus(null);
				continue;
			}

			String queueServer = PerServerConfigUtil.getString(plugin, server.getName(), "QueueServer");

			if (!queueServer.equals("")) {
				ServerInfo serverInfo = plugin.getProxy().getServerInfo(queueServer);
				if (serverInfo != null) {
					QueueableServer queueableQueueServer = plugin.getServer(serverInfo);
					queueableQueueServer.setHoldServer(true);
				}
			}

			ProxyServer.getInstance().getServerInfo(server.getName()).ping((serverPing, throwable) -> {
				ServerStatus status = ServerStatus.OFFLINE;

				if (serverPing != null) {
					int slotsAvailable = serverPing.getPlayers().getMax() - serverPing.getPlayers().getOnline();
					server.setAvailableSlots(slotsAvailable);
					status = slotsAvailable <= 0 ? ServerStatus.FULL : ServerStatus.SPACE_AVAILABLE;
				}

				server.setStatus(status);
			});
		}
	}

	public static void processPluginMessages(UQueue plugin) {
		for (ProxiedPlayer p : plugin.getProxy().getPlayers()) {
			if (p.getServer() == null) continue;

			QueueablePlayer queueablePlayer = plugin.getPlayer(p);

			boolean queued = queueablePlayer.isQueued();
			String server = queued ? queueablePlayer.getQueuedServer().getName() : "";
			int queuePosition = queued ? queueablePlayer.getQueuedServer().getQueuePosition(queueablePlayer) : 0;
			int queueTotal = queued ? queueablePlayer.getQueuedServer().getQueueLength() : 0;

			p.getServer().sendData("uqueue:queueupdate", PluginMessageUtil.toBytes(queued, server, PerServerConfigUtil.getServerDisplayName(plugin, server), queuePosition, queueTotal));
		}
	}

}
