package me.noahvdaa.uqueue.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.noahvdaa.uqueue.BukkitPlugin;
import me.noahvdaa.uqueue.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitQueueCommand implements CommandExecutor {

	private final BukkitPlugin plugin;

	public BukkitQueueCommand(BukkitPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatUtil.colorize("&cThis command can only be used by players."));
			return true;
		}
		Player p = (Player) sender;

		if (args.length != 1) {
			sender.sendMessage(ChatUtil.colorize("&cUsage: /queue <server>"));
			return true;
		}

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(p.getName());
		out.writeUTF(args[0].toLowerCase());

		p.sendPluginMessage(plugin, "uqueue:queueplayer", out.toByteArray());
		return true;
	}
}
