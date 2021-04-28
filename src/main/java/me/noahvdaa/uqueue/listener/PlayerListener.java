package me.noahvdaa.uqueue.listener;

import me.noahvdaa.uqueue.UQueue;
import me.noahvdaa.uqueue.util.QueueUtil;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PlayerListener implements Listener {

	private UQueue plugin;

	public PlayerListener(UQueue plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		UUID player = e.getPlayer().getUniqueId();
		if (!plugin.queuedFor.containsKey(player)) return;
		QueueUtil.removeFromQueue(plugin, player);
	}

}
