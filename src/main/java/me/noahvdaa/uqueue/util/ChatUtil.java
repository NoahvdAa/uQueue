package me.noahvdaa.uqueue.util;

import me.noahvdaa.uqueue.uQueue;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatUtil {

	public static String colorize(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}

	public static BaseComponent colorizeIntoComponent(String input) {
		return new TextComponent(colorize(input));
	}

	public static BaseComponent colorizeIntoPrefixedComponent(uQueue plugin, String input) {
		return colorizeIntoComponent(plugin.getConfig().getString("Chat.Prefix") + " " + input);
	}

}
