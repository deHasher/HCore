package ru.dehasher.bukkit.managers;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.HashMap;
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

import org.jetbrains.annotations.Nullable;
import ru.dehasher.bukkit.HCore;

@SuppressWarnings("deprecation")
public class Methods {

    // Красим сообщения.
    public static String colorSet(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    // Удаляем все цветовые коды из сообщения.
    public static String colorClear(String message) {
        return ChatColor.stripColor(colorSet(message));
    }

    // Заменяем плейсхолдеры.
    public static String replacePlaceholders(Player player, String message) {
        if (Methods.checkPlugin(Plugins.PlaceholderAPI)) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        return message;
    }

    public static void setHealth(Player player) {
        if (HCore.config.getBoolean("other-params.custom-health.enabled")) {
            int health = HCore.config.getInt("other-params.custom-health.health");
            player.setMaxHealth(health);
            player.setHealth(health);
        }
    }

    // Проверка игрока на наличие прав.
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
                    info.getDouble("x"),
                    info.getDouble("y"),
                    info.getDouble("z"),
                    (float)info.getDouble("yaw"),
                    (float)info.getDouble("pitch")
            );
        }
        return null;
    }

    public static double getConfigVersion(String config) {
        switch (config) {
            case "main":   return HCore.main_version;
            case "lang":   return HCore.lang_version;
            case "config": return HCore.config_version;
            default:       return 0.0;
        }
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
        string = string
                .replaceAll(Pattern.quote("www."), "")
                .replaceAll(Pattern.quote("http://"), "")
                .replaceAll(Pattern.quote("https://"), "");

        for (String url : HCore.config.getStringList("fix-advertisement.whitelist")) {
            if (string.contains(url)) return false;
        }

        Pattern pattern = Pattern.compile(HCore.config.getString("fix-advertisement.regex"));
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }

    // Исправляем слэши в зависимости от ОС.
    public static String fixSlashes(String input) {
        return input.replace("/", Matcher.quoteReplacement(File.separator));
    }

    // Проверяем существование плагина.
    public static boolean checkPlugin(String plugin) {
        return Bukkit.getPluginManager().getPlugin(plugin) != null;
    }

    // Шифруем строку в url.
    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (Exception ex) {
            return input;
        }
    }

    // Отправляем команду в консоль.
    public static void sendConsole(String command) {
        Bukkit.getScheduler().runTask(HCore.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    // Проверяем местоположение на запрещенку.
    public static boolean invalidLocation(Location loc) {
        double Y = loc.getY();
        return Y < -1000 || Y > 1000;
    }

    // Проверяем строку на кириллицу.
    public static boolean isCyrillic(String input) {
        return input.matches(".*\\p{InCyrillic}.*");
    }

    // Генерируем нормальный url.
    public static String httpBuildQuery(String link, @Nullable HashMap<String, String> params) {
        if (params != null) {
            String data = params.entrySet().stream()
                    .map(p -> Methods.urlEncode(p.getKey()) + "=" + Methods.urlEncode((p.getValue())))
                    .reduce((p1, p2) -> p1 + "&" + p2).orElse("");
            link = link + "?" + data;
        }
        return link;
    }
}