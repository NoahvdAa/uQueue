package me.noahvdaa.uqueue.util;

import me.noahvdaa.uqueue.UQueue;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatUtil {

	// TODO: These method names suck.

	public static String colorize(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}

	public static BaseComponent colorizeIntoComponent(String input) {
		return new TextComponent(colorize(input));
	}

	public static BaseComponent colorizeIntoPrefixedComponent(UQueue plugin, String input) {
		return colorizeIntoComponent(plugin.getConfig().getString("Chat.Prefix") + " " + input);
	}

	public static BaseComponent getConfigPlaceholderMessageWithoutPrefixAsComponent(UQueue plugin, String message, String... args) {
		String msg = plugin.getMessages().getString(message);
		int i = 0;
		for (String arg : args) {
			i++;
			msg = msg.replaceAll("%" + i + "%", arg);
		}
		return colorizeIntoComponent(msg);
	}

	public static BaseComponent getConfigPlaceholderMessageAsComponent(UQueue plugin, String message, String... args) {
		String msg = plugin.getMessages().getString(message);
		int i = 0;
		for (String arg : args) {
			i++;
			msg = msg.replaceAll("%" + i + "%", arg);
		}
		return colorizeIntoComponent(plugin.getConfig().getString("Chat.Prefix") + " " + msg);
	}

}
