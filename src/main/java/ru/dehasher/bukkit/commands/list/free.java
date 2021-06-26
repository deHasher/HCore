package ru.dehasher.bukkit.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.managers.Cooldowner;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;

public class free {
    public static boolean send(CommandSender sender, String command, String[] args) {
        Player player = null;
        if (sender instanceof Player) player = (Player) sender;

        if (player != null) {
            if (Methods.isPerm(player, "hcore.command.free")) {
                if (Cooldowner.isInCooldown(player, "free")) {
                    Informer.send(player, HCore.lang.getString("commands.free.error"));
                } else {
                    Cooldowner c = new Cooldowner(player, "free", 86400);
                    c.start();

                    String cmd = HCore.config.getString("send-command.plugin.commands.free.data.command");
                    if (cmd.startsWith("/")) {
                        cmd = cmd.substring(1);
                    }
                    cmd = cmd.replace("{player}", player.getName());
                    Methods.sendConsole(cmd);

                    Informer.send(player, HCore.lang.getString("commands.free.success"));

                    return true;
                }
            } else {
                Informer.send(player, HCore.lang.getString("errors.no-perm"));
            }
        } else {
            Informer.send(HCore.lang.getString("errors.only-player"));
        }
        return false;
    }
}