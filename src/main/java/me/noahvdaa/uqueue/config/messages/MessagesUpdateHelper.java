package me.noahvdaa.uqueue.config.messages;

import com.google.common.io.Files;
import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.internal.settings.ReloadSettings;
import me.noahvdaa.uqueue.UQueue;

import java.io.File;
import java.io.IOException;

public class MessagesUpdateHelper {

	public static boolean updateMessages(Config config, UQueue plugin) {
		try {
			File messagesFile = new File(plugin.getDataFolder() + File.separator + "messages.yml");
			File backupFile = new File(plugin.getDataFolder() + File.separator + "messages.backup.yml");
			Files.copy(messagesFile, backupFile);

			String tmpName = "messages." + System.currentTimeMillis() + ".yml";
			File tmpFile = new File(plugin.getDataFolder().getPath() + File.separator + tmpName);

			Config newConfig = LightningBuilder
					.fromPath(tmpName, plugin.getDataFolder().getPath())
					.addInputStreamFromResource("messages.yml")
					.setDataType(DataType.SORTED)
					.setReloadSettings(ReloadSettings.MANUALLY)
					.setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
					.createConfig();

			for (String key : config.keySet()) {
				newConfig.set(key, config.get(key));
			}

			newConfig.set("messagesVersion", UQueue.messagesVersion);

			newConfig.write();
			messagesFile.delete();
			Files.move(tmpFile, messagesFile);
		} catch (IOException e) {
			plugin.getLogger().warning("Failed to update messages:");
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
