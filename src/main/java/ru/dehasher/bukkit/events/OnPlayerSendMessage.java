package ru.dehasher.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;

public class OnPlayerSendMessage implements Listener {

    public OnPlayerSendMessage(HCore plugin) {}

    // Отслеживаем что пишет игрок.
    @EventHandler(priority = EventPriority.HIGHEST)
    public boolean onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
        Player player  = e.getPlayer();
        String message = e.getMessage().replace("%", "%%");
        String format  = e.getFormat();

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

        // Фикс отображения локального и глобального чата в CMI.
        if (!e.isCancelled()) {
            String local  = HCore.lang.getString("cmi.chat.local.replace");
            String global = HCore.lang.getString("cmi.chat.global.replace");

            format = format.replaceFirst(local, HCore.lang.getString("cmi.chat.local.1"))
                    .replaceFirst(local, HCore.lang.getString("cmi.chat.local.2"));
            format = format.replaceFirst(global, HCore.lang.getString("cmi.chat.global.1"))
                    .replaceFirst(global, HCore.lang.getString("cmi.chat.global.2"));

            e.setFormat(format);
        }

        // Проверяем включен ли чат в настройках.
        if (HCore.config.getBoolean("send-message.disable-messages.enabled")) {
            e.setCancelled(true);
            Informer.send(player, HCore.config.getString("send-message.disable-messages.message"));
            return false;
        }

        return true;
    }
}
