package me.noahvdaa.uqueue.util;

import me.noahvdaa.uqueue.UQueue;

import java.util.ArrayList;
import java.util.UUID;

public class QueueUtil {

	public static void insertIntoQueue(UQueue plugin, String server, UUID target, int priority) {
		// Inform server pinger that this server is queueable.
		if (!plugin.queueableServers.contains(server))
			plugin.queueableServers.add(server);

		// Make a queue if it doesn't exist yet.
		if (!plugin.queues.containsKey(server))
			plugin.queues.put(server, new ArrayList<UUID>());

		// Find queue position to insert into.
		int pos = 0;
		for (UUID queuedPlayer : plugin.queues.get(server)) {
			if (plugin.queuePriority.get(queuedPlayer) < priority) break;
			pos++;
		}

		// Set player queue target.
		plugin.queuedFor.put(target, server);

		// Add player in right queue position.
		plugin.queues.get(server).add(pos, target);
		// Store priority for later comparison.
		plugin.queuePriority.put(target, priority);
	}

	public static void removeFromQueue(UQueue plugin, UUID target) {
		String queuedFor = plugin.queuedFor.remove(target);
		plugin.queuePriority.remove(target);
		plugin.queues.get(queuedFor).remove(target);
		if (plugin.queues.get(queuedFor).size() == 0) {
			// Clean up unused queues.
			plugin.queues.remove(queuedFor);
		}
		if (plugin.connectionAttempts.containsKey(target)) {
			plugin.connectionAttempts.remove(target);
		}
	}

}
