package ru.dehasher.bukkit.managers;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.io.IOException;
import java.net.URLEncoder;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Nullable;
import ru.dehasher.bukkit.HCore;

public class Informer {

    private static void request(Player player, String message) {
        ConfigurationSection placeholders = HCore.lang.getConfigurationSection("placeholders");
        if (placeholders != null) {
            for (String placeholder : placeholders.getKeys(false)) {
                message = message.replace("{" + placeholder + "}", HCore.lang.getString("placeholders." + placeholder));
            }
        }

        if (message == null || message.equals("") || message.isEmpty()) message = HCore.lang.getString("errors.very-bad-error");

        message = Methods.colorSet(message);

        if (player == null) {
            HCore.getPlugin().getLogger().info(message);
        } else {
            message = Methods.replacePlaceholders(player, message);
            player.sendMessage(message);
        }
    }

    @Nullable
    public static void send(Player player, String message) {
        request(player, message + "");
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
        request(player, message.toString() + "");
    }

    @Nullable
    public static void send(Player player, Boolean message) {
        request(player, message ? "true" : "false");
    }

    @Nullable
    public static void send(String message) {
        request(null, message + "");
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
        request(null, message.toString() + "");
    }

    @Nullable
    public static void send(Boolean message) {
        request(null, message ? "true" : "false");
    }

    @Nullable
    public static void vk(String message) {
        try {
            message = message.replace("{server}", HCore.PlaceholderAPI ? HCore.server_name : "Unknown (without papi)");
            message = "http://api.klaun.ch/vk?msg=" + URLEncoder.encode(message, "UTF-8");
            URL url = new URL(message);
            System.setProperty("http.agent", "Chrome");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            br.close();
        } catch (IOException ignored) {}
    }
}