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
import ru.dehasher.hcore.timers.tEggs;

public class OnPlayerUseSpawnegg implements Listener {
    private final HCore plugin;

    public static List<Player> eggs = new ArrayList<Player>();

    public OnPlayerUseSpawnegg(HCore plugin) {
    	this.plugin = plugin;
    }

	// Если поц юзает яйки и у него нема прав.
    @EventHandler
    private void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e) {
    	Player player       = e.getPlayer();
    	PlayerInventory inv = player.getInventory();

    	if (player.hasPermission(HCore.config.getString("settings.cooldown-on-use-spawnegg.permission"))) return;

    	if (inv.getItemInMainHand().getType().name().endsWith("_EGG")) {
    		if (eggs.contains((Object)player)) {
    			e.setCancelled(true);
    			Informer.PLAYER(player, HCore.lang.getString("messages.errors.egg-spawn-cooldown"));
    			return;
    		}
    		eggs.add(player);
    		Bukkit.getScheduler().runTaskLater(plugin, (Runnable)new tEggs(player), 20L * Long.valueOf(HCore.config.getInt("settings.cooldown-on-use-spawnegg.time")));
    		return;
    	}

    	if (inv.getItemInOffHand().getType().name().endsWith("_EGG")) {
    		e.setCancelled(true);
    		Informer.PLAYER(player, HCore.lang.getString("messages.errors.egg-spawn-cooldown"));
    	}
    }

    // Если поц юзает яйки как-то через жопу и у него нет прав на них.
    @EventHandler
    private void onPlayerInteractEvent(PlayerInteractEvent e) {
    	Player player       = e.getPlayer();
    	PlayerInventory inv = player.getInventory();

    	if (player.hasPermission(HCore.config.getString("settings.cooldown-on-use-spawnegg.permission"))) return;

    	if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) {
    		if (inv.getItemInOffHand().getType().name().endsWith("_EGG")) {
    			e.setCancelled(true);
    			Informer.PLAYER(player, HCore.lang.getString("messages.errors.egg-spawn-cooldown"));
    		}
    		return;
    	}

    	if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
    		if (inv.getItemInMainHand().getType().name().endsWith("_EGG")) {
    			if (eggs.contains((Object)player)) {
    				e.setCancelled(true);
    				Informer.PLAYER(player, HCore.lang.getString("messages.errors.egg-spawn-cooldown"));
    				return;
    			}
    			eggs.add(player);
    			Bukkit.getScheduler().runTaskLater(plugin, (Runnable)new tEggs(player), 20L * Long.valueOf(HCore.config.getInt("settings.cooldown-on-use-spawnegg.time")));
    			return;
    		}
    		if (inv.getItemInOffHand().getType().name().endsWith("_EGG")) {
    			e.setCancelled(true);
    			Informer.PLAYER(player, HCore.lang.getString("messages.errors.egg-spawn-cooldown"));
    		}
    		return;
    	}
    }
}