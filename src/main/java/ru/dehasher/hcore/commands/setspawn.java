package ru.dehasher.hcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Informer;
import ru.dehasher.hcore.managers.Methods;

public class setspawn implements CommandExecutor {
    private final HCore plugin;
	public static FileConfiguration spawn;

    public setspawn(HCore plugin) {
    	this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
        	Player player = (Player)sender;
            if (Methods.isPerm(player)) {
            	HCore.spawn.set("location", player.getLocation());
            	plugin.file_manager.getConfig(HCore.spawn_name + ".yml").save();
            	Informer.PLAYER(player, HCore.lang.getString("messages.commands.setspawn"));
    			return true;
            } else {
            	Informer.PLAYER(player, HCore.lang.getString("messages.errors.no-perm"));
    	        return false;
            }
        } else if (sender instanceof ConsoleCommandSender) {
        	Informer.CONSOLE(HCore.lang.getString("messages.errors.only-player"));
            return false;
        }
        return false;
    }
}