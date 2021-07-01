package ru.dehasher.bungee.managers;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import ru.dehasher.bungee.HCore;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class Methods {

    // Красим сообщения.
    public static String colorSet(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    // Удаляем все цветовые коды из сообщения.
    public static String colorClear(String message) {
        return ChatColor.stripColor(colorSet(message));
    }

    // Исправляем слэши в зависимости от ОС.
    public static String fixSlashes(String text) {
        text = text.replace("/", File.separator);
        return text;
    }

    // Получаем сервер на который будет кикнут игрок.
    public static ServerInfo getCancelServer() {
        ServerInfo cancelServer = HCore.getPlugin().getServers().get(HCore.hub + "1");
        int online = -1;

        List<ServerInfo> hubs = HCore.getPlugin().getServers().values().stream().filter(q -> q.getName().contains(HCore.hub)).collect(Collectors.toList());
        for (ServerInfo server : hubs) {
            int serverOnline = HCore.getPlugin().getServers().get(server.getName()).getPlayers().size();
            if (online == -1 || serverOnline < online) {
                online       = serverOnline;
                cancelServer = server;
            }
        }
        return cancelServer;
    }
}
