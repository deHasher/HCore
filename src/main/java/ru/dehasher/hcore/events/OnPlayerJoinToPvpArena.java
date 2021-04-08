package ru.dehasher.hcore.events;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.api.essentials.EAPI;
import ru.dehasher.hcore.managers.Informer;
import ru.dehasher.hcore.managers.Methods;

public class OnPlayerJoinToPvpArena implements Listener {

    private static WorldGuardPlugin WorldGuard;

    public OnPlayerJoinToPvpArena(HCore plugin) {
        WorldGuard = WorldGuardPlugin.inst();
        if (HCore.Essentials) {
            Bukkit.getPluginManager().registerEvents(new EAPI(), HCore.getPlugin());
        }
    }

    // Постоянная проверка игрока.
    public static void checkPlayer(Player player) {
        if (cancelAction(player)) {
            if (HCore.config.getString("pvp-arena.flags.gamemode") != null) {
                GameMode gm = GameMode.valueOf(HCore.config.getString("pvp-arena.flags.gamemode"));
                if (player.getGameMode() != gm) player.setGameMode(gm);
            }
            if (HCore.config.getBoolean("pvp-arena.flags.block-fly")) {
                if (player.getAllowFlight()) player.setAllowFlight(false);
                if (player.isFlying()) player.setFlying(false);
            }
            if (HCore.config.getBoolean("pvp-arena.flags.block-godmode") && HCore.Essentials) {
                EAPI.offGodMode(player);
            }
        }
    }

    // Попытки сменить игроку режим игры.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent e) {
        if (HCore.config.getString("pvp-arena.flags.gamemode") == null) return;
        Player player = e.getPlayer();
        if (cancelAction(player)) {
            if (HCore.config.getString("pvp-arena.flags.gamemode") != null) {
                GameMode gm = GameMode.valueOf(HCore.config.getString("pvp-arena.flags.gamemode"));
                if (player.getGameMode() != gm) return;
            }
            e.setCancelled(true);
        }
    }

    // Попытки игроку ввести команду.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {

        String[] command = e.getMessage().split(" ");
        Player player    = e.getPlayer();

        // Блокируем команды которые вводит игрок.
        if (HCore.config.getBoolean("pvp-arena.flags.disable-commands")) {
            if (cancelAction(player)) {
                for (String cmd : HCore.config.getStringList("pvp-arena.whitelist-commands")) {
                    if (cmd.equals(e.getMessage().toLowerCase())) return;
                }
                Informer.send(player, HCore.lang.getString("errors.pvp-arena.commands-disabled"));
                e.setCancelled(true);
            }
        }

        // Блокируем команды которые вводят другие игроки и которые относятся к игроку на пвп арене.
        for (String cmd : command) {
            Player target = HCore.getPlugin().getServer().getPlayer(cmd);
            if (target != null && target.isOnline()) {
                if (cancelAction(target)) {
                    for (String whitelist : HCore.config.getStringList("pvp-arena.whitelist-other-commands")) {
                        if (e.getMessage().toLowerCase().startsWith(whitelist.toLowerCase())) return;
                    }
                    Informer.send(player, HCore.lang.getString("errors.pvp-arena.player-in-pvp"));
                    e.setCancelled(true);
                }
            }
        }
    }

    // Проверка где находится игрок и можно ли его трогать.
    public static boolean cancelAction(Player player) {
        if (Methods.isPerm(player, "hcore.bypass.pvp")) return false;

        // Локация игрока.
        Location loc = player.getLocation();

        // Список регионов мира где находится игрок.
        RegionManager regionManager = WorldGuard.getRegionManager(loc.getWorld());

        // Список регионов в которых находится игрок.
        ApplicableRegionSet set     = regionManager.getApplicableRegions(loc);
        for (ProtectedRegion region : set.getRegions()) {
            if (HCore.config.getStringList("pvp-arena.regions").contains(region.getId())) {
                return true;
            }
        }

        return false;
    }
}