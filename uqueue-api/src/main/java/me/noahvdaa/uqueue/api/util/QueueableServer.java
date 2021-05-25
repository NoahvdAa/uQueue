package me.noahvdaa.uqueue.api.util;

import java.util.List;
import java.util.UUID;

/**
 * A server, that can be queued for.
 */
public interface QueueableServer {

	/**
	 * Returns the server's name.
	 *
	 * @return The name of this server.
	 */
	String getName();

	/**
	 * Returns the display name of the server. This display name may be the same for multiple servers.
	 *
	 * @return The display name of this server.
	 */
	String getDisplayName();

	/**
	 * Returns the player's position in the queue or null if they aren't queued for this server.
	 * The first position in the queue is "1".
	 *
	 * @param player The player to look up.
	 * @return The position the player is in.
	 */
	Integer getQueuePosition(QueueablePlayer player);

	/**
	 * Returns the total amount of people queued for this serve.
	 *
	 * @return The amount of people in queue for this server.
	 */
	Integer getQueueLength();

	/**
	 * Get the player's current priority level.
	 * NOTE: This is a fairly intensive method! You might want to consider using getCachedPriority() instead.
	 *
	 * @param player The player to look up.
	 * @return The player's current priority level.
	 */
	Integer getCurrentPriority(QueueablePlayer player);

	/**
	 * Get the player's current priority level.
	 * NOTE: This is a fairly intensive method! You might want to consider using getCachedPriority() instead.
	 *
	 * @param player The player to look up.
	 * @return The player's current priority level.
	 */
	Integer getCurrentPriority(UUID player);

	/**
	 * Get the player's priority level when they joined the queue.
	 * Returns the player's current priority if no cached value exists.
	 *
	 * @param player The player to look up.
	 * @return The player's cached priority level.
	 */
	Integer getCachedPriority(QueueablePlayer player);

	/**
	 * Get the player's priority level when they joined the queue.
	 * Returns the player's current priority if no cached value exists.
	 *
	 * @param player The player to look up.
	 * @return The player's cached priority level.
	 */
	Integer getCachedPriority(UUID player);

	/**
	 * Returns true if the specified player is allowed to queue for this server.
	 *
	 * @param player The player to check permissions for.
	 * @return Whether or not the player is allowed to queue.
	 */
	Boolean mayQueue(QueueablePlayer player);

	/**
	 * Returns true if the server is a server that can be used to "hold" players while they're queued for a different server.
	 *
	 * @return Whether or not the server is a hold server.
	 */
	boolean isHoldServer();

	/**
	 * Returns the server's current status or null if it hasn't been pinged yet.
	 *
	 * @return The status of this server.
	 */
	ServerStatus getStatus();

	/**
	 * Returns when the server's status was last updated or null if it hasn't been set yet.
	 *
	 * @return The epoch timestamp the server's status was last updated.
	 */
	Long getStatusLastUpdated();

	/**
	 * Returns the amount of available slots this server has or null if it hasn't been pinged yet.
	 *
	 * @return The available slots for this server.
	 */
	Integer getAvailableSlots();

	/**
	 * Returns a list of all players queued for this server.
	 *
	 * @return A list of all players that are queued for this server.
	 */
	List<UUID> getQueuedPlayers();

	/**
	 * Set whether or not the server is a hold server.
	 *
	 * @param newHoldServer The new value of isHoldServer().
	 */
	void setHoldServer(boolean newHoldServer);

	/**
	 * Set the status for this server.
	 *
	 * @param newStatus The new status to set.
	 */
	void setStatus(ServerStatus newStatus);

	/**
	 * Set the available slots for this server.
	 *
	 * @param newAvailableSlots The new available slots to set.
	 */
	void setAvailableSlots(int newAvailableSlots);

	/**
	 * Queues the specified player. If the player was already queued, nothing will happen.
	 *
	 * @param player The player to add to the queue.
	 */
	void addToQueue(QueueablePlayer player);

	/**
	 * Un-queues the specified player. If the player wasn't queued, nothing will happen.
	 *
	 * @param player The player to remove from the queue.
	 */
	void removeFromQueue(QueueablePlayer player);

}
