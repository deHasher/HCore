package ru.dehasher.bungee.commands.list;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import ru.dehasher.bungee.HCore;
import ru.dehasher.bungee.managers.Informer;
import ru.dehasher.bungee.managers.Lang;

public class hub extends Command {
    String name;

    public hub(final String name) {
        super(name);
        this.name  = name;
    }

    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (!player.getServer().getInfo().getName().equals(HCore.hub)) {
                if (ProxyServer.getInstance().getServers().containsKey(HCore.hub)) {
                    Informer.send(player, Lang.prefix2 + Lang.tpInHub);
                    player.connect(ProxyServer.getInstance().getServerInfo(HCore.hub));
                } else {
                    Informer.send(player, Lang.prefix2 + Lang.serverNotFound);
                }
            } else {
                Informer.send(player, Lang.prefix2 + Lang.alreadyInHub);
            }
        } else {
            Informer.send(Lang.prefix2 + Lang.notPlayer);
        }
    }
}
