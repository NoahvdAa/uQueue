package me.noahvdaa.uqueue.config;

import com.google.common.io.Files;
import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.internal.settings.ReloadSettings;
import me.noahvdaa.uqueue.UQueue;

import java.io.File;
import java.io.IOException;

public class ConfigUpdateHelper {

	public static boolean updateConfig(Config config, UQueue plugin) {
		try {
			File configFile = new File(plugin.getDataFolder() + File.separator + "config.yml");
			File backupFile = new File(plugin.getDataFolder() + File.separator + "config.backup.yml");
			Files.copy(configFile, backupFile);

			String tmpName = "config." + System.currentTimeMillis() + ".yml";
			File tmpFile = new File(plugin.getDataFolder().getPath() + File.separator + tmpName);

			Config newConfig = LightningBuilder
					.fromPath(tmpName, plugin.getDataFolder().getPath())
					.addInputStreamFromResource("config.yml")
					.setDataType(DataType.SORTED)
					.setReloadSettings(ReloadSettings.MANUALLY)
					.setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
					.createConfig();

			for (String key : config.keySet()) {
				newConfig.set(key, config.get(key));
			}

			newConfig.set("configVersion", plugin.configVersion);

			newConfig.write();
			configFile.delete();
			Files.move(tmpFile, configFile);
		} catch (IOException e) {
			plugin.getLogger().warning("Failed to update config:");
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
