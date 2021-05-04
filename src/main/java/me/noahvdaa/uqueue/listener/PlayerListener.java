package me.noahvdaa.uqueue.listener;

import me.noahvdaa.uqueue.UQueue;
import me.noahvdaa.uqueue.util.ChatUtil;
import me.noahvdaa.uqueue.util.PerServerConfigUtil;
import me.noahvdaa.uqueue.util.PermissionUtil;
import me.noahvdaa.uqueue.util.QueueUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PlayerListener implements Listener {

	private final UQueue plugin;

	public PlayerListener(UQueue plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		UUID player = e.getPlayer().getUniqueId();
		if (!plugin.queuedFor.containsKey(player)) return;
		QueueUtil.removeFromQueue(plugin, player);
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent e) {
		ProxiedPlayer p = e.getPlayer();
		String vHost = p.getPendingConnection().getVirtualHost().getHostString().toLowerCase().replace('.', '_');
		String forcedHost = plugin.getConfig().getString("ForcedHosts.OTHER");

		if (plugin.getConfig().contains("ForcedHosts." + vHost)) {
			forcedHost = plugin.getConfig().getString("ForcedHosts." + vHost);
		}

		if (forcedHost.equals("")) return;
		if (!PermissionUtil.mayQueueForServer(plugin, p, forcedHost)) return;

		ServerInfo server = ProxyServer.getInstance().getServerInfo(forcedHost);
		if (server == null) return;

		if (p.hasPermission("uqueue.bypass." + server.getName())) {
			p.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Notifications.SendingYou", PerServerConfigUtil.getServerDisplayName(plugin, server.getName())));
			p.connect(server);
			return;
		}

		QueueUtil.insertIntoQueue(plugin, server.getName(), p.getUniqueId(), PermissionUtil.getQueuePriority(p, server.getName()));
		p.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.NowQueuedFor", PerServerConfigUtil.getServerDisplayName(plugin, server.getName())));
	}

	@EventHandler
	public void onServerConnected(ServerConnectedEvent e) {
		UUID player = e.getPlayer().getUniqueId();
		if (!plugin.queuedFor.containsKey(player) || !plugin.queuedFor.get(player).equals(e.getServer().getInfo().getName()))
			return;
		QueueUtil.removeFromQueue(plugin, player);
	}

	@EventHandler
	public void onChat(ChatEvent e) {
		if (!plugin.getConfig().getBoolean("Chat.HijackServerCommand")) return;
		if (!e.getMessage().toLowerCase().startsWith("/server")) return;
		e.setMessage(e.getMessage().replaceAll("^/server", "/queue"));
	}

}
