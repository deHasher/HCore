package ru.dehasher.hcore.events.other_params;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.*;

import org.bukkit.material.SpawnEgg;
import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.api.protocollib.PLAPI;
import ru.dehasher.hcore.managers.Methods;

@SuppressWarnings("deprecation")
public class Extra implements Listener {

    public Extra(HCore plugin) {
    	if (HCore.ProtocolLib) PLAPI.endPortalSound();
    }

    // Когда игрок пытается телепортироваться через /gm 3.
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleportEvent(PlayerTeleportEvent e) {
		if (!HCore.config.getBoolean("other-params.block-actions.spectate-teleport")) return;
		if (e.getPlayer().getGameMode() == GameMode.SPECTATOR && e.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE)) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		if (!HCore.config.getBoolean("other-params.block-actions.use-spawners")) return;
    	Player player = e.getPlayer();
		if (player.getItemInHand().getData() instanceof SpawnEgg) {
    		if (!Methods.isPerm(player, null)) e.setCancelled(true);
		}
	}

	// Когда игрок пишет текст на табличке.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChangeEvent(SignChangeEvent e) {
    	int i = 0;
    	for (String line : e.getLines()) {
    		if (Methods.isAdv(line) && HCore.config.getBoolean("fix-advertisement.checks.signs")) {
    			e.setLine(i, Methods.colorSet(HCore.config.getString("fix-advertisement.replacement")));
    		} else if (HCore.config.getBoolean("other-params.colored-signs")) {
    			e.setLine(i, Methods.colorSet(e.getLine(i)));
    		}
    		i++;
    	}
    }

    // Когда игрок заходит на сервер.
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		// Скрываем игрока от других игроков.
	    if (HCore.config.getBoolean("other-params.block-actions.see-other-players")) {
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
