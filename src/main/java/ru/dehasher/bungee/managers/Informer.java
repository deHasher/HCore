package ru.dehasher.bungee.managers;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.Nullable;

import java.awt.List;

@SuppressWarnings("deprecation")
public class Informer {

    private static void request(ProxiedPlayer player, String message) {
        if (message == null || message.equals("") || message.isEmpty()) message = ":P";

        message = Methods.colorSet(message);

        if (player == null) {
            System.out.println(message);
        } else {
            player.sendMessage(message);
        }
    }

    @Nullable
    public static void send(ProxiedPlayer player, String message) {
        request(player, message + "");
    }

    @Nullable
    public static void send(ProxiedPlayer player, int message) {
        request(player, message + "");
    }

    @Nullable
    public static void send(ProxiedPlayer player, Double message) {
        request(player, message + "");
    }

    @Nullable
    public static void send(ProxiedPlayer player, Float message) {
        request(player, message + "");
    }

    @Nullable
    public static void send(ProxiedPlayer player, List message) {
        request(player, message.toString() + "");
    }

    @Nullable
    public static void send(ProxiedPlayer player, Boolean message) {
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
}