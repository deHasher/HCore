package ru.dehasher.bukkit.events;

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

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.api.essentials.EAPI;
import ru.dehasher.bukkit.api.gadgetsmenu.GMAPI;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;
import ru.dehasher.bukkit.managers.Plugins;
import ru.dehasher.bukkit.managers.Rusificator;

public class OnPlayerJoinToPvpArena implements Listener {

    private static WorldGuardPlugin WorldGuard;

    public OnPlayerJoinToPvpArena(HCore plugin) {
        WorldGuard = WorldGuardPlugin.inst();
        if (Methods.checkPlugin(Plugins.Essentials)) {
            Bukkit.getPluginManager().registerEvents(new EAPI(), HCore.getPlugin());
        }
    }

    // Постоянная проверка игрока.
    public static void checkPlayer(Player player) {
        if (!cancelAction(player)) return;

        for (String gamemode : HCore.config.getStringList("pvp-arena.block.gamemodes")) {
            if (player.getGameMode() == GameMode.valueOf(gamemode)) {
                player.setGameMode(GameMode.SURVIVAL);
            }
        }

        if (HCore.config.getBoolean("pvp-arena.block.fly")) {
            if (player.getAllowFlight() && player.getGameMode() != GameMode.SPECTATOR) {
                player.setAllowFlight(false);
                player.setFlying(false);
            }
        }

        if (HCore.config.getBoolean("pvp-arena.block.godmode") && Methods.checkPlugin(Plugins.Essentials)) EAPI.offGodMode(player);
        if (HCore.config.getBoolean("pvp-arena.block.gadgets") && Methods.checkPlugin(Plugins.GadgetsMenu)) GMAPI.getPlugin(player).unequipGadget();
        if (HCore.config.getBoolean("pvp-arena.block.vanish")  && player.hasPotionEffect(PotionEffectType.INVISIBILITY)) player.removePotionEffect(PotionEffectType.INVISIBILITY);

        if (HCore.config.getBoolean("pvp-arena.clear-custom-items.enabled")) {
            ItemStack[] inv = player.getInventory().getContents();
            for (int slot = 0; slot < inv.length; slot++) {
                ItemStack s = inv[slot];

                if (s == null) continue;
                if (s.getType().name().equals("AIR")) continue;

                if (!s.hasItemMeta()) continue;
                ItemMeta item_meta = s.getItemMeta();

                if (!item_meta.hasDisplayName()) continue;
                String item_name = item_meta.getDisplayName();

                for (String item : HCore.config.getStringList("pvp-arena.clear-custom-items.item-names")) {
                    if (item_name.equals(Methods.colorSet(item))) {
                        player.getInventory().setItem(slot, null);
                    }
                }
            }
        }
    }

    // Попытки сменить игроку режим игры.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent e) {
        Player player = e.getPlayer();
        if (!cancelAction(player)) return;

        for (String gamemode : HCore.config.getStringList("pvp-arena.block.gamemodes")) {
            if (e.getNewGameMode() == GameMode.valueOf(gamemode)) e.setCancelled(true);
        }
    }

    // Попытки игроку ввести команду.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {

        String command    = e.getMessage().toLowerCase();
        String[] commands = command.split(" ");
        Player player     = e.getPlayer();

        if (Methods.isPerm(player, "hcore.bypass.pvp")) return;

        // Блокируем команды, которые вводит игрок.
        if (HCore.config.getBoolean("pvp-arena.block.commands") && cancelAction(player)) {
            for (String cmd : HCore.config.getStringList("pvp-arena.whitelist-commands")) {
                if (
                    command.startsWith(cmd.toLowerCase()) ||
                    (!Methods.isCyrillic(command) && command.startsWith(Rusificator.replace(cmd.toLowerCase())))
                ) return;
            }
            Informer.send(player, HCore.lang.getString("errors.pvp-arena.commands-disabled"));
            e.setCancelled(true);
        }

        // Блокируем команды, которые вводят другие игроки и относятся к игроку на пвп арене.
        for (String user : commands) {

            // Скипаем название команды.
            if (user.equals(commands[0])) continue;

            // Скипаем если в кусочке команды всё состоит из цифр.
            if (user.matches("-?(0|[1-9]\\d*)")) continue;

            Player target = HCore.getPlugin().getServer().getPlayer(user);

            if (target != null && target.isOnline() && cancelAction(target)) {
                for (String cmd : HCore.config.getStringList("pvp-arena.blacklist-other-commands")) {
                    if (command.startsWith(cmd.toLowerCase()) || (!Methods.isCyrillic(command) && command.startsWith(Rusificator.replace(cmd.toLowerCase())))) {
                        Informer.send(player, HCore.lang.getString("errors.pvp-arena.player-in-pvp"));
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    // Проверка, где находится игрок и можно ли его трогать.
    public static boolean cancelAction(Player player) {
        if (Methods.isPerm(player, "hcore.bypass.pvp")) return false;

        // Локация игрока.
        Location location = player.getLocation();

        // Список регионов мира, где находится игрок.
        RegionManager regionManager = WorldGuard.getRegionManager(location.getWorld());

        // Список регионов в которых находится игрок.
        ApplicableRegionSet regions = regionManager.getApplicableRegions(location);

        for (String info : HCore.config.getStringList("pvp-arena.regions")) {
            String[] data = info.split(":");
            if (data.length == 1) {
                if (location.getWorld().getName().equals(data[0])) return true;
            } else if (data.length > 1) {
                for (ProtectedRegion region : regions.getRegions()) {
                    if (region.getId().equals(data[0]) && location.getWorld().getName().equals(data[1])) return true;
                }
            }
        }
        return false;
    }
}