package ru.dehasher.hcore.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Informer;

public class spawn implements CommandExecutor {

    public spawn(HCore plugin) {}

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
        	Player player = (Player)sender;
        	Location loc  = (Location)HCore.spawn.get("location");
        	Informer.PLAYER(player, HCore.lang.getString("messages.commands.spawn"));
        	if (loc != null) {
				player.teleport(loc);
        		return true;
        	}
        } else if (sender instanceof ConsoleCommandSender) {
        	Informer.CONSOLE(HCore.lang.getString("messages.errors.only-player"));
            return false;
        }
        return false;
    }
}