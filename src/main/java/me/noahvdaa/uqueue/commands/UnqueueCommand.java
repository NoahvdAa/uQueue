package me.noahvdaa.uqueue.commands;

import me.noahvdaa.uqueue.UQueue;
import me.noahvdaa.uqueue.util.ChatUtil;
import me.noahvdaa.uqueue.util.QueueUtil;
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
			sender.sendMessage(ChatUtil.colorizeIntoPrefixedComponent(plugin, "&cThis command can only be used by players."));
			return;
		}
		ProxiedPlayer p = (ProxiedPlayer) sender;

		if (!plugin.queuedFor.containsKey(p.getUniqueId())) {
			sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Unqueue.NotQueued"));
			return;
		}

		String queuedFor = plugin.queuedFor.get(p.getUniqueId());

		QueueUtil.removeFromQueue(plugin, p.getUniqueId());
		sender.sendMessage(ChatUtil.getConfigPlaceholderMessageAsComponent(plugin, "Commands.Queue.LeftQueueFor", queuedFor));
		// Clear queue position message.
		p.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
	}

}
