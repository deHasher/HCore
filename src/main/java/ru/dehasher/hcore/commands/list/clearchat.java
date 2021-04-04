package ru.dehasher.hcore.commands.list;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Informer;
import ru.dehasher.hcore.managers.Methods;

public class clearchat {
    public static boolean send(CommandSender sender, String command, String[] args) {
        Player player = null;
        if (sender instanceof Player) player = (Player)sender;

        if (player != null) {
            if (Methods.isPerm(player, "hcore.command.clearchat")) {

                for (int i = 0; i < 100; i++) {
                    Informer.send(player, "\n");
                }
                Informer.send(player, HCore.lang.getString("commands.clearchat.success"));
                player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 500.0f, 1.0f);
                return true;
            } else {
                Informer.send(player, HCore.lang.getString("errors.no-perm"));
            }
        } else {
            Informer.send(HCore.lang.getString("errors.only-player"));
        }
        return false;
    }
}