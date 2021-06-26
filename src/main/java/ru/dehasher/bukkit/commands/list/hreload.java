package ru.dehasher.bukkit.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;

public class hreload {
    public static boolean send(CommandSender sender, String command, String[] args) {
        Player player = null;
        if (sender instanceof Player) player = (Player) sender;

        if (Methods.isPerm(player, "hcore.command.hreload")) {
            if (HCore.getPlugin().reloadFiles() && HCore.getPlugin().registerCommands()) {
                Informer.send(player, HCore.lang.getString("commands.hreload.success"));
                return true;
            } else {
                Informer.send(player, HCore.lang.getString("commands.hreload.error"));
            }
        } else {
            Informer.send(player, HCore.lang.getString("errors.no-perm"));
        }
        return false;
    }
}