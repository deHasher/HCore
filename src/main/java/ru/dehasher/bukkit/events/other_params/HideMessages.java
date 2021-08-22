package ru.dehasher.bukkit.events.other_params;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.dehasher.bukkit.HCore;

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
}