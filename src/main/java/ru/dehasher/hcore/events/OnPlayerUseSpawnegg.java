package ru.dehasher.hcore.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Informer;
import ru.dehasher.hcore.managers.Methods;
import ru.dehasher.hcore.timers.tEggs;

public class OnPlayerUseSpawnegg implements Listener {
    private final HCore plugin;

    public static List<Player> eggs = new ArrayList<>();

    public OnPlayerUseSpawnegg(HCore plugin) {
    	this.plugin = plugin;
    }

	// Если поц юзает яйки и у него нема прав.
    @EventHandler
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e) {
    	Player player       = e.getPlayer();
    	PlayerInventory inv = player.getInventory();

    	if (Methods.isPerm(player, "hcore.bypass.cooldown.egg")) return;

    	if (inv.getItemInMainHand().getType().name().endsWith("_EGG")) {
    		if (eggs.contains(player)) {
    			e.setCancelled(true);
    			Informer.send(player, HCore.lang.getString("errors.egg-spawn-cooldown"));
    			return;
    		}
    		eggs.add(player);
    		Bukkit.getScheduler().runTaskLater(plugin, new tEggs(player), 20L * (long) HCore.config.getInt("cooldown-on-use-spawnegg.time"));
    		return;
    	}

    	if (inv.getItemInOffHand().getType().name().endsWith("_EGG")) {
    		e.setCancelled(true);
    		Informer.send(player, HCore.lang.getString("errors.egg-spawn-cooldown"));
    	}
    }

    // Если поц юзает яйки как-то через жопу и у него нет прав на них.
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
    	Player player       = e.getPlayer();
    	PlayerInventory inv = player.getInventory();

		if (Methods.isPerm(player, "hcore.bypass.cooldown.egg")) return;

    	if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
    		if (inv.getItemInOffHand().getType().name().endsWith("_EGG")) {
    			e.setCancelled(true);
    			Informer.send(player, HCore.lang.getString("errors.egg-spawn-cooldown"));
    		}
    		return;
    	}

    	if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
    		if (inv.getItemInMainHand().getType().name().endsWith("_EGG")) {
    			if (eggs.contains(player)) {
    				e.setCancelled(true);
    				Informer.send(player, HCore.lang.getString("errors.egg-spawn-cooldown"));
    				return;
    			}
    			eggs.add(player);
    			Bukkit.getScheduler().runTaskLater(plugin, new tEggs(player), 20L * (long) HCore.config.getInt("cooldown-on-use-spawnegg.time"));
    			return;
    		}
    		if (inv.getItemInOffHand().getType().name().endsWith("_EGG")) {
    			e.setCancelled(true);
    			Informer.send(player, HCore.lang.getString("errors.egg-spawn-cooldown"));
    		}
    	}
    }
}
