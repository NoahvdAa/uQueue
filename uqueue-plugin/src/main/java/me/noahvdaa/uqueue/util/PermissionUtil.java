package me.noahvdaa.uqueue.util;

import me.noahvdaa.uqueue.util.permissions.BungeePermsPermissionUtil;
import me.noahvdaa.uqueue.util.permissions.LuckPermsPermissionUtil;
import me.noahvdaa.uqueue.util.permissions.UltraPermissionsPermissionUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;

import java.util.Collection;

public class PermissionUtil {

	public static String getPermissionProvider() {
		PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();

		if (pluginManager.getPlugin("BungeePerms") != null) {
			return "BungeePerms";
		} else if (pluginManager.getPlugin("LuckPerms") != null) {
			return "LuckPerms";
		} else if (pluginManager.getPlugin("UltraPermissions") != null) {
			return "UltraPermissions";
		}

		return "Other";
	}

	public static Collection<String> getPermissions(ProxiedPlayer p) {
		String permissionProvider = getPermissionProvider();

		// Permission fetchers are split across classes to prevent ClassNotFound exceptions.

		switch (permissionProvider) {
			case "BungeePerms":
				return BungeePermsPermissionUtil.getPermissions(p);
			case "LuckPerms":
				return LuckPermsPermissionUtil.getPermissions(p);
			case "UltraPermissions":
				return UltraPermissionsPermissionUtil.getPermissions(p);
			default:
				// Fallback if permission plugin isn't officially supported, may not always work properly.
				return p.getPermissions();
		}
	}

}
