package me.noahvdaa.uqueue.config;

import de.leonhard.storage.Config;

import java.util.logging.Logger;

public class ConfigValidationHelper {

	public static void validateConfig(Config config, Logger logger) {
		String serverListMode = config.getString("Queueing.ServerListMode");
		if (!serverListMode.equalsIgnoreCase("blacklist") && !serverListMode.equalsIgnoreCase("whitelist")) {
			logger.warning("Unexpected value '" + serverListMode + "' for Queueing->ServerListMode. Accepted values are: blacklist, whitelist. Falling back to default value, 'blacklist'.");
			config.set("Queueing.ServerListMode", "blacklist");
		}
	}

}
