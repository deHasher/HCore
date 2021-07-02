package ru.dehasher.bungee.managers;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import ru.dehasher.bungee.HCore;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
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
    @Nullable
    public static ServerInfo getCancelServer() {
        ServerInfo cancelServer = null;
        int online              = -1;

        List<ServerInfo> hubs = HCore.getPlugin().getServers().values().stream().filter(q -> q.getName().contains(HCore.hub)).collect(Collectors.toList());
        for (ServerInfo server : hubs) {
            int serverOnline = HCore.getPlugin().getServers().get(server.getName()).getPlayers().size();
            try {
                Socket socket = new Socket();
                socket.connect(server.getSocketAddress(), 10);
                socket.close();
                if (online == -1 || serverOnline < online) {
                    online       = serverOnline;
                    cancelServer = server;
                }
            } catch (IOException ignored) {}
        }
        return cancelServer;
    }
}