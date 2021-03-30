package ru.dehasher.hcore.events.other_events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import ru.dehasher.hcore.HCore;

@SuppressWarnings("deprecation")
public class HideMessages implements Listener {

    public HideMessages(HCore plugin) {}

    // Убрать сообщени¤ о входах на сервер.
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.hide-messages.join")) return;
    	e.setJoinMessage(null);
    }

	// Убрать сообщени¤ о выходах с сервера.
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerQuitEvent(PlayerQuitEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.hide-messages.leave")) return;
    	e.setQuitMessage(null);
    }

    // Убрать сообщени¤ о киках.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKickEvent(PlayerKickEvent e) {
    	if (!HCore.config.getBoolean("settings.global-params.disable-message-on-leave")) return;
    	e.setLeaveMessage(null);
    }

    // Убрать сообщени¤ о смерт¤х.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
    	if (!HCore.config.getBoolean("settings.global-params.disable-message-on-death")) return;
    	e.setDeathMessage(null);
    }

	// Убрать сообщени¤ об ачивках.
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldLoad(WorldLoadEvent e) {
		if (!HCore.config.getBoolean("settings.global-params.disable-message-on-advancements")) return;
		e.getWorld().setGameRuleValue("announceAdvancements", "false");
	}
}
