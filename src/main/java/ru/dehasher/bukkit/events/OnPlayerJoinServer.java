package ru.dehasher.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.api.placeholderapi.PAPI;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;
import ru.dehasher.bukkit.managers.Plugins;

import java.util.HashMap;

public class OnPlayerJoinServer implements Listener {

    public OnPlayerJoinServer(HCore plugin) {}

    // Когда игрок заходит на сервер выставляем ему необходимые параметры.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        // Телепортируем на локацию спавна.
        if (HCore.config.getBoolean("join-server.spawn.first") && !player.hasPlayedBefore() || HCore.config.getBoolean("join-server.spawn.always")) {
            Methods.teleportPlayer(player, Methods.getSpawnLocation("overworld"));
        }

        // Работаем с кастомными хпшками.
        Methods.setHealth(player);

        // Отправляем уведомление о том, что сервер активен.
        if (HCore.server_name.equals("Unknown") && HCore.config.getBoolean("other-params.api-notifications.enabled") && Methods.checkPlugin(Plugins.PlaceholderAPI)) {
            HCore.server_name = PAPI.setPlaceholders(player, "%server_name%");
            Informer.url(HCore.config.getString("other-params.api-notifications.url.status"), new HashMap<String, String>(){{
                put("msg", "Сервер " + HCore.server_type + " #" + HCore.server_name + " активен.");
            }});
        }

        // Отправляем в очередь на cart-notifications.
        if (HCore.config.getBoolean("other-params.cart-notifications.enabled")) {
            HCore.ping.add(player.getName());
        }

        // Деопаем игрока который только что вошёл.
        if (HCore.config.getBoolean("join-server.deop")) {
            if (player.isOp()) player.setOp(false);
        }

        // Выдаём флай игроку.
        if (HCore.config.getBoolean("join-server.fly")) {
            if (!player.getAllowFlight()) player.setAllowFlight(true);
            if (!player.isFlying()) player.setFlying(true);
        }
    }
}
