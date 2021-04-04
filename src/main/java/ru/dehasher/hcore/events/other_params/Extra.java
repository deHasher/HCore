package ru.dehasher.hcore.events.other_params;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import org.bukkit.event.player.PlayerTeleportEvent;
import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Methods;

@SuppressWarnings("deprecation")
public class Extra implements Listener {

    public Extra(HCore plugin) {}

    // Когда игрок пытается телепортироваться через /gm 3.
	@EventHandler(priority = EventPriority.HIGHEST)
	final void onPlayerTeleportEvent(PlayerTeleportEvent e) {
		if (!HCore.config.getBoolean("other-params.block-spectate-teleport")) return;
		if (e.getPlayer().getGameMode() == GameMode.SPECTATOR && e.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE)) {
			e.setCancelled(true);
		}
	}

	// Когда игрок пишет текст на табличке.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChangeEvent(SignChangeEvent e) {
    	int i = 0;
    	for (String line : e.getLines()) {
    		if (Methods.isAdv(line) && HCore.config.getBoolean("fix-advertisement.checks.signs")) {
    			e.setLine(i, Methods.color(HCore.config.getString("fix-advertisement.replacement")));
    		} else if (HCore.config.getBoolean("other-params.colored-signs")) {
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
	    if (HCore.config.getBoolean("other-params.block-see-other-players")) {
		    for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
		    	otherPlayer.hidePlayer(player);
		    	player.hidePlayer(otherPlayer);
		    }
	    }

        // Устанавливаем максимальный уровень еды.
    	if (HCore.config.getBoolean("other-params.disable-events.FoodLevelChangeEvent")) {
    		player.setFoodLevel(20);
    	}
	}
}
