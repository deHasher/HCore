package ru.dehasher.hcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Informer;
import ru.dehasher.hcore.managers.Methods;

public class hreload implements CommandExecutor {
    private final HCore plugin;

    public hreload(HCore plugin) {
    	this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
        	Player player = (Player)sender;
        	if (Methods.isPerm(player)) {
	    	    if (plugin.reloadFiles()) {
		    	    Informer.PLAYER(player, HCore.lang.getString("messages.commands.hreload.success"));
	    	    } else {
	    	    	Informer.PLAYER(player, HCore.lang.getString("messages.commands.hreload.error"));
	    	    }
	        } else {
	        	Informer.PLAYER(player, HCore.lang.getString("messages.errors.no-perm"));
	        }
        } else if (sender instanceof ConsoleCommandSender) {
        	plugin.reloadFiles();
            return true;
        }
        return false;
    }
}