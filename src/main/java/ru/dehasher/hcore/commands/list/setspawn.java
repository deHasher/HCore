package ru.dehasher.hcore.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Informer;
import ru.dehasher.hcore.managers.Methods;

public class setspawn {
    public static boolean send(CommandSender sender, String command, String[] args) {
        Player player = null;

        if (sender instanceof Player) player = (Player)sender;

        if (player != null) {
            if (Methods.isPerm(player, "hcore.command.setspawn")) {
                HCore.spawn.set("location", player.getLocation());
                HCore.getPlugin().file_manager.getConfig(HCore.spawn_name + ".yml").save();
                Informer.send(player, HCore.lang.getString("messages.commands.setspawn"));
                return true;
            } else {
                Informer.send(player, HCore.lang.getString("messages.errors.no-perm"));
                return false;
            }
        } else {
            Informer.send(HCore.lang.getString("messages.errors.only-player"));
            return false;
        }
    }
}