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
import org.bukkit.inventory.meta.ItemMeta;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Methods;

public class OnPlayerDeath implements Listener {

	public OnPlayerDeath(HCore plugin) {}

	// Устанавливаем шанс дропа предметов после смерти.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
		if (HCore.config.getBoolean("death.chance-on-drop-items.enabled")) {
			Random random = new Random();
			Player player = e.getEntity();

			// Дроп предметов.
			if (!e.getKeepInventory()) {
				e.setKeepInventory(true);

				ItemStack[] inv = player.getInventory().getContents();

				for (int slot = 0; slot < inv.length; slot++) {
					ItemStack s = inv[slot];

					// Пропускаем воздух.
					if (s == null || s.getType().equals(Material.AIR)) continue;

					// Пропускаем предметы у которых присутствует описание.
					if (s.hasItemMeta()) {
						ItemMeta m = s.getItemMeta();
						if (m != null && m.hasLore()) continue;
					}

					double chance_item = HCore.config.getDouble("death.chance-on-drop-items.chance.item");
					boolean drop = random.nextDouble() <= chance_item;

					if (drop) {
						player.getWorld().dropItemNaturally(player.getLocation(), s);
						player.getInventory().setItem(slot, null);
					}
				}
			}

			// Дроп опыта.
			if (!e.getKeepLevel()) {
				int total_exp     = player.getTotalExperience();
				double chance_exp = HCore.config.getDouble("death.chance-on-drop-items.chance.exp");
				double amount_exp = HCore.config.getDouble("death.chance-on-drop-items.amount-dropped-exp");

				if (random.nextDouble() < chance_exp) {
					double percentage = random.nextDouble() * amount_exp;
					int p = (int) (percentage * 100.0d);
					int dropped = total_exp * p / 100;
					e.setDroppedExp(dropped);
				}
			}
		}
	}

	// Когда игрок возрождается.
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerRespawnEvent(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		Methods.editHealth(player, true);

		Methods.teleportPlayer(player, Methods.getSpawnLocation("overworld"));
		// Телепортируем игрока к спавну.
		if (HCore.config.getBoolean("death.teleport-to-spawn")) {
			Location loc = Methods.getSpawnLocation("overworld");
			if (loc != null) e.setRespawnLocation(loc);
		}
	}
}
