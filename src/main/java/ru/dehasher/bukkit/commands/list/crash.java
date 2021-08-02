package ru.dehasher.bukkit.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.api.protocollib.PLAPI;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;
import ru.dehasher.bukkit.managers.Plugins;

public class crash {
    public static boolean send(CommandSender sender, String command, String[] args) {
        int length    = args.length;
        Player player = null;
        String info   = Methods.getCommandInfo(command);

        if (sender instanceof Player) player = (Player) sender;

        if (Methods.isPerm(player, "hcore.command.crash")) {
            if (length > 0) {
                Player target = HCore.getPlugin().getServer().getPlayer(args[0]);
                if (target == null || !target.isOnline()) {
                    Informer.send(player, HCore.lang.getString("errors.player-not-found"));
                } else {
                    if (!Methods.isPerm(target, "hcore.command.crash.exempt")) {
                        if (Methods.checkPlugin(Plugins.ProtocolLib)) {
                            PLAPI.crash(target);
                            Informer.send(player, HCore.lang.getString("commands.crash.success")
                                    .replace("{player}", target.getName())
                            );
                            return true;
                        } else {
                            Informer.send(player, HCore.lang.getString("errors.no-plugin")
                                    .replace("{plugin}", Plugins.ProtocolLib)
                            );
                        }
                    } else {
                        Informer.send(player, HCore.lang.getString("commands.crash.error"));
                    }
                }
            } else {
                Informer.send(player, info);
            }
        } else {
            Informer.send(player, HCore.lang.getString("errors.no-perm"));
        }
        return false;
    }
}