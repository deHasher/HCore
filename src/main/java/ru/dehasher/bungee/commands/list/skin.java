package ru.dehasher.bungee.commands.list;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import ru.dehasher.bungee.managers.Informer;
import ru.dehasher.bungee.managers.Lang;

public class skin extends Command {
    String name;

    public skin(final String name) {
        super(name);
        this.name  = name;
    }

    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            Informer.send(player, Lang.skinInfo0);
            Informer.send(player, Lang.skinInfo1);
            Informer.send(player, Lang.skinInfo2);
            Informer.send(player, Lang.skinInfo3);
            Informer.send(player, Lang.skinInfo4);
            Informer.send(player, Lang.skinInfo5);
            Informer.send(player, Lang.skinInfo6);
            Informer.send(player, Lang.skinInfo7);
            Informer.send(player, Lang.skinInfo8);
        } else {
            Informer.send(Lang.prefix2 + Lang.notPlayer);
        }
    }
}
