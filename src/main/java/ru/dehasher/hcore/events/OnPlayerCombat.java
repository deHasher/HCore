package ru.dehasher.hcore.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.api.protocollib.WrapperPlayClientUseEntity;
import ru.dehasher.hcore.api.protocollib.WrapperPlayServerEntityStatus;

public class OnPlayerCombat implements Listener {

    public OnPlayerCombat(HCore plugin) {
        if (HCore.ProtocolLib) {
            ProtocolManager pm = ProtocolLibrary.getProtocolManager();
            // Делаем фейк анимации ударов.
            if (HCore.config.getBoolean("combat.fake-damage-animation")) {
                pm.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.USE_ENTITY) {
                    @Override
                    public void onPacketReceiving(PacketEvent e) {
                        WrapperPlayClientUseEntity checker = new WrapperPlayClientUseEntity(e.getPacket());
                        if (checker.getTarget(e) instanceof Player) {
                            Player target = (Player) checker.getTarget(e);
                            Player player = e.getPlayer();
                            if (checker.getType().name().equals("ATTACK")) {
                                WrapperPlayServerEntityStatus attack = new WrapperPlayServerEntityStatus();
                                attack.setEntityID(target.getEntityId());
                                attack.setEntityStatus((byte) 2);
                                attack.sendPacket(player);
                            }
                        }
                    }
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        effect(e.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeathEvent(EntityDeathEvent e) {
        effect(e.getEntity());
    }

    private void effect(Entity entity) {
        if (HCore.config.getBoolean("combat.blood.enabled")) {
            Material material = Material.valueOf(HCore.config.getString("combat.blood.effect"));
            entity.getWorld().playEffect(
                    entity.getLocation(),
                    Effect.STEP_SOUND,
                    material
            );
        }
    }
}
