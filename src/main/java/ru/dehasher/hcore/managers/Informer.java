package ru.dehasher.hcore.managers;

import java.awt.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;

import ru.dehasher.hcore.HCore;

public class Informer {

	private static void request(Player player, String message) {
		ConfigurationSection placeholders = HCore.lang.getConfigurationSection("placeholders");
		if (placeholders != null) {
			for (String placeholder : placeholders.getKeys(false)) {
				message = message.replace("{" + placeholder + "}", HCore.lang.getString("placeholders." + placeholder));
			}
		}

		if (message == null || message.equals("")) message = HCore.lang.getString("errors.very-bad-error");

		message = Methods.color(message);

		if (player == null) {
			HCore.getPlugin().getLogger().info(message);
		} else {
			message = Methods.replacePlaceholders(player, message);
			player.sendMessage(message);
		}
	}

	@Nullable
	public static void send(Player player, String message) {
		request(player, message);
	}

	@Nullable
	public static void send(Player player, int message) {
		request(player, message + "");
	}

	@Nullable
	public static void send(Player player, Double message) {
		request(player, message + "");
	}

	@Nullable
	public static void send(Player player, Float message) {
		request(player, message + "");
	}

	@Nullable
	public static void send(Player player, List message) {
		request(player, message.toString());
	}

	@Nullable
	public static void send(Player player, Boolean message) {
		request(player, message ? "true" : "false");
	}

	@Nullable
	public static void send(String message) {
		request(null, message);
	}

	@Nullable
	public static void send(int message) {
		request(null, message + "");
	}

	@Nullable
	public static void send(Double message) {
		request(null, message + "");
	}

	@Nullable
	public static void send(Float message) {
		request(null, message + "");
	}

	@Nullable
	public static void send(List message) {
		request(null, message.toString());
	}

	@Nullable
	public static void send(Boolean message) {
		request(null, message ? "true" : "false");
	}
}