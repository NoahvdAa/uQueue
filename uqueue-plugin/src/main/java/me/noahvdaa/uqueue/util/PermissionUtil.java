package me.noahvdaa.uqueue.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.query.QueryOptions;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;

public class PermissionUtil {

	public static String getPermissionProvider() {
		if (ProxyServer.getInstance().getPluginManager().getPlugin("LuckPerms") != null) {
			return "LuckPerms";
		}
		return "other";
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
