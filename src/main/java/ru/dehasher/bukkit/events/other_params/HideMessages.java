package ru.dehasher.bukkit.events.other_params;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.dehasher.bukkit.HCore;

@SuppressWarnings("deprecation")
public class HideMessages implements Listener {

    public HideMessages(HCore plugin) {}

    // Убрать сообщения о входах на сервер.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        if (!HCore.config.getBoolean("other-params.hide-messages.join")) return;
        e.setJoinMessage(null);
    }

    // Убрать сообщения о выходах с сервера.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        if (!HCore.config.getBoolean("other-params.hide-messages.leave")) return;
        e.setQuitMessage(null);
    }

    // Убрать сообщения о смертях.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        if (!HCore.config.getBoolean("other-params.hide-messages.death")) return;
        e.setDeathMessage(null);
    }

    // Убрать сообщения об ачивках.
    public static void disableAchievements() {
        if (!HCore.config.getBoolean("other-params.hide-messages.advancements")) return;

        for (World world : HCore.getPlugin().getServer().getWorlds()) {
            if (Double.parseDouble(Bukkit.getBukkitVersion().substring(0, 4)) > 1.12) {
                world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            } else {
                world.setGameRuleValue("announceAdvancements", "false");
            }
        }
    }
}