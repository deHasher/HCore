package ru.dehasher.hcore.events;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.api.protocollib.PLAPI;

public class OnPlayerCombat implements Listener {

    public OnPlayerCombat(HCore plugin) {
        if (HCore.ProtocolLib) PLAPI.fakeDamageAnimation();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (!e.isCancelled()) effect(e.getEntity(), 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeathEvent(EntityDeathEvent e) {
        effect(e.getEntity(), 20);
    }

    private void effect(Entity entity, int count) {
        if (entity instanceof Monster || entity instanceof Animals || entity instanceof Player) {
            if (HCore.config.getBoolean("combat.blood.enabled")) {
                Material material = Material.valueOf(HCore.config.getString("combat.blood.effect"));
                for (int i = 0; i < count; i++) sendParticles(entity, material);
            }
        }
    }

    private void sendParticles(Entity entity, Material material) {
        entity.getWorld().playEffect(entity.getLocation(), Effect.STEP_SOUND, material);
    }
}
