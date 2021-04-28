package me.noahvdaa.uqueue;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.internal.settings.ReloadSettings;
import me.noahvdaa.uqueue.commands.QueueCommand;
import me.noahvdaa.uqueue.commands.UQueueCommand;
import me.noahvdaa.uqueue.config.ConfigUpdateHelper;
import me.noahvdaa.uqueue.config.ConfigValidationHelper;
import me.noahvdaa.uqueue.config.messages.MessagesUpdateHelper;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;

public class UQueue extends Plugin {

	private static UQueue instance;
	private Config config;
	private Config messages;
	public static final int configVersion = 1;
	public static final int messagesVersion = 1;
	public HashMap<String, TreeMap<Integer, ArrayList<UUID>>> queue;

	@Override
	public void onEnable() {
		instance = this;

		queue = new HashMap<>();

		// Initialize config.
		config = LightningBuilder
				.fromFile(new File(getDataFolder().getPath() + File.separator + "config.yml"))
				.addInputStreamFromResource("config.yml")
				.setDataType(DataType.SORTED)
				.setReloadSettings(ReloadSettings.MANUALLY)
				.setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
				.createConfig();

		// Initialize messages.
		messages = LightningBuilder
				.fromFile(new File(getDataFolder().getPath() + File.separator + "messages.yml"))
				.addInputStreamFromResource("messages.yml")
				.setDataType(DataType.SORTED)
				.setReloadSettings(ReloadSettings.MANUALLY)
				.setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
				.createConfig();

		// Register commands.
		getProxy().getPluginManager().registerCommand(this, new QueueCommand(this));
		getProxy().getPluginManager().registerCommand(this, new UQueueCommand(this));

		// Update config if needed.
		if (config.getInt("configVersion") != configVersion) {
			boolean updateResult = ConfigUpdateHelper.updateConfig(config, this);
			if (!updateResult) return;
			config.forceReload();
		}

		// Update messages if needed.
		if (messages.getInt("configVersion") != messagesVersion) {
			boolean msgUpdateResult = MessagesUpdateHelper.updateMessages(config, this);
			if (!msgUpdateResult) return;
			messages.forceReload();
		}

		// Verify config.
		ConfigValidationHelper.validateConfig(config, getLogger());
	}

	public static UQueue getInstance() {
		return instance;
	}

	public Config getConfig() {
		return this.config;
	}

	public Config getMessages() {
		return this.messages;
	}

}
