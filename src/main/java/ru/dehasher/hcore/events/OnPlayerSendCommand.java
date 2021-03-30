package ru.dehasher.hcore.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Informer;
import ru.dehasher.hcore.managers.Methods;

public class OnPlayerSendCommand implements Listener {
	public OnPlayerSendCommand(HCore plugin) {}

    // Отслеживаем какие команды вводит игрок.
	@EventHandler(priority = EventPriority.HIGHEST)
    public boolean onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();

		// Проверяем команду на рекламу.
		if (HCore.config.getBoolean("settings.fix-advertisement.checks.commands")) {
			if (Methods.isAdv(e.getMessage()) && !Methods.isPerm(player)) {
				if (!player.hasPermission(HCore.config.getString("settings.fix-advertisement.permission"))) {
					e.setCancelled(true);
					Informer.PLAYER(player, HCore.lang.getString("messages.errors.advertisement.commands"));
					return false;
				}
			}
		}

		// Блокируем ему команды через двоеточие.
		if (HCore.config.getBoolean("settings.send-command.disable-colon")) {
	        if (e.getMessage().split(" ")[0].contains(":")) {
	            e.setCancelled(true);
	            Informer.PLAYER(player, HCore.lang.getString("messages.errors.blocked-colon-commands"));
        		return false;
	        }
		}

		// Блокируем абсолютно все команды кроме разрешённых
        if (HCore.config.getBoolean("settings.send-command.disable-commands")) {
        	if (!HCore.config.getStringList("settings.send-command.whitelist").contains(e.getMessage().toLowerCase())) {
        		e.setCancelled(true);
        		Informer.PLAYER(player, HCore.lang.getString("messages.errors.commands-disabled"));
        		return false;
        	}
        }
        return true;
    }
}
