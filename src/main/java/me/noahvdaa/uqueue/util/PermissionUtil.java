package me.noahvdaa.uqueue.util;

import me.noahvdaa.uqueue.UQueue;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.query.QueryOptions;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

public class PermissionUtil {

	public static int getQueuePriority(ProxiedPlayer p, String server) {
		int priority = 0;

		for (String permission : getPermissions(p)) {
			// It's not a uQueue priority permission or the format isn't right.
			if (!permission.toLowerCase().startsWith("uqueue.priority") || permission.split("\\.").length != 4)
				continue;
			String[] parts = permission.split("\\.");
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

	public static String getPermissionProvider() {
		if (ProxyServer.getInstance().getPluginManager().getPlugin("LuckPerms") != null) {
			return "LuckPerms";
		}
		return "other";
	}

	public static boolean mayQueueForServer(UQueue plugin, ProxiedPlayer p, String server) {
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

	public static Collection<String> getPermissions(ProxiedPlayer p) {
		String permissionProvider = getPermissionProvider();

		if (permissionProvider.equals("LuckPerms")) {
			LuckPerms lpAPI = LuckPermsProvider.get();

			User lpuser = lpAPI.getUserManager().getUser(p.getUniqueId());

			// Fetch all permissions with the current context.
			SortedSet<Node> permissions = lpuser.resolveDistinctInheritedNodes(QueryOptions.contextual(lpuser.getQueryOptions().context()));
			Collection<String> out = new ArrayList<String>();

			for (Node node : permissions) {
				if (node.getType() != NodeType.PERMISSION) continue;
				// Permission is set to false.
				if (!node.getValue()) continue;
				out.add(node.getKey());
			}

			return out;
		}

		// TODO: Integrations for other permission plugins.

		// Fallback if permission plugin isn't officially supported, may not always work properly.
		return p.getPermissions();
	}

}
