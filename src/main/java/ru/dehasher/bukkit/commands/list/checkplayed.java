package ru.dehasher.bukkit.commands.list;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;

public class checkplayed {
    public static boolean send(CommandSender sender, String command, String[] args) {
        int length    = args.length;
        Player player = null;
        String info   = Methods.getCommandInfo(command);

        if (sender instanceof Player) player = (Player) sender;

        if (Methods.isPerm(player, "hcore.command.checkplayed")) {
            if (length > 0) {
                Player target = Bukkit.getPlayer(args[0]);
                Informer.send(player, (target.hasPlayedBefore()) ? 1 : 0);
                return true;
            } else {
                Informer.send(player, info);
            }
        } else {
            Informer.send(player, HCore.lang.getString("errors.no-perm"));
        }
        return false;
    }
}