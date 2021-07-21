package ru.dehasher.bungee.events;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
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
        ServerInfo    hub    = Methods.getHub();
        if (player != null && hub != null) {
            Server server = player.getServer();
            if (server != null && !server.getInfo().getName().contains(HCore.HUB)) {
                e.setCancelled(true);
                e.setCancelServer(hub);
                Informer.send(player, e.getKickReason());
            }
        }
    }
}