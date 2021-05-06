package me.noahvdaa.uqueue.util.permissions;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.query.QueryOptions;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

public class LuckPermsPermissionUtil {

	public static Collection<String> getPermissions(ProxiedPlayer p) {
		LuckPerms lpAPI = LuckPermsProvider.get();

		User lpUser = lpAPI.getUserManager().getUser(p.getUniqueId());

		// Fetch all permissions with the current context.
		SortedSet<Node> permissions = lpUser.resolveDistinctInheritedNodes(QueryOptions.contextual(lpUser.getQueryOptions().context()));
		List<String> out = new ArrayList<>();

		for (Node node : permissions) {
			if (node.getType() != NodeType.PERMISSION) continue;
			// Permission is set to false.
			if (!node.getValue()) continue;
			out.add(node.getKey());
		}

		return out;
	}

}
