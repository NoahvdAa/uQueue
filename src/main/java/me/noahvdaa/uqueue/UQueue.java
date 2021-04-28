package me.noahvdaa.uqueue;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.internal.settings.ReloadSettings;
import me.noahvdaa.uqueue.commands.QueueCommand;
import me.noahvdaa.uqueue.commands.UQueueCommand;
import me.noahvdaa.uqueue.commands.UnqueueCommand;
import me.noahvdaa.uqueue.config.ConfigUpdateHelper;
import me.noahvdaa.uqueue.config.ConfigValidationHelper;
import me.noahvdaa.uqueue.config.messages.MessagesUpdateHelper;
import me.noahvdaa.uqueue.listener.PlayerListener;
import me.noahvdaa.uqueue.util.ChatUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UQueue extends Plugin {

	private static UQueue instance;
	private Config config;
	private Config messages;
	public static final int configVersion = 1;
	public static final int messagesVersion = 1;
	// Contains the server a player is currently queued for.
	public HashMap<UUID, String> queuedFor;
	// Contains the priority the player had when they queued for this server.
	public HashMap<UUID, Integer> queuePriority;
	// Queues per server.
	public HashMap<String, List<UUID>> queues;

	@Override
	public void onEnable() {
		instance = this;

		queuedFor = new HashMap<>();
		queuePriority = new HashMap<>();
		queues = new HashMap<>();

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
		getProxy().getPluginManager().registerCommand(this, new UnqueueCommand(this));
		getProxy().getPluginManager().registerCommand(this, new UQueueCommand(this));

		// Register events.
		getProxy().getPluginManager().registerListener(this, new PlayerListener(this));

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

		getProxy().getScheduler().schedule(this, new Runnable() {
			@Override
			public void run() {
				for (String server : queues.keySet()) {
					List<UUID> queue = queues.get(server);
					String queueSize = Integer.toString(queue.size());
					for (UUID player : queue) {
						String position = Integer.toString(queue.indexOf(player) + 1);
						getProxy().getPlayer(player).sendMessage(ChatMessageType.ACTION_BAR, ChatUtil.getConfigPlaceholderMessageWithoutPrefixAsComponent(instance, "Notifications.QueuePosition", position, queueSize, server));
					}
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
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
