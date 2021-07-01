package ru.dehasher.bukkit.api.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PAPI {
    public static String setPlaceholders(Player player, String input) {
        return PlaceholderAPI.setPlaceholders(player, input);
    }
}
