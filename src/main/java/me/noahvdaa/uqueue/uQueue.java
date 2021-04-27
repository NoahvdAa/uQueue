package me.noahvdaa.uqueue;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.internal.settings.ReloadSettings;
import me.noahvdaa.uqueue.commands.QueueCommand;
import me.noahvdaa.uqueue.commands.uQueueCommand;
import me.noahvdaa.uqueue.config.ConfigUpdater;
import me.noahvdaa.uqueue.config.ConfigValidator;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;

public class uQueue extends Plugin {

	private static uQueue instance;
	private Config config;
	public static final int configVersion = 2;
	public HashMap<String, TreeMap<Integer, ArrayList<UUID>>> queue;

	@Override
	public void onEnable() {
		instance = this;

		queue = new HashMap<String, TreeMap<Integer, ArrayList<UUID>>>();

		// Initialize config.
		config = LightningBuilder
				.fromFile(new File(getDataFolder().getPath() + File.separator + "config.yml"))
				.addInputStreamFromResource("config.yml")
				.setDataType(DataType.SORTED)
				.setReloadSettings(ReloadSettings.MANUALLY)
				.setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
				.createConfig();

		// Register commands.
		getProxy().getPluginManager().registerCommand(this, new QueueCommand(this));
		getProxy().getPluginManager().registerCommand(this, new uQueueCommand(this));

		// Update config if needed.
		if(config.getInt("configVersion") != configVersion){
			boolean updateResult = ConfigUpdater.updateConfig(config, this);
			if (!updateResult) return;
			config.forceReload();
		}

		// Verify config.
		ConfigValidator.validateConfig(config, getLogger());
	}

	public static uQueue getInstance() {
		return instance;
	}

	public Config getConfig() {
		return config;
	}

}
