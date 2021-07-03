package ru.dehasher.bungee.commands.list;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import ru.dehasher.bungee.HCore;
import ru.dehasher.bungee.managers.Informer;
import ru.dehasher.bungee.managers.Lang;
import ru.dehasher.bungee.managers.Methods;

public class hub extends Command {
    String name;

    public hub(final String name) {
        super(name);
        this.name  = name;
    }

    public void execute(CommandSender sender, String[] args) {
        ServerInfo hub = Methods.getHub();
        if (sender instanceof ProxiedPlayer && hub != null) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (!player.getServer().getInfo().getName().contains(HCore.hub)) {
                Informer.send(player, Lang.prefix2 + Lang.tpInHub);
                player.connect(hub);
            } else {
                Informer.send(player, Lang.prefix2 + Lang.alreadyInHub);
            }
        } else {
            Informer.send(Lang.prefix2 + Lang.notPlayer);
        }
    }
}
