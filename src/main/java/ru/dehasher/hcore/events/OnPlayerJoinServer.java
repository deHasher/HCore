package ru.dehasher.hcore.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Methods;

public class OnPlayerJoinServer implements Listener {

    public OnPlayerJoinServer(HCore hCore) {}

	// Когда игрок заходит на сервер выставляем ему необходимые параметры.
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerJoinEvent(PlayerJoinEvent e) {
    	Player player = e.getPlayer();

    	// Деопаем игрока который только что вошёл.
    	if (HCore.config.getBoolean("settings.join-server.auto-deop")) {
    		if (player.isOp()) {
    			player.setOp(false);
    		}
    	}

    	// Выдаём флай игроку.
    	if (HCore.config.getBoolean("settings.join-server.auto-fly")) {
    		player.setAllowFlight(true);
    	}

    	// Работа с кастомными хпшками.
	    if (player.hasPlayedBefore() && !HCore.config.getBoolean("settings.join-server.always-set-max-hp")) {
	    	Methods.editHealth(player, false);
	    } else {
	    	Methods.editHealth(player, true);
	    }

	    // Работа с кастомными никами.
    	if (HCore.config.getBoolean("settings.join-server.custom-nickname.enabled")) {
        	Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

        	Team users = board.getTeam("users");
        	if (users == null) users = board.registerNewTeam("users");

        	Team admins = board.getTeam("admins");
        	if (admins == null) admins = board.registerNewTeam("admins");

        	users.setPrefix(Methods.color(HCore.config.getString("settings.join-server.custom-nickname.color.users")));
        	admins.setPrefix(Methods.color(HCore.config.getString("settings.join-server.custom-nickname.color.admins")));

    		if (Methods.isAdmin(player) || Methods.isAuthor(player)) {
    			admins.addEntry(player.getName());
    		} else {
    			users.addEntry(player.getName());
    		}

	    	if (HCore.config.getBoolean("settings.join-server.custom-nickname.disable-collisions")) {
	    		users.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
	    		admins.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
	    	}
        }

    	// Телепортируем на локацию спавна.
    	if (HCore.config.getBoolean("settings.join-server.force-tp-to-spawn")) {
	        Location loc = (Location)HCore.spawn.get("location");
	        if (loc != null) {
	        	player.teleport(loc);
	        }
    	}
    }
}