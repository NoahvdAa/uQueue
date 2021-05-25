package me.noahvdaa.uqueue.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.noahvdaa.uqueue.BukkitPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderImplementation extends PlaceholderExpansion {

	private final BukkitPlugin plugin;

	public PlaceholderImplementation(BukkitPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public @NotNull String getAuthor() {
		return "NoahvdAa";
	}

	@Override
	public @NotNull String getIdentifier() {
		return "uqueue";
	}

	@Override
	public @NotNull String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player player, @NotNull String identifier) {
		if (player == null || !plugin.statuses.containsKey(player.getUniqueId()))
			return "";

		PluginMessageUtil.PluginMessage message = plugin.statuses.get(player.getUniqueId());

		switch (identifier) {
			case "is_queued":
				return message.queued ? "true" : "false";
			case "queue_position":
				return Integer.toString(message.queuePosition);
			case "queue_length":
				return Integer.toString(message.queueLength);
			case "queued_for":
				return message.server;
			case "queued_for_display_name":
				return message.serverDisplayName;
			default:
				return "unknown placeholder";
		}

	}

}
