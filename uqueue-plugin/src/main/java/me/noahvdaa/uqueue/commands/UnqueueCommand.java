package me.noahvdaa.uqueue.commands;

import me.noahvdaa.uqueue.UQueue;
import me.noahvdaa.uqueue.api.util.QueueablePlayer;
import me.noahvdaa.uqueue.util.ChatUtil;
import me.noahvdaa.uqueue.util.PerServerConfigUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class UnqueueCommand extends Command {

	private final UQueue plugin;

	public UnqueueCommand(UQueue plugin) {
		super("unqueue", "", "leavequeue");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(ChatUtil.colorizeAsPrefixedComponent(plugin, "&cThis command can only be used by players."));
			return;
		}
		ProxiedPlayer p = (ProxiedPlayer) sender;
		QueueablePlayer queueablePlayer = plugin.getPlayer(p);

		if (!queueablePlayer.isQueued()) {
			sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Unqueue.NotQueued"));
			return;
		}

		String queuedFor = queueablePlayer.getQueuedServer().getName();

		queueablePlayer.getQueuedServer().removeFromQueue(queueablePlayer);
		sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.LeftQueueFor", PerServerConfigUtil.getServerDisplayName(plugin, queuedFor)));
		// Clear queue position message.
		p.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
	}

}