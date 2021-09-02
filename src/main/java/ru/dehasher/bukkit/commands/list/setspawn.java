package ru.dehasher.bukkit.commands.list;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;

public class setspawn {
    public static boolean send(CommandSender sender, String command, String[] args) {
        int length    = args.length;
        Player player = null;
        String info   = Methods.getCommandInfo(command);

        if (sender instanceof Player) player = (Player) sender;

        if (player == null) {
            Informer.send(HCore.lang.getString("errors.only-player"));
            return false;
        }

        if (!Methods.isPerm(player, "hcore.command.setspawn")) {
            Informer.send(player, HCore.lang.getString("errors.no-perm"));
            return false;
        }

        if (length > 0) {
            String spawn = args[0].toLowerCase();
            Location loc = player.getLocation();
            switch (spawn) {
                case "overworld":
                case "nether":
                    HCore.spawn.set(spawn + ".world", loc.getWorld().getName());
                    HCore.spawn.set(spawn + ".x", loc.getX());
                    HCore.spawn.set(spawn + ".y", loc.getY());
                    HCore.spawn.set(spawn + ".z", loc.getZ());
                    HCore.spawn.set(spawn + ".yaw", loc.getYaw());
                    HCore.spawn.set(spawn + ".pitch", loc.getPitch());

                    HCore.getPlugin().file_manager.getConfig(HCore.spawn_file).save();
                    HCore.getPlugin().reloadFiles();

                    String message = HCore.lang.getString("commands.setspawn." + spawn);

                    message = message.replace("{x}", Methods.round(loc.getX(), 2));
                    message = message.replace("{y}", Methods.round(loc.getY(), 2));
                    message = message.replace("{z}", Methods.round(loc.getZ(), 2));
                    message = message.replace("{world}", loc.getWorld().getName());

                    Informer.send(player, message);
                    return true;
            }
        }
        Informer.send(player, info);
        return false;
    }
}