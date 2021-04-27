package me.noahvdaa.uqueue.util;

import me.noahvdaa.uqueue.uQueue;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PermissionUtil {

	public static int getQueuePriority(ProxiedPlayer p, String server) {
		int priority = 0;

		for (String permission : p.getPermissions()) {
			// It's not a uQueue priority permissino or the format isn't right.
			if (!permission.toLowerCase().startsWith("uqueue.priority") || permission.split(".").length != 4) continue;
			String[] parts = permission.split(".");
			// Extract the parts.
			String target = parts[2];
			String prio = parts[3];
			// Doesn't affect this server or prio is not a number.
			if ((!target.equalsIgnoreCase(server) && !target.equals("*")) || !prio.matches("\\d+")) continue;
			int parsedPrio = Integer.parseInt(prio);
			if (parsedPrio > priority) priority = parsedPrio;
		}

		return priority;
	}

	public static boolean mayQueueForServer(uQueue plugin, ProxiedPlayer p, String server) {
		String serverListMode = plugin.getConfig().getString("Queueing.ServerListMode");
		// We can use if/else here, because the config validator ensures that the setting
		// is always either 'blacklist' or 'whitelist'.
		if (serverListMode.equalsIgnoreCase("blacklist")) {
			if (plugin.getConfig().getStringList("Queueing.ServerList").contains(server)) {
				return p.hasPermission("uqueue.server." + server);
			}
			return true;
		} else {
			if (!plugin.getConfig().getStringList("Queueing.ServerList").contains(server)) {
				return p.hasPermission("uqueue.server." + server);
			}
			return true;
		}
	}

}
