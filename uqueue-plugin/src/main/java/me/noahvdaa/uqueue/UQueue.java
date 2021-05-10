package me.noahvdaa.uqueue;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.internal.settings.ReloadSettings;
import me.noahvdaa.uqueue.api.UQueuePlugin;
import me.noahvdaa.uqueue.api.util.QueueablePlayer;
import me.noahvdaa.uqueue.api.util.QueueableServer;
import me.noahvdaa.uqueue.commands.QueueCommand;
import me.noahvdaa.uqueue.commands.UQueueCommand;
import me.noahvdaa.uqueue.commands.UnqueueCommand;
import me.noahvdaa.uqueue.config.ConfigUpdateHelper;
import me.noahvdaa.uqueue.config.ConfigValidationHelper;
import me.noahvdaa.uqueue.config.messages.MessagesUpdateHelper;
import me.noahvdaa.uqueue.impl.UQueueablePlayer;
import me.noahvdaa.uqueue.impl.UQueueableServer;
import me.noahvdaa.uqueue.listener.PlayerListener;
import me.noahvdaa.uqueue.util.PermissionUtil;
import me.noahvdaa.uqueue.util.ScheduledTaskUtil;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import org.bstats.charts.SimplePie;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UQueue extends Plugin implements UQueuePlugin {

	private static UQueue instance;
	private Config config;
	private Config messages;
	private Config perServerConfig;

	public static final int configVersion = 1;
	public static final int messagesVersion = 1;

	public HashMap<String, QueueableServer> queueableServers;
	public HashMap<UUID, QueueablePlayer> queueablePlayers;
	public List<String> disabledServers;

	@Override
	public void onEnable() {
		// Store instance for singleton.
		instance = this;

		queueableServers = new HashMap<>();
		queueablePlayers = new HashMap<>();
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
		getProxy().getScheduler().schedule(this, () -> ScheduledTaskUtil.processQueue(instance), 1, 1, TimeUnit.SECONDS);

		// Process plugin messages for placeholders.
		getProxy().getScheduler().schedule(this, () -> ScheduledTaskUtil.processPluginMessages(instance), 1, 1, TimeUnit.SECONDS);

		// Ping servers to check if they're up.
		getProxy().getScheduler().schedule(this, () -> ScheduledTaskUtil.processServerPings(instance), 1, 1, TimeUnit.SECONDS);

		// Register bStats metrics.
		Metrics bStats = new Metrics(this, 11230);
		bStats.addCustomChart(new SimplePie("permission_system", () -> PermissionUtil.getPermissionProvider()));
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

	@Override
	public QueueablePlayer getPlayer(ProxiedPlayer player) {
		UUID uuid = player.getUniqueId();
		if (queueablePlayers.containsKey(uuid))
			return queueablePlayers.get(uuid);
		QueueablePlayer newPlayer = new UQueueablePlayer(player);
		queueablePlayers.put(uuid, newPlayer);
		return newPlayer;
	}

	public void removePlayer(ProxiedPlayer player) {
		UUID uuid = player.getUniqueId();
		queueablePlayers.remove(uuid);
	}

	@Override
	public QueueableServer getServer(ServerInfo info) {
		String name = info.getName();
		if (queueableServers.containsKey(name))
			return queueableServers.get(name);
		QueueableServer newServer = new UQueueableServer(name);
		queueableServers.put(name, newServer);
		return newServer;
	}
}
