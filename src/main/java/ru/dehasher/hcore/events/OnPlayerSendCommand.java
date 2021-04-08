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
		Player player  = e.getPlayer();
		String command = e.getMessage();

		// Проверяем команду на рекламу.
		if (HCore.config.getBoolean("fix-advertisement.checks.commands")) {
			if (Methods.isAdv(command)) {
				if (!Methods.isPerm(player, "hcore.bypass.advertisement")) {
					e.setCancelled(true);
					Informer.send(player, HCore.lang.getString("errors.advertisement.commands"));
					return false;
				}
			}
		}

		// Блокируем ему команды через двоеточие.
		if (HCore.config.getBoolean("send-command.disable-colon")) {
	        if (command.split(" ")[0].contains(":")) {
	        	if (!Methods.isPerm(player, "hcore.bypass.commands.colon")) {
					e.setCancelled(true);
					Informer.send(player, HCore.lang.getString("errors.blocked-colon-commands"));
					return false;
				}
	        }
		}

		// Блокируем абсолютно все команды, кроме разрешённых.
        if (HCore.config.getBoolean("send-command.disable-commands")) {
			if (Methods.isPerm(player, "hcore.bypass.commands.all")) return true;

			for (String cmd : HCore.config.getStringList("send-command.whitelist")) {
				if (cmd.equals(command.toLowerCase())) return true;
			}

			Informer.send(player, HCore.lang.getString("errors.commands-disabled"));
			e.setCancelled(true);
		}
        return true;
    }
}
