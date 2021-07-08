package ru.dehasher.bukkit.commands.list;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;

import java.io.File;

@SuppressWarnings("deprecation")
public class checkplayed {
    public static boolean send(CommandSender sender, String command, String[] args) {
        int length    = args.length;
        Player player = null;
        String info   = Methods.getCommandInfo(command);

        if (sender instanceof Player) player = (Player) sender;

        if (Methods.isPerm(player, "hcore.command.checkplayed")) {
            if (length > 0) {
                OfflinePlayer target = HCore.getPlugin().getServer().getOfflinePlayer(args[0]);
                int exists = 0;
                if (target.isOnline()) exists = 1;

                String folder = HCore.getPlugin().getServer().getWorldContainer().getAbsolutePath();
                if (folder.endsWith(".")) folder = folder.substring(0, folder.length() - 1);

                String path   = HCore.getPlugin().getServer().getWorlds().get(0).getName() + "/playerdata/" + target.getUniqueId() + ".dat";
                if (new File(Methods.fixSlashes(folder + path)).exists()) exists = 1;
                sender.sendMessage(exists + "");

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