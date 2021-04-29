package me.noahvdaa.uqueue.util;

import me.noahvdaa.uqueue.UQueue;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;

import java.util.List;
import java.util.UUID;

public class ScheduledTaskUtil {

	public static void processQueueNotifications(UQueue plugin) {
		for (String server : plugin.queues.keySet()) {
			List<UUID> queue = plugin.queues.get(server);
			String queueSize = Integer.toString(queue.size());
			String serverStatus;
			if (!plugin.serverOnlineStatus.containsKey(server) || plugin.serverOnlineStatus.get(server)) {
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
			// Still being pinged.
			if (!plugin.serverOnlineStatus.containsKey(server)) continue;
		}
	}

	public static void processServerPings(UQueue plugin) {
		for (String server : plugin.queueableServers) {
			ProxyServer.getInstance().getServerInfo(server).ping((serverPing, throwable) -> {
				boolean status = serverPing != null;
				boolean previousStatus = plugin.serverOnlineStatus.get(server);
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
