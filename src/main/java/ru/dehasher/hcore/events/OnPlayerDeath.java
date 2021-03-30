package ru.dehasher.hcore.events;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ru.dehasher.hcore.HCore;

public class OnPlayerDeath implements Listener {

	public OnPlayerDeath(HCore hCore) {}

	// Устанавливаем шанс дропа предметов после смерти.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
    	if (e.getKeepLevel() && e.getKeepInventory()) return;

        Random random = new Random();
	
        // Дроп предметов.
        if (!e.getKeepInventory()) {
        	e.setKeepInventory(true);

        	ItemStack[] inv = e.getEntity().getInventory().getContents();
        	for (int i = 0; i < inv.length; i++) {
        		ItemStack s = inv[i];

        		// Пропускаем воздух.
        		if (inv[i] == null || inv[i].getType().equals(Material.AIR)) continue;

        		// Пропускаем предметы у которых присутствует описание.
        		if (s.hasItemMeta()) {
        			ItemMeta m = s.getItemMeta();
					if (m != null && m.hasLore()) {
        				continue;
        			}
        		}

        		double chance_item = HCore.config.getDouble("settings.chance-on-drop-items-after-death.chance.item");
        		boolean drop = random.nextDouble() <= chance_item;

        		if (drop) {
        			e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), s);
        			e.getEntity().getInventory().setItem(i, null);
        		}
        	}
        }

        // Дроп опыта.
        if (!e.getKeepLevel()) {
        	int total_exp = e.getEntity().getTotalExperience();
        	double chance_exp = HCore.config.getDouble("settings.chance-on-drop-items-after-death.chance.exp");
        	double amount_exp = HCore.config.getDouble("settings.chance-on-drop-items-after-death.amount-dropped-exp");

        	if (random.nextDouble() < chance_exp) {
        		double percentage = random.nextDouble() * amount_exp;
        		int p = (int)(percentage * 100.0d);
        		int dropped = total_exp * p / 100;
        		e.setDroppedExp(dropped);
        	}
        }
	}
}
