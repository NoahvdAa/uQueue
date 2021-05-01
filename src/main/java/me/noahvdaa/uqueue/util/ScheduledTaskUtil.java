package me.noahvdaa.uqueue.util;

import me.noahvdaa.uqueue.UQueue;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.UUID;

public class ScheduledTaskUtil {

	public static void processQueueNotifications(UQueue plugin) {
		for (String server : plugin.queues.keySet()) {
			List<UUID> queue = plugin.queues.get(server);
			String queueSize = Integer.toString(queue.size());
			String serverStatus;
			if (!plugin.serverOnlineStatus.containsKey(server)) {
				serverStatus = "online";
			} else if (plugin.serverOnlineStatus.get(server) != ServerStatus.OFFLINE) {
				if (plugin.serverOnlineStatus.get(server) == ServerStatus.SPACE_AVAILABLE) {
					serverStatus = "online";
				} else {
					serverStatus = "full";
				}
			} else {
				long offlineFor = 0L;
				if (plugin.serverStatusSince.containsKey(server)) {
					offlineFor = System.currentTimeMillis() - plugin.serverStatusSince.get(server);
				}
				if (offlineFor > PerServerConfigUtil.getInt(plugin, server, "RestartLength") * 1000L) {
					serverStatus = "offline";
				} else {
					serverStatus = "restarting";
				}
			}
			for (UUID player : queue) {
				String position = Integer.toString(queue.indexOf(player) + 1);
				switch (serverStatus) {
					default:
						ProxyServer.getInstance().getPlayer(player).sendMessage(ChatMessageType.ACTION_BAR, ChatUtil.getConfigPlaceholderMessageWithoutPrefixAsComponent(plugin, "Notifications.QueuePosition", position, queueSize, server));
						break;
					case "full":
						ProxyServer.getInstance().getPlayer(player).sendMessage(ChatMessageType.ACTION_BAR, ChatUtil.getConfigPlaceholderMessageWithoutPrefixAsComponent(plugin, "Notifications.ServerIsFull", server, position, queueSize));
						break;
					case "offline":
						ProxyServer.getInstance().getPlayer(player).sendMessage(ChatMessageType.ACTION_BAR, ChatUtil.getConfigPlaceholderMessageWithoutPrefixAsComponent(plugin, "Notifications.ServerIsOffline", server, position, queueSize));
						break;
					case "restarting":
						ProxyServer.getInstance().getPlayer(player).sendMessage(ChatMessageType.ACTION_BAR, ChatUtil.getConfigPlaceholderMessageWithoutPrefixAsComponent(plugin, "Notifications.ServerIsRestarting", server, position, queueSize));
						break;
				}
			}
			// Server is disabled.
			if (plugin.disabledServers.contains(server))
				continue;
			// Server not online or no space.
			if (!plugin.serverOnlineStatus.containsKey(server) || plugin.serverOnlineStatus.get(server) != ServerStatus.SPACE_AVAILABLE)
				continue;
			ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
			for (int i = 0; i < queue.size() && i < PerServerConfigUtil.getInt(plugin, server, "PlayersPerSecond"); i++) {
				UUID target = queue.get(i);
				if (i >= plugin.slotsFree.get(server)) continue;
				ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(target);

				if (proxiedPlayer.getServer().getInfo().getName().equals(serverInfo.getName())) {
					QueueUtil.removeFromQueue(plugin, target);
					return;
				}

				if (!plugin.connectionAttempts.containsKey(target))
					plugin.connectionAttempts.put(target, 0);

				plugin.connectionAttempts.put(target, plugin.connectionAttempts.get(target) + 1);

				proxiedPlayer.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Notifications.SendingYou", server));
				proxiedPlayer.connect(serverInfo);

				if (plugin.connectionAttempts.get(target) > PerServerConfigUtil.getInt(plugin, server, "MaxSendAttempts")) {
					QueueUtil.removeFromQueue(plugin, target);
					proxiedPlayer.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Notifications.ReachedMaxAttempts", server));
				}
			}
		}
	}

	public static void processServerPings(UQueue plugin) {
		for (String server : plugin.queueableServers) {
			if (PerServerConfigUtil.getBoolean(plugin, server, "NoPingIfQueueEmpty") && !plugin.queues.containsKey(server)) {
				plugin.slotsFree.remove(server);
				plugin.serverOnlineStatus.remove(server);
				plugin.serverStatusSince.remove(server);
				continue;
			}
			ProxyServer.getInstance().getServerInfo(server).ping((serverPing, throwable) -> {
				ServerStatus status = ServerStatus.OFFLINE;
				if (serverPing != null) {
					int slotsAvailable = serverPing.getPlayers().getMax() - serverPing.getPlayers().getOnline();
					plugin.slotsFree.put(server, slotsAvailable);
					status = slotsAvailable <= 0 ? ServerStatus.FULL : ServerStatus.SPACE_AVAILABLE;
				}
				ServerStatus previousStatus = plugin.serverOnlineStatus.get(server);
				plugin.serverOnlineStatus.put(server, status);
				if (!plugin.serverStatusSince.containsKey(server)) {
					plugin.serverStatusSince.put(server, System.currentTimeMillis());
					return;
				}
				if (!previousStatus.equals(status)) plugin.serverStatusSince.put(server, System.currentTimeMillis());
			});
		}
	}

	public static void processPluginMessages(UQueue plugin) {
		for (ProxiedPlayer p : plugin.getProxy().getPlayers()) {
			if (p.getServer() == null) continue;
			boolean queued = plugin.queuedFor.containsKey(p.getUniqueId());
			String server = queued ? plugin.queuedFor.get(p.getUniqueId()) : "";
			int queuePosition = queued ? plugin.queues.get(server).indexOf(p.getUniqueId()) + 1 : 0;
			int queueTotal = queued ? plugin.queues.get(server).size() : 0;

			p.getServer().sendData("uqueue:queueupdate", PluginMessageUtil.toBytes(queued, server, queuePosition, queueTotal));
		}
	}

}
