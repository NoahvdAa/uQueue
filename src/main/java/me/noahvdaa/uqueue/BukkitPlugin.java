package me.noahvdaa.uqueue;

import me.noahvdaa.uqueue.util.PlaceholderImplementation;
import me.noahvdaa.uqueue.util.PluginMessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.HashMap;
import java.util.UUID;

public class BukkitPlugin extends JavaPlugin implements Listener, PluginMessageListener {

	public HashMap<UUID, PluginMessageUtil.PluginMessage> statuses;

	@Override
	public void onEnable() {
		statuses = new HashMap<>();

		getLogger().info("The Bukkit/Spigot version of uQueue exists only for PlaceHolderAPI support!");
		getLogger().info("It will only work if you also install the plugin on your BungeeCord proxy!");

		if(getServer().getPluginManager().getPlugin("PlaceholderAPI") == null){
			getLogger().warning("You don't have PlaceHolderAPI installed! The placeholders will not work!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		// Register expansion.
		new PlaceholderImplementation(this).register();

		// Register events for cleanup.
		getServer().getPluginManager().registerEvents(this, this);

		// Register plugin channel.
		getServer().getMessenger().registerIncomingPluginChannel(this, "uqueue:queueupdate", this);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
		if (!channel.equals("uqueue:queueupdate")) return;
		PluginMessageUtil.PluginMessage message = PluginMessageUtil.parseBytes(bytes);
		statuses.put(player.getUniqueId(), message);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		statuses.remove(e.getPlayer().getUniqueId());
	}

}
