package ru.dehasher.hcore.commands.list;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Informer;
import ru.dehasher.hcore.managers.Methods;

public class spawn {
    public static boolean send(CommandSender sender, String command, String[] args) {
		Player player = null;
		Location loc = (Location) HCore.spawn.get("location");

		if (sender instanceof Player) player = (Player)sender;
		if (loc == null) return false;

		String player_name;
		if (player == null) {
			player_name = "" + HCore.lang.getString("messages.console-name");
		} else {
			player_name = player.getName();
		}

		int length = args.length;
    	if (length == 0 || length == 1 && (player != null && player.getName().equals(args[0]))) {
    		if (player == null) {
    			Informer.send(HCore.lang.getString("messages.errors.only-player"));
    			return false;
			} else {
				if (Methods.isPerm(player, "hcore.command.spawn")) {
					Informer.send(player, HCore.lang.getString("messages.commands.spawn.self"));
					player.teleport(loc);
					return true;
				} else {
					Informer.send(player, HCore.lang.getString("messages.errors.no-perm"));
					return false;
				}
			}
		} else {
			Player target = HCore.getPlugin().getServer().getPlayer(args[0]);
			if (target == null || !target.hasPlayedBefore() || !target.isOnline()) {
				Informer.send(player, HCore.lang.getString("messages.errors.player-not-found"));
				return false;
			} else {
				if (Methods.isPerm(player, "hcore.command.spawn.others")) {
					if (Methods.isPerm(target, "hcore.command.spawn.exempt")) {
						Informer.send(player, HCore.lang.getString("messages.commands.spawn.other.error"));
						return false;
					} else {
						Informer.send(player, HCore.lang.getString("messages.commands.spawn.other.self").replace("{player}", target.getName()));
						Informer.send(target, HCore.lang.getString("messages.commands.spawn.other.target").replace("{player}", player_name));

						target.teleport(loc);
						return true;
					}
				} else {
					Informer.send(player, HCore.lang.getString("messages.errors.no-perm"));
					return false;
				}
			}
		}
    }
}