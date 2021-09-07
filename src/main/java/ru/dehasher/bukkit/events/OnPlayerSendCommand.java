package ru.dehasher.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.managers.*;

public class OnPlayerSendCommand implements Listener {

    public OnPlayerSendCommand(HCore plugin) {}

    // Отслеживаем какие команды вводит игрок.
    @EventHandler(priority = EventPriority.HIGHEST)
    public boolean onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
        Player player  = e.getPlayer();
        String command = e.getMessage().toLowerCase();

        // Разрешаем отправку команд в белом списке.
        for (String cmd : HCore.config.getStringList("send-command.whitelist")) {
            if (command.startsWith(cmd.toLowerCase()) || (!Methods.isCyrillic(cmd) && command.startsWith(Rusificator.replace(cmd.toLowerCase())))) return true;
        }

        // Блокируем ему команды через двоеточие.
        if (HCore.config.getBoolean("send-command.disable-colon")) {
            if (command.split(" ")[0].contains(":")) {
                if (!Methods.isPerm(player, "hcore.bypass.commands.colon")) {
                    Informer.send(player, HCore.lang.getString("errors.blocked-colon-commands"));
                    e.setCancelled(true);
                    return false;
                }
            }
        }

        // Блокируем абсолютно все команды, кроме разрешённых.
        if (HCore.config.getBoolean("send-command.disable-commands")) {
            if (!Methods.isPerm(player, "hcore.bypass.commands.all")) {
                Informer.send(player, HCore.lang.getString("errors.commands-disabled"));
                e.setCancelled(true);
                return false;
            }
        }

        return true;
    }
}
