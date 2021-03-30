package ru.dehasher.hcore.managers;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ru.dehasher.hcore.HCore;

@SuppressWarnings("deprecation")
public class Methods {
	// Красим сообщения.
	public static String color(String message) {
		message = ChatColor.translateAlternateColorCodes('&', message);
		return message;
	}

	// set необходим чтобы устанавливать челам сразу фул хп если они возродились или ток зашли на сервер.
	public static void editHealth(Player player, boolean set) {
		String  path   = "settings.join-server.custom-health.";
		boolean noperm = true;

		if (!HCore.config.getBoolean(path + "enabled")) return;

		// Перебор по всем правам игрока.
		for (String group : HCore.config.getConfigurationSection(path + "groups").getKeys(false)) {
			// Если у игрока есть право на кастомные хпшки.
			if (player.hasPermission(HCore.config.getString(path + "permission") + group.toLowerCase())) {

				// Устанавливаем ему хп сколько положено.
				player.setMaxHealth(HCore.config.getInt(path + "groups." + group));

				// Следует ли установить максимум хп игроку.
				if (set) {
					player.setHealth(HCore.config.getInt(path + "groups." + group));
				}

				noperm = false;
				break;
			}
		}

		// Тоже самое, но если у игрока не найдено пермов.
		if (noperm) {
			player.setMaxHealth(HCore.config.getInt(path + "default"));

			// Следует ли установить максимум хп игроку.
			if (set) {
				player.setHealth(HCore.config.getInt(path + "default"));
			}
		}
	}

	// Неявная проверка игрока на наличие прав.
	public static boolean isPerm(Player player) {
		if (player.isOp()) return true; // Проверка на опку.
		if (player.hasPermission("*")) return true; // Проверка на звёздочку.
		return isAdmin(player) || isAuthor(player); // Проверка на админку.
	}

	// Проверка на админку.
	public static boolean isAdmin(Player player) {
		for (String user : HCore.main.getStringList("admins.nicknames")) {
			if (player.getName().equals(user)) return true;
		}
		return false;
	}

	// Проверка на автора.
	public static boolean isAuthor(Player player) {
		for (String user : HCore.getPlugin().getDescription().getAuthors()) {
			if (user.equals(player.getName())) return true;
		}

		return false;
	}

	// Подсчёт байт в предмете.
	public static int checkBytes(ItemStack item) {
		int length = 0;

		if (item == null)        return 0;
		if (!item.hasItemMeta()) return 0;

		ItemMeta meta = item.getItemMeta();

		if (meta != null) {
			length = meta.toString().getBytes(StandardCharsets.UTF_8).length;
		}

		return length;
	}

	// Проверка строчки на рекламу.
	public static boolean isAdv(String string) {
		if (!HCore.config.getBoolean("settings.fix-advertisement.enabled")) return false;
		string = string.replaceAll(Pattern.quote("www."), "").replaceAll(Pattern.quote("http://"), "").replaceAll(Pattern.quote("https://"), "");

		for (String url : HCore.config.getStringList("settings.fix-advertisement.whitelist")) {
			if (string.contains(url)) return false;
		}

	    Pattern pattern = Pattern.compile(HCore.config.getString("settings.fix-advertisement.regex"));
	    Matcher matcher = pattern.matcher(string);
		return matcher.find();
	}
}