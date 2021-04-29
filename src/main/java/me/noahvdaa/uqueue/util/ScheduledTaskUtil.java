package me.noahvdaa.uqueue.util;

import me.noahvdaa.uqueue.ServerStatus;
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
			if (!plugin.serverOnlineStatus.containsKey(server) || plugin.serverOnlineStatus.get(server) != ServerStatus.OFFLINE) {
				serverStatus = "online";
			} else {
				long offlineFor = 0L;
				if (plugin.serverStatusSince.containsKey(server)) {
					offlineFor = System.currentTimeMillis() - plugin.serverStatusSince.get(server);
				}
				if (offlineFor > plugin.getConfig().getInt("Queueing.RestartLength") * 1000L) {
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
					case "offline":
						ProxyServer.getInstance().getPlayer(player).sendMessage(ChatMessageType.ACTION_BAR, ChatUtil.getConfigPlaceholderMessageWithoutPrefixAsComponent(plugin, "Notifications.ServerIsOffline", server, position, queueSize));
						break;
					case "restarting":
						ProxyServer.getInstance().getPlayer(player).sendMessage(ChatMessageType.ACTION_BAR, ChatUtil.getConfigPlaceholderMessageWithoutPrefixAsComponent(plugin, "Notifications.ServerIsRestarting", server, position, queueSize));
						break;
				}
			}
			// Server not online or no space.
			if (!plugin.serverOnlineStatus.containsKey(server) || plugin.serverOnlineStatus.get(server) != ServerStatus.SPACE_AVAILABLE)
				continue;
			ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
			for (int i = 0; i < queue.size() && i < plugin.getConfig().getInt("Queueing.PlayersPerSecond"); i++) {
				UUID target = queue.get(i);
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

				if (plugin.connectionAttempts.get(target) > plugin.getConfig().getInt("Queueing.MaxSendAttempts")) {
					QueueUtil.removeFromQueue(plugin, target);
					proxiedPlayer.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Notifications.ReachedMaxAttempts", server));
				}
			}
		}
	}

	public static void processServerPings(UQueue plugin) {
		for (String server : plugin.queueableServers) {
			ProxyServer.getInstance().getServerInfo(server).ping((serverPing, throwable) -> {
				ServerStatus status = ServerStatus.OFFLINE;
				if (serverPing != null) {
					status = serverPing.getPlayers().getOnline() >= serverPing.getPlayers().getMax() ? ServerStatus.FULL : ServerStatus.SPACE_AVAILABLE;
				}
				ServerStatus previousStatus = plugin.serverOnlineStatus.get(server);
				plugin.serverOnlineStatus.put(server, status);
				if (!plugin.serverStatusSince.containsKey(server)) {
					plugin.serverStatusSince.put(server, System.currentTimeMillis());
					return;
				}
				if (previousStatus != status) plugin.serverStatusSince.put(server, System.currentTimeMillis());
			});
		}
	}

}
