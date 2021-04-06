package ru.dehasher.hcore.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Informer;
import ru.dehasher.hcore.managers.Methods;

public class spawn {
    public static boolean send(CommandSender sender, String command, String[] args) {
		int length    = args.length;
		Player player = null;
		String info   = Methods.getCommandInfo(command);
		String player_name;

		if (sender instanceof Player) player = (Player)sender;

		// Если аргументов нет.
    	if (length == 0 || length == 1 && (player != null && player.getName().equals(args[0]))) {
    		if (player == null) {
    			Informer.send(info);
    			return false;
			} else {
				if (Methods.isPerm(player, "hcore.command.spawn")) {
					Informer.send(player, HCore.lang.getString("commands.spawn.self"));
					Methods.teleportPlayer(player, Methods.getSpawnLocation("overworld"));
					return true;
				} else {
					Informer.send(player, HCore.lang.getString("errors.no-perm"));
					return false;
				}
			}
		} else { // Если аргументы есть.
			Player target = HCore.getPlugin().getServer().getPlayer(args[0]);
			if (target == null || !target.isOnline()) {
				Informer.send(player, HCore.lang.getString("errors.player-not-found"));
				return false;
			} else {
				// Задаём имя того, кто тепнул на спавн чела.
				if (player == null) {
					player_name = "" + HCore.lang.getString("console-name");
				} else {
					player_name = player.getName();
				}
				if (Methods.isPerm(player, "hcore.command.spawn.others")) {
					if (Methods.isPerm(target, "hcore.command.spawn.exempt")) {
						Informer.send(player, HCore.lang.getString("commands.spawn.other.error"));
						return false;
					} else {
						Informer.send(player, HCore.lang.getString("commands.spawn.other.self").replace("{player}", target.getName()));
						Informer.send(target, HCore.lang.getString("commands.spawn.other.target").replace("{player}", player_name));

						Methods.teleportPlayer(target, Methods.getSpawnLocation("overworld"));
						return true;
					}
				} else {
					Informer.send(player, HCore.lang.getString("errors.no-perm"));
					return false;
				}
			}
		}
    }
}