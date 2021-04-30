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
import me.noahvdaa.uqueue.util.ScheduledTaskUtil;
import me.noahvdaa.uqueue.util.ServerStatus;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UQueue extends Plugin {

	private static UQueue instance;
	private Config config;
	private Config messages;
	private Config perServerConfig;

	public static final int configVersion = 1;
	public static final int messagesVersion = 1;

	// Contains the server a player is currently queued for.
	public HashMap<UUID, String> queuedFor;
	// Contains the priority the player had when they queued for this server.
	public HashMap<UUID, Integer> queuePriority;
	// Queues per server.
	public HashMap<String, List<UUID>> queues;
	// Server online status
	public HashMap<String, ServerStatus> serverOnlineStatus;
	// Servers that have been queued for since load. Used for pinging.
	public List<String> queueableServers;
	// How long have these servers had their current status?
	public HashMap<String, Long> serverStatusSince;
	// Amount of times we've tried to connect the player.
	public HashMap<UUID, Integer> connectionAttempts;
	// The amount of slots still available for a server.
	public HashMap<String, Integer> slotsFree;
	// Servers that have queueing disabled.
	public List<String> disabledServers;

	@Override
	public void onEnable() {
		instance = this;

		queuedFor = new HashMap<>();
		queuePriority = new HashMap<>();
		queues = new HashMap<>();
		serverOnlineStatus = new HashMap<>();
		queueableServers = new ArrayList<>();
		serverStatusSince = new HashMap<>();
		connectionAttempts = new HashMap<>();
		slotsFree = new HashMap<>();
		disabledServers = new ArrayList<>();

		initializeConfigs();

		// Register commands.
		getProxy().getPluginManager().registerCommand(this, new QueueCommand(this));
		getProxy().getPluginManager().registerCommand(this, new UnqueueCommand(this));
		getProxy().getPluginManager().registerCommand(this, new UQueueCommand(this));

		// Register plugin message channel.
		getProxy().registerChannel("uqueue:queueupdate");

		// Register events.
		getProxy().getPluginManager().registerListener(this, new PlayerListener(this));

		// Process queue.
		getProxy().getScheduler().schedule(this, () -> ScheduledTaskUtil.processQueueNotifications(instance), 1, 1, TimeUnit.SECONDS);

		// Process plugin messages for placeholders.
		getProxy().getScheduler().schedule(this, () -> ScheduledTaskUtil.processPluginMessages(instance), 1, 1, TimeUnit.SECONDS);

		// Ping servers to check if they're up.
		getProxy().getScheduler().schedule(this, () -> ScheduledTaskUtil.processServerPings(instance), 1, 1, TimeUnit.SECONDS);
	}

	private void initializeConfigs() {
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

		// Initialize per server config.
		perServerConfig = LightningBuilder
				.fromFile(new File(getDataFolder().getPath() + File.separator + "servers.yml"))
				.addInputStreamFromResource("servers.yml")
				.setDataType(DataType.SORTED)
				.setReloadSettings(ReloadSettings.MANUALLY)
				.setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
				.createConfig();

		// Update config if needed.
		if (config.getInt("configVersion") != configVersion) {
			boolean updateResult = ConfigUpdateHelper.updateConfig(config, this);
			if (!updateResult) return;
			config.forceReload();
		}

		// Update messages if needed.
		if (messages.getInt("messagesVersion") != messagesVersion) {
			boolean msgUpdateResult = MessagesUpdateHelper.updateMessages(messages, this);
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

	public Config getPerServerConfig() {
		return this.perServerConfig;
	}

}
