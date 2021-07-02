package ru.dehasher.bungee.events;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import ru.dehasher.bungee.HCore;
import ru.dehasher.bungee.managers.Informer;
import ru.dehasher.bungee.managers.Methods;

@SuppressWarnings("deprecation")
public class Extra implements Listener {

    public Extra(HCore plugin) {}

    @EventHandler
    public void onServerKickEvent(ServerKickEvent e) {
        ProxiedPlayer player = e.getPlayer();
        ServerInfo    server = Methods.getCancelServer();
        if (!player.getServer().getInfo().getName().contains(HCore.hub) && server != null) {
            e.setCancelled(true);
            e.setCancelServer(server);
            Informer.send(player, e.getKickReason());
        }
    }
}
