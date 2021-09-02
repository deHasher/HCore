package ru.dehasher.bukkit.api.protocollib;

import com.comphenix.protocol.PacketType;
//import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import ru.dehasher.bukkit.HCore;

public class PLAPI {

    public static ProtocolManager ProtocolLib;
    public static HCore plugin;

    // Убираем звук создания портала в ЭНД.
    public static void endPortalSound() {
        ProtocolLib = ProtocolLibrary.getProtocolManager();
        if (!HCore.config.getBoolean("other-params.block-actions.end-portal-sound")) return;

        ProtocolLib.addPacketListener(new PacketAdapter(HCore.getPlugin(), ListenerPriority.HIGHEST, PacketType.Play.Server.WORLD_EVENT) {
            @Override
            public void onPacketSending(PacketEvent e) {
                WrapperPlayServerWorldEvent packet = new WrapperPlayServerWorldEvent(e.getPacket());
                if (packet.getEffectId() == 1038) packet.setDisableRelativeVolume(false);
            }
        });
    }

    // Делаем фейк анимации ударов.
    public static void fakeDamageAnimation() {
        ProtocolLib = ProtocolLibrary.getProtocolManager();
        if (!HCore.config.getBoolean("combat.fake-damage-animation")) return;

        ProtocolLib.addPacketListener(new PacketAdapter(HCore.getPlugin(), ListenerPriority.HIGHEST, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent e) {
                try {
                    Entity entity = e.getPacket().getEntityModifier(e.getPlayer().getWorld()).read(0);
                    if (!(entity instanceof Player)) return;
                    WrapperPlayClientUseEntity checker = new WrapperPlayClientUseEntity(e.getPacket());

                    Player target = (Player) entity;
                    Player player = e.getPlayer();

                    if (player.getGameMode() == GameMode.SPECTATOR) return;

                    if (checker.getType().name().equals("ATTACK")) {
                        WrapperPlayServerEntityStatus attack = new WrapperPlayServerEntityStatus();
                        attack.setEntityID(target.getEntityId());
                        attack.setEntityStatus((byte) 2);
                        attack.sendPacket(player);
                    }
                } catch (Exception ignored) {}
            }
        });
    }

    public static void crash(Player player) {
        Location loc = player.getLocation();
        WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();

        packet.setType(EntityType.BLAZE);
        packet.setX(loc.getX());
        packet.setY(loc.getY());
        packet.setZ(loc.getZ());

        for (int i = 0; i < 50000; i++) {
            packet.setEntityID(i);
            packet.sendPacket(player);
        }
    }
}
