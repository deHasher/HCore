package ru.dehasher.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.api.guilds.GAPI;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;

public class OnPlayerSendCommand implements Listener {

    public OnPlayerSendCommand(HCore plugin) {}

    // Отслеживаем какие команды вводит игрок.
    @EventHandler(priority = EventPriority.HIGHEST)
    public boolean onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
        Player player  = e.getPlayer();
        String command = e.getMessage();

        // Разрешаем отправку команд в белом списке.
        for (String cmd : HCore.config.getStringList("send-command.whitelist")) {
            if (command.startsWith(cmd.toLowerCase())) return true;
        }

        // Исправление гильдий.
        if (HCore.config.getBoolean("guilds-fix.enabled") && Methods.checkPlugin("Guilds")) {
            for (String guild : HCore.config.getString("guilds-fix.commands").split("\\|")) {
                if (command.substring(1).equals(guild)) {
                    e.setCancelled(true);
                    String playerRole = Methods.colorClear(GAPI.getRole(player)).toLowerCase();
                    Informer.send(player, HCore.config.getString("guilds-fix.prefix"));
                    for (String role : HCore.config.getConfigurationSection("guilds-fix.roles").getKeys(false)) {
                        Informer.send(player, HCore.config.getString("guilds-fix.roles." + role));
                        if (role.equals(playerRole)) return false;
                    }
                }
            }
        }

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
            Informer.send(player, HCore.lang.getString("errors.commands-disabled"));
            e.setCancelled(true);
            return false;
        }
        return true;
    }
}
