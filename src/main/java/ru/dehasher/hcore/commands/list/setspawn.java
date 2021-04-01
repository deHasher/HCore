package ru.dehasher.hcore.commands.list;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Informer;
import ru.dehasher.hcore.managers.Methods;

public class setspawn {
    public static boolean send(CommandSender sender, String command, String[] args) {
        int length    = args.length;
        Player player = null;
        String info   = Methods.getCommandInfo(command);

        if (sender instanceof Player) player = (Player)sender;

        if (player != null) {
            if (Methods.isPerm(player, "hcore.command.setspawn")) {
                if (length > 0) {
                    String spawn = args[0].toLowerCase();
                    Location loc = player.getLocation();
                    String path  = "locations." + spawn;
                    switch (spawn) {
                        case "overworld":
                        case "nether":
                            HCore.spawn.set(path + ".world", loc.getWorld().getName());
                            HCore.spawn.set(path + ".x", loc.getX());
                            HCore.spawn.set(path + ".y", loc.getY());
                            HCore.spawn.set(path + ".z", loc.getZ());
                            HCore.spawn.set(path + ".yaw", loc.getYaw());
                            HCore.spawn.set(path + ".pitch", loc.getPitch());

                            HCore.getPlugin().file_manager.getConfig(HCore.spawn_name + ".yml").save();
                            HCore.getPlugin().reloadFiles();

                            String message = HCore.lang.getString("messages.commands.setspawn." + spawn);

                            message = message.replace("{x}", Methods.round(loc.getX(), 2));
                            message = message.replace("{y}", Methods.round(loc.getY(), 2));
                            message = message.replace("{z}", Methods.round(loc.getZ(), 2));
                            message = message.replace("{world}", loc.getWorld().getName());

                            Informer.send(player, message);
                            return true;
                    }
                }
                Informer.send(player, info);
            } else {
                Informer.send(player, HCore.lang.getString("messages.errors.no-perm"));
            }
        } else {
            Informer.send(HCore.lang.getString("messages.errors.only-player"));
        }
        return false;
    }
}