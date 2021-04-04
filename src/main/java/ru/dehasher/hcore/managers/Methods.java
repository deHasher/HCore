package ru.dehasher.hcore.managers;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ru.dehasher.hcore.HCore;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class Methods {
	// Красим сообщения.
	public static String color(String message) {
		message = ChatColor.translateAlternateColorCodes('&', message);
		return message;
	}

	// Заменяем плейсхолдеры.
	public static String replacePlaceholders(Player player, String message) {
		if (HCore.PlaceholderAPI) {
			message = PlaceholderAPI.setPlaceholders(player, message);
		}
		return message;
	}

	// set необходим чтобы устанавливать челам сразу фул хп если они возродились или ток зашли на сервер.
	public static void editHealth(Player player, boolean set) {
		String  path   = "join-server.custom-health.";
		boolean noperm = true;

		if (!HCore.config.getBoolean(path + "enabled")) return;

		// Перебор по всем правам игрока.
		for (String group : HCore.config.getConfigurationSection(path + "groups").getKeys(false)) {
			// Если у игрока есть право на кастомные хпшки.
			if (player.hasPermission("hcore.health.group." + group.toLowerCase())) {

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
	public static boolean isPerm(@Nullable Player player, @Nullable String permission) {
		if (player == null) return true;
		if (permission != null) {
			if (player.hasPermission(permission)) return true;
		}
		return isAdmin(player) || isAuthor(player);
	}

	// Обработка информации о команде.
	public static String getCommandInfo(String command) {
		ConfigurationSection info = HCore.lang.getConfigurationSection("commands." + command + ".info");
		String format = HCore.config.getString("send-command.plugin.format");

		format = format.replace("{command}", command);
		format = format.replace("{arguments}", info.getString("arguments"));
		format = format.replace("{description}", info.getString("description"));

		return format;
	}

	// Округление double числа.
	public static String round(Double number, int symbols) {
		if (number == null) return "0.0";
		StringBuilder format = new StringBuilder();
		for (int x = 0 ; x < symbols ; x++) format.append("#");
		DecimalFormat df = new DecimalFormat("#." + format);
		return df.format(number).replace(",", ".");
	}

	// Получение точки спавна.
	public static Location getSpawnLocation(String spawn) {
		ConfigurationSection info = HCore.spawn.getConfigurationSection(spawn);
		if (info != null) {
			return new Location(
					Bukkit.getWorld(info.getString("world")),
					(float)info.getDouble("x"),
					(float)info.getDouble("y"),
					(float)info.getDouble("z"),
					(float)info.getDouble("yaw"),
					(float)info.getDouble("pitch")
			);
		}
		return null;
	}

	// Телепортация игрока.
	public static void teleportPlayer(Player player, @Nullable Location loc) {
		if (loc == null) return;
		player.teleport(loc);
	}

	// Проверка на админку.
	private static boolean isAdmin(Player player) {
		for (String user : HCore.main.getStringList("admins.nicknames")) {
			if (user.equals(player.getName())) return true;
		}
		return false;
	}

	// Проверка на автора.
	private static boolean isAuthor(Player player) {
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
		if (!HCore.config.getBoolean("fix-advertisement.enabled")) return false;
		string = string.replaceAll(Pattern.quote("www."), "").replaceAll(Pattern.quote("http://"), "").replaceAll(Pattern.quote("https://"), "");

		for (String url : HCore.config.getStringList("fix-advertisement.whitelist")) {
			if (string.contains(url)) return false;
		}

	    Pattern pattern = Pattern.compile(HCore.config.getString("fix-advertisement.regex"));
	    Matcher matcher = pattern.matcher(string);
		return matcher.find();
	}

	public static String fixSlashes(String text) {
		text = text.replace("/", File.separator);
		return text;
	}
}