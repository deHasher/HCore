package ru.dehasher.bukkit.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.api.placeholderapi.PAPI;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;

public class OnPlayerJoinServer implements Listener {

    public OnPlayerJoinServer(HCore plugin) {}

    // Когда игрок заходит на сервер выставляем ему необходимые параметры.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (HCore.server_name == null) {
            if (HCore.PlaceholderAPI) HCore.server_name = PAPI.setPlaceholders(player, "%server_name%");
            Informer.vk("Сервер " + HCore.server_type + " #{server} активен.");
        }

        // Деопаем игрока который только что вошёл.
        if (HCore.config.getBoolean("join-server.auto-deop")) {
            if (player.isOp()) {
                player.setOp(false);
            }
        }

        // Выдаём флай игроку.
        if (HCore.config.getBoolean("join-server.auto-fly")) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        // Работа с кастомными хпшками.
        Methods.setHealth(player);

        // Работа с кастомными никами.
        if (HCore.config.getBoolean("join-server.custom-nickname.enabled")) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            if (manager != null) {
                Scoreboard board = manager.getMainScoreboard();

                Team users = board.getTeam("users");
                if (users == null) users = board.registerNewTeam("users");

                Team admins = board.getTeam("admins");
                if (admins == null) admins = board.registerNewTeam("admins");

                users.setPrefix(Methods.colorSet(HCore.config.getString("join-server.custom-nickname.color.users")));
                admins.setPrefix(Methods.colorSet(HCore.config.getString("join-server.custom-nickname.color.admins")));

                if (Methods.isPerm(player, null)) {
                    admins.addEntry(player.getName());
                } else {
                    users.addEntry(player.getName());
                }

                if (HCore.config.getBoolean("join-server.custom-nickname.disable-collisions")) {
                    users.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                    admins.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                }
            }
        }

        // Телепортируем на локацию спавна.
        if (HCore.config.getBoolean("join-server.spawn.first") && !player.hasPlayedBefore() || HCore.config.getBoolean("join-server.spawn.always")) {
            Methods.teleportPlayer(player, Methods.getSpawnLocation("overworld"));
        }
    }
}
