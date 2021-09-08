package ru.dehasher.bukkit.events.other_params;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.*;

import org.bukkit.material.SpawnEgg;
import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.api.protocollib.PLAPI;
import ru.dehasher.bukkit.managers.Methods;
import ru.dehasher.bukkit.managers.Plugins;

@SuppressWarnings("deprecation")
public class Extra implements Listener {

    public Extra(HCore plugin) {
        if (Methods.checkPlugin(Plugins.ProtocolLib)) PLAPI.endPortalSound();
    }

    // Когда игрок пытается телепортироваться через /gm 3.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleportEvent(PlayerTeleportEvent e) {
        if (HCore.config.getBoolean("other-params.block-actions.spectate-teleport")) {
            if (e.getPlayer().getGameMode() == GameMode.SPECTATOR && e.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE)) {
                e.setCancelled(true);
            }
        }
        if (HCore.config.getBoolean("other-params.block-actions.invalid-location")) {
            if (Methods.invalidLocation(e.getTo())) e.setCancelled(true);
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

    public static void gameruleSetup() {
        for (World world : HCore.getPlugin().getServer().getWorlds()) {
            if (HCore.config.getBoolean("other-params.hide-messages.advancements")) {
                if (Double.parseDouble(Bukkit.getBukkitVersion().substring(0, 4)) > 1.12) {
                    world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
                } else {
                    world.setGameRuleValue("announceAdvancements", "false");
                }
            }
        }
    }
}
