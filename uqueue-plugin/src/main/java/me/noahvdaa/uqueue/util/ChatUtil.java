package me.noahvdaa.uqueue.util;

import me.noahvdaa.uqueue.UQueue;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatUtil {

	public static String colorize(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}

	public static BaseComponent colorizeAsComponent(String input) {
		return new TextComponent(colorize(input));
	}

	public static BaseComponent colorizeAsPrefixedComponent(UQueue plugin, String input) {
		return colorizeAsComponent(plugin.getConfig().getString("Chat.Prefix") + " " + input);
	}

	public static BaseComponent getConfigMessageAsComponent(UQueue plugin, String message, String... args) {
		String msg = plugin.getMessages().getString(message);
		int i = 0;
		for (String arg : args) {
			i++;
			msg = msg.replaceAll("%" + i + "%", arg);
		}
		return colorizeAsComponent(msg);
	}

	public static BaseComponent getConfigPlaceholderMessageAsComponent(UQueue plugin, String message, String... args) {
		String msg = plugin.getMessages().getString(message);
		int i = 0;
		for (String arg : args) {
			i++;
			msg = msg.replaceAll("%" + i + "%", arg);
		}
		return colorizeAsComponent(plugin.getConfig().getString("Chat.Prefix") + " " + msg);
	}

}
