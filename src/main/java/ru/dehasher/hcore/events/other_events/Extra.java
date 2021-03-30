package ru.dehasher.hcore.events.other_events;

import org.bukkit.Bukkit;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import org.bukkit.inventory.ItemStack;
import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Methods;

@SuppressWarnings("deprecation")
public class Extra implements Listener {

    public Extra(HCore plugin) {}

    // Когда игрок возрождается.
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawnEvent(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		Methods.editHealth(player, true);
	}

	// Когда игрок пишет текст на табличке.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChangeEvent(SignChangeEvent e) {
    	int i = 0;
    	for (String line : e.getLines()) {
    		if (Methods.isAdv(line) && HCore.config.getBoolean("settings.fix-advertisement.checks.signs")) {
    			e.setLine(i, Methods.color(HCore.config.getString("settings.fix-advertisement.replacement")));
    		} else if (HCore.config.getBoolean("settings.other-params.colored-signs")) {
    			e.setLine(i, Methods.color(e.getLine(i)));
    		}
    		i++;
    	}
    }

    // Когда игрок заходит на сервер.
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		// Скрываем игрока от других игроков.
	    if (HCore.config.getBoolean("settings.other-params.block-see-other-players")) {
		    for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
		    	otherPlayer.hidePlayer(player);
		    	player.hidePlayer(otherPlayer);
		    }
	    }

        // Устанавливаем максимальный уровень еды.
    	if (HCore.config.getBoolean("settings.other-params.disable-events.FoodLevelChangeEvent")) {
    		player.setFoodLevel(20);
    	}
	}
}
