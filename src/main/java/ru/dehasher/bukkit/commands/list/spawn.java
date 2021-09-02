package ru.dehasher.bukkit.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;

public class spawn {
    public static boolean send(CommandSender sender, String command, String[] args) {
        int length    = args.length;
        Player player = null;
        String info   = Methods.getCommandInfo(command);
        String player_name;

        if (sender instanceof Player) player = (Player) sender;

        if (length == 0 || length == 1 && (player != null && player.getName().equals(args[0]))) {
            if (player == null) {
                Informer.send(info);
            } else {
                if (Methods.isPerm(player, "hcore.command.spawn")) {
                    Informer.send(player, HCore.lang.getString("commands.spawn.self"));
                    Methods.teleportPlayer(player, Methods.getSpawnLocation("overworld"));
                    return true;
                } else {
                    Informer.send(player, HCore.lang.getString("errors.no-perm"));
                }
            }
        } else {
            Player target = HCore.getPlugin().getServer().getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                Informer.send(player, HCore.lang.getString("errors.player-not-found"));
            } else {
                player_name = (player == null) ? HCore.lang.getString("console-name") : player.getName();
                if (Methods.isPerm(player, "hcore.command.spawn.others")) {
                    if (Methods.isPerm(target, "hcore.command.spawn.exempt")) {
                        Informer.send(player, HCore.lang.getString("commands.spawn.other.error"));
                    } else {
                        Informer.send(player, HCore.lang.getString("commands.spawn.other.self").replace("{player}", target.getName()));
                        Informer.send(target, HCore.lang.getString("commands.spawn.other.target").replace("{player}", player_name));

                        Methods.teleportPlayer(target, Methods.getSpawnLocation("overworld"));
                        return true;
                    }
                } else {
                    Informer.send(player, HCore.lang.getString("errors.no-perm"));
                }
            }
        }
        return false;
    }
}