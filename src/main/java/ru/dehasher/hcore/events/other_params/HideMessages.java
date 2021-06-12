package ru.dehasher.hcore.events.other_params;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Informer;
import ru.dehasher.hcore.managers.Methods;

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
            if (Methods.getServerVersion() > 12) {
                world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            } else {
                world.setGameRuleValue("announceAdvancements", "false");
            }
        }
    }
}