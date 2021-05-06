package me.noahvdaa.uqueue.api;

import me.noahvdaa.uqueue.api.util.QueueablePlayer;
import me.noahvdaa.uqueue.api.util.QueueableServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * The main class of the UQueue API.
 */
public interface UQueuePlugin {

	/**
	 * Returns the QueueablePlayer object from a ProxiedPlayer.
	 *
	 * @param player The ProxiedPlayer to get a QueueablePlayer instance from.
	 * @return The Queueable player belonging to this ProxiedPlayer.
	 */
	QueueablePlayer getPlayer(ProxiedPlayer player);

	/**
	 * Returns the QueueableServer object from ServerInfo.
	 *
	 * @param info The ServerInfo to get a QueueablePlayer instance from.
	 * @return The Queueable server belonging to this ServerInfo.
	 */
	QueueableServer getServer(ServerInfo info);

}
