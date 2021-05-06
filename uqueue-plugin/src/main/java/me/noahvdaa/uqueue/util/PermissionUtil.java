package me.noahvdaa.uqueue.util;

import me.noahvdaa.uqueue.util.permissions.BungeePermsPermissionUtil;
import me.noahvdaa.uqueue.util.permissions.LuckPermsPermissionUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

public class PermissionUtil {

	public static String getPermissionProvider() {
		PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();
		if (pluginManager.getPlugin("BungeePerms") != null) {
			return "BungeePerms";
		} else if (pluginManager.getPlugin("LuckPerms") != null) {
			return "LuckPerms";
		}
		return "other";
	}

	public static Collection<String> getPermissions(ProxiedPlayer p) {
		String permissionProvider = getPermissionProvider();

		// Permission fetchers are split across classes to prevent ClassNotFound exceptions.

		if (permissionProvider.equals("BungeePerms")) {
			return BungeePermsPermissionUtil.getBungeePermsPermissions(p);
		} else if (permissionProvider.equals("LuckPerms")) {
			return LuckPermsPermissionUtil.getPermissions(p);
		}

		// TODO: Integrations for other permission plugins.

		// Fallback if permission plugin isn't officially supported, may not always work properly.
		return p.getPermissions();
	}

}
