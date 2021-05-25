package me.noahvdaa.uqueue.api.util;

import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * A player, that can queue for servers.
 */
public interface QueueablePlayer {

	/**
	 * Returns true if a player is queued for any server.
	 *
	 * @return Whether or not the player is queued.
	 */
	Boolean isQueued();

	/**
	 * Returns the QueueableServer the player is queued for, or null if they aren't queued.
	 *
	 * @return The QueueServer the player belongs to or null.
	 */
	QueueableServer getQueuedServer();

	/**
	 * Returns the ProxiedPlayer this QueueablePlayer belongs to.
	 *
	 * @return The parent ProxiedPlayer.
	 */
	ProxiedPlayer getPlayer();

	/**
	 * Returns the amount of times this player has tried to connect to the server they're queued for or null if they aren't queued.
	 *
	 * @return The amount of times this player has tried to connect to the target server.
	 */
	Integer getConnectionAttempts();

	/**
	 * Sets the server the player is currently queued for. Pass null to mark as not queued.
	 *
	 * @param server The server to mark as queued.
	 */
	void setQueuedServer(QueueableServer server);

	/**
	 * Sets the amount of times the player has tried to connect to the server they're queued for.
	 *
	 * @param newConnectionAttempts The new value for connection attempts.
	 */
	void setConnectionAttempts(int newConnectionAttempts);

}
