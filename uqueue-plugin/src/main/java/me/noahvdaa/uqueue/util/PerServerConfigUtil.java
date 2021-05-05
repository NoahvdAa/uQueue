package me.noahvdaa.uqueue.util;

import me.noahvdaa.uqueue.UQueue;

public class PerServerConfigUtil {

	public static int getInt(UQueue plugin, String serverName, String key) {
		if (!plugin.getPerServerConfig().contains(serverName) || !plugin.getPerServerConfig().contains(serverName + "." + key)) {
			return plugin.getConfig().getInt("Queueing." + key);
		}
		return plugin.getPerServerConfig().getInt(serverName + "." + key);
	}

	public static boolean getBoolean(UQueue plugin, String serverName, String key) {
		if (!plugin.getPerServerConfig().contains(serverName) || !plugin.getPerServerConfig().contains(serverName + "." + key)) {
			return plugin.getConfig().getBoolean("Queueing." + key);
		}
		return plugin.getPerServerConfig().getBoolean(serverName + "." + key);
	}

	public static String getString(UQueue plugin, String serverName, String key) {
		if (!plugin.getPerServerConfig().contains(serverName) || !plugin.getPerServerConfig().contains(serverName + "." + key)) {
			return plugin.getConfig().getString("Queueing." + key);
		}
		return plugin.getPerServerConfig().getString(serverName + "." + key);
	}

	public static String getServerDisplayName(UQueue plugin, String serverName) {
		if (!plugin.getPerServerConfig().contains(serverName) || !plugin.getPerServerConfig().contains(serverName + ".DisplayAs")) {
			return serverName;
		}
		return plugin.getPerServerConfig().getString(serverName + ".DisplayAs");
	}

}
