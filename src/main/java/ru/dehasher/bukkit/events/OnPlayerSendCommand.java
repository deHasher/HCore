package ru.dehasher.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.api.guilds.GAPI;
import ru.dehasher.bukkit.managers.ChatFilter;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;
import ru.dehasher.bukkit.managers.Plugins;

public class OnPlayerSendCommand implements Listener {

    public OnPlayerSendCommand(HCore plugin) {}

    // Отслеживаем какие команды вводит игрок.
    @EventHandler(priority = EventPriority.HIGHEST)
    public boolean onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
        Player player  = e.getPlayer();
        String command = e.getMessage();

        // Проверка на спам.
        if (HCore.config.getBoolean("other-params.block-actions.spam")) {
            if (!Methods.isPerm(player, "hcore.bypass.commands.spam")) {
                if (ChatFilter.isSpam(player, command, true)) {
                    Informer.titles(player, null, HCore.lang.getString("errors.spam"));
                    e.setCancelled(true);
                    return false;
                }
            }
        }

        // Проверяем команду на рекламу.
        if (HCore.config.getBoolean("fix-advertisement.checks.commands")) {
            if (!Methods.isPerm(player, "hcore.bypass.advertisement")) {
                if (Methods.isAdv(command)) {
                    Informer.send(player, HCore.lang.getString("errors.advertisement.commands"));
                    e.setCancelled(true);
                    return false;
                }
            }
        }

        // Разрешаем отправку команд в белом списке.
        for (String cmd : HCore.config.getStringList("send-command.whitelist")) {
            if (command.startsWith(cmd.toLowerCase())) return true;
        }

        // Костыль для гильдий!
        if (Methods.checkPlugin(Plugins.Guilds)) {
            for (String guild : HCore.lang.getString("guild.commands").split("\\|")) {
                if (command.substring(1).equals(guild)) {
                    e.setCancelled(true);
                    String playerRole = Methods.colorClear(GAPI.getRole(player)).toLowerCase();
                    Informer.send(player, HCore.lang.getString("guild.prefix"));
                    for (String role : HCore.lang.getConfigurationSection("guild.roles").getKeys(false)) {
                        Informer.send(player, HCore.lang.getString("guild.roles." + role));
                        if (role.equals(playerRole)) return true;
                    }
                }
            }
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
