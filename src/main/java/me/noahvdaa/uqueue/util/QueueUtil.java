package me.noahvdaa.uqueue.util;

import me.noahvdaa.uqueue.UQueue;

import java.util.ArrayList;
import java.util.UUID;

public class QueueUtil {

	public static void insertIntoQueue(UQueue plugin, String server, UUID target, int priority) {
		plugin.queuedFor.put(target, server);
		if (!plugin.queues.containsKey(server)) {
			plugin.queues.put(server, new ArrayList<UUID>());
		}
		int pos = 0;
		for (UUID queuedPlayer : plugin.queues.get(server)) {
			if (plugin.queuePriority.get(queuedPlayer) < priority) break;
			pos++;
		}
		plugin.queues.get(server).add(pos, target);
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
	}

}
