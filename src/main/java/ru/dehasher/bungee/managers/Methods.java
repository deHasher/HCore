package ru.dehasher.bungee.managers;

import net.md_5.bungee.api.ChatColor;

public class Methods {

    // Красим сообщения.
    public static String colorSet(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    // Удаляем все цветовые коды из сообщения.
    public static String colorClear(String message) {
        return ChatColor.stripColor(colorSet(message));
    }
}
