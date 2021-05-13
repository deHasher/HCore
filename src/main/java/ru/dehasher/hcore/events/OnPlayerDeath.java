package ru.dehasher.hcore.events;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Experience;
import ru.dehasher.hcore.managers.Methods;

public class OnPlayerDeath implements Listener {

    public OnPlayerDeath(HCore plugin) {}

    // Устанавливаем шанс дропа предметов после смерти.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        if (HCore.config.getBoolean("death.chance-on-drop-items.enabled")) {
            Random random = new Random();
            Player player = e.getEntity().getPlayer();

            // Дроп предметов.
            if (!e.getKeepInventory()) {
                e.setKeepInventory(true);
                ItemStack[] inv = player.getInventory().getContents();

                for (int slot = 0; slot < inv.length; slot++) {
                    ItemStack item = inv[slot];

                    // Пропускаем воздух.
                    if (item == null || item.getType().equals(Material.AIR)) continue;

                    double chance_item = HCore.config.getDouble("death.chance-on-drop-items.chance.item");
                    boolean drop       = random.nextDouble() <= chance_item;

                    if (drop) {
                        player.getInventory().setItem(slot, null);
                    } else {
                        e.getDrops().remove(item);
                    }
                }
            }

            // Дроп опыта.
            if (!e.getKeepLevel()) {
                e.setKeepLevel(true);

                double chance_exp = HCore.config.getDouble("death.chance-on-drop-items.chance.exp");
                boolean drop      = random.nextDouble() <= chance_exp;

                if (drop) {
                    Experience.setTotalExperience(player, 0);
                } else {
                    e.setDroppedExp(0);
                }
            }
        }
    }

    // Когда игрок возрождается.
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawnEvent(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        Methods.setHealth(player);

        Methods.teleportPlayer(player, Methods.getSpawnLocation("overworld"));
        // Телепортируем игрока к спавну.
        if (HCore.config.getBoolean("death.teleport-to-spawn")) {
            Location loc = Methods.getSpawnLocation("overworld");
            if (loc != null) e.setRespawnLocation(loc);
        }
    }
}
