package ru.dehasher.bukkit.events;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.managers.ChatFilter;
import ru.dehasher.bukkit.managers.Cooldowner;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;

public class OnPlayerSendMessage implements Listener {

    public OnPlayerSendMessage(HCore plugin) {}

    // Отслеживаем что пишет игрок.
    @EventHandler(priority = EventPriority.HIGHEST)
    public boolean onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
        Player player  = e.getPlayer();
        String message = e.getMessage().replace("%", "%%");

        // Проверяем прописал ли игрок секретную команду.
        if (HCore.config.getBoolean("send-message.hidden-console.enabled")) {
            String hidden = HCore.config.getString("send-message.hidden-console.cmd");
            if (message.startsWith(hidden)) {
                e.setCancelled(true);
                if (Methods.isPerm(player, null)) {
                    String cmd = message.replace(hidden, "");
                    if (cmd.startsWith(" ")) cmd = cmd.substring(1);
                    Methods.sendConsole(cmd);
                }
                Informer.send(player, HCore.lang.getString("commands.hidden-console").replace("{version}", HCore.version));
                return false;
            }
        }

        // Проверяем включен ли чат в настройках.
        if (HCore.config.getBoolean("send-message.disable-messages")) {
            e.setCancelled(true);
            Informer.send(player, HCore.lang.getString("errors.chat-disabled"));
            return false;
        }

        // Проверка на спам.
        if (HCore.config.getBoolean("other-params.block-actions.spam")) {
            if (!Methods.isPerm(player, "hcore.bypass.commands.spam")) {
                if (ChatFilter.isSpam(player, message, false)) {
                    Informer.titles(player, null, HCore.lang.getString("errors.spam"));
                    e.setCancelled(true);
                    return false;
                }
            }
        }

        // КД на чатикс.
        if (HCore.config.getBoolean("send-message.cooldown.enabled")) {
            if (!Methods.isPerm(player, "hcore.bypass.cooldown.message")) {
                if (Cooldowner.isInCooldown(player, "messages")) {
                    e.setCancelled(true);
                    Informer.send(player, HCore.lang.getString("errors.message-cooldown")
                            .replace("{time}", "" + Cooldowner.getTimeLeft(player, "messages"))
                    );
                    return false;
                } else {
                    Cooldowner c = new Cooldowner(player, "messages", HCore.config.getInt("send-message.cooldown.time"));
                    c.start();
                }
            }
        }

        // Если в сообщении пользователя обнаружена реклама.
        if (HCore.config.getBoolean("fix-advertisement.checks.messages")) {
            if (!Methods.isPerm(player, "hcore.bypass.advertisement")) {
                if (Methods.isAdv(message)) {
                    e.setCancelled(true);
                    Informer.send(player, HCore.lang.getString("errors.advertisement.messages"));
                    return false;
                }
            }
        }

        // Проверяем надо-ли модифицировать сообщение.
        if (HCore.config.getBoolean("send-message.modify.enabled")) {
            String format      = HCore.config.getString("send-message.modify.format");
            String playername  = player.getName();

            if (HCore.config.getBoolean("join-server.custom-nickname.enabled")) {
                if (Methods.isPerm(player, null)) {
                    playername = HCore.config.getString("join-server.custom-nickname.color.admins") + playername;
                }
            }

            ConfigurationSection placeholders = HCore.lang.getConfigurationSection("placeholders");
            if (placeholders != null) {
                for (String placeholder : placeholders.getKeys(false)) {
                    format = format.replace("{" + placeholder + "}", HCore.lang.getString("placeholders." + placeholder));
                }
            }

            format = format.replace("{player}", playername);
            format = format.replace("{message}", message);
            format = Methods.colorSet(format);

            e.setFormat(format);
            e.setMessage(message);
        }

        return true;
    }
}
