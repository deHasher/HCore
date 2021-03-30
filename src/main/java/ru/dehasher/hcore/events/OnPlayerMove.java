package ru.dehasher.hcore.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import ru.dehasher.hcore.HCore;

public class OnPlayerMove implements Listener {

	public OnPlayerMove(HCore hCore) {}

	// Прыжки на блоках с помощью нажимных плит.
	@EventHandler
	private void onPlayerMoveEvent(PlayerMoveEvent e) {
    	Player player       = e.getPlayer();

    	double height       = HCore.config.getDouble("settings.batuts.vector.height");
    	double direction    = HCore.config.getDouble("settings.batuts.vector.direction");

    	Material blockup    = Material.getMaterial(HCore.config.getString("settings.batuts.block.up"));
    	Material blockdown  = Material.getMaterial(HCore.config.getString("settings.batuts.block.down"));

    	if (e.getTo().getBlock().getType() == blockup && e.getTo().getBlock().getRelative(BlockFace.DOWN).getType() == blockdown) {
    		if (HCore.config.getBoolean("settings.batuts.sound.enabled")) {
    	    	String sound = HCore.config.getString("settings.batuts.sound.play");
    			for (Player all : Bukkit.getOnlinePlayers()) {
    				all.playSound(player.getLocation(), Sound.valueOf(sound), 1.0F, 1.0F);  
    			}
    		}

    		Vector v = player.getLocation().getDirection().multiply(direction).setY(player.getLocation().getDirection().getY() + height);
    		player.setVelocity(v);
    	}
    }
}