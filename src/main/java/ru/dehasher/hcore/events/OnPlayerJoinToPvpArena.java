package ru.dehasher.hcore.events;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import org.bukkit.inventory.ItemStack;
import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.api.essentials.EAPI;
import ru.dehasher.hcore.api.gadgetsmenu.GMAPI;
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
            if (HCore.config.getBoolean("pvp-arena.flags.block-gadgets") && HCore.GadgetsMenu) {
                GMAPI.getPlugin(player).unequipGadget();
            }
            if (HCore.config.getBoolean("pvp-arena.clear-custom-items.enabled")) {
                ItemStack[] inv = player.getInventory().getContents();
                for (int slot = 0; slot < inv.length; slot++) {
                    ItemStack s = inv[slot];
                    if (inv[slot] == null || inv[slot].getType().equals(Material.AIR)) continue;
                    if (!s.hasItemMeta()) continue;
                    for (String item : HCore.config.getStringList("pvp-arena.clear-custom-items.item-names")) {
                        if (s.getItemMeta().getDisplayName().equals(Methods.colorSet(item))) {
                            player.getInventory().setItem(slot, null);
                        }
                    }
                }
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

        if (Methods.isPerm(player, "hcore.bypass.pvp")) return;

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

            // Скипаем название команды.
            if (cmd.equals(command[0])) continue;

            // Скипаем если в кусочке команды всё состоит из цифр.
            if (cmd.matches("-?(0|[1-9]\\d*)")) continue;

            // Скипаем аргументы команды.
            if (HCore.config.getStringList("pvp-arena.whitelist-other-arguments").contains(cmd)) continue;

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

        for (ProtectedRegion regions : set.getRegions()) {
            for (String info : HCore.config.getStringList("pvp-arena.regions")) {
                String[] data = info.split(":");
                String region = data[0];
                String world  = data[1];
                if (regions.getId().equals(region) && loc.getWorld().getName().equals(world)) {
                    return true;
                }
            }
        }

        return false;
    }
}