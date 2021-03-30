package ru.dehasher.hcore.managers;

import java.awt.List;

import org.bukkit.entity.Player;

import ru.dehasher.hcore.HCore;

public class Informer {

	private static void send(Player player, String message) {
		for (String placeholder : HCore.lang.getConfigurationSection("messages.placeholders").getKeys(true)) {
			message = message.replace("{" + placeholder + "}", HCore.lang.getString("messages.placeholders." + placeholder));
		}

		if (message == null) message = HCore.lang.getString("messages.errors.very-bad-error");

		if (message == "") return;

		message = Methods.color(message);

		player.sendMessage(message);
	}

	private static void send(String message) {
		for (String placeholder : HCore.lang.getConfigurationSection("messages.placeholders").getKeys(true)) {
			message = message.replace("{" + placeholder + "}", HCore.lang.getString("messages.placeholders." + placeholder));
		}

		if (message == null) message = HCore.lang.getString("messages.errors.very-bad-error");

		if (message == "") return;

		message = Methods.color(message);

		HCore.getPlugin().getLogger().info(message);
	}
















	public static void CONSOLE(String message) {
		send(message);
	}

	public static void CONSOLE(int message) {
		send("" + message);
	}

	public static void CONSOLE(Double message) {
		send("" + message);
	}

	public static void CONSOLE(Float message) {
		send("" + message);
	}

	public static void CONSOLE(List message) {
		send(message.toString());
	}

	public static void CONSOLE(Boolean message) {
		send(message ? "true" : "false");
	}

	public static void PLAYER(Player player, String message) {
		send(player, message);
	}

	public static void PLAYER(Player player, int message) {
		send(player, message + "");
	}

	public static void PLAYER(Player player, Double message) {
		send(player, message + "");
	}

	public static void PLAYER(Player player, Float message) {
		send(player, message + "");
	}

	public static void PLAYER(Player player, List message) {
		send(player, message.toString());
	}

	public static void PLAYER(Player player, Boolean message) {
		send(player, message ? "true" : "false");
	}
}