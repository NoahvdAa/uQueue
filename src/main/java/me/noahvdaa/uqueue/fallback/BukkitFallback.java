package me.noahvdaa.uqueue.fallback;

import org.bukkit.plugin.java.JavaPlugin;

public class BukkitFallback extends JavaPlugin {

	@Override
	public void onEnable() {
		for (int i = 0; i < 15; i++) {
			getLogger().warning("uQueue is not a Bukkit/Spigot plugin! You must install it on your BungeeCord proxy!");
		}
		getServer().getPluginManager().disablePlugin(this);
	}

}
