package ru.dehasher.hcore.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Informer;
import ru.dehasher.hcore.managers.Methods;

public class hreload {
    public static boolean send(CommandSender sender, String command, String[] args) {
        Player player = null;
        if (sender instanceof Player) player = (Player)sender;

        if (Methods.isPerm(player, "hcore.command.hreload")) {
            if (HCore.getPlugin().reloadFiles() && HCore.getPlugin().registerCommands()) {
                Informer.send(player, HCore.lang.getString("commands.hreload.success"));
                return true;
            } else {
                Informer.send(player, HCore.lang.getString("commands.hreload.error"));
                return false;
            }
        } else {
            Informer.send(player, HCore.lang.getString("errors.no-perm"));
        }
        return false;
    }
}