package ru.dehasher.hcore.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import org.bukkit.util.Vector;
import ru.dehasher.hcore.HCore;

public class OnBatutJump implements Listener {

    public OnBatutJump(HCore plugin) {}

    // Прыжки на блоках с помощью нажимных плит.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        double height = HCore.config.getDouble("batuts.vector.height");
        double direction = HCore.config.getDouble("batuts.vector.direction");

        Material blockup = Material.getMaterial(HCore.config.getString("batuts.block.up"));
        Material blockdown = Material.getMaterial(HCore.config.getString("batuts.block.down"));

        if (e.getAction() == Action.PHYSICAL) {
            Block block = e.getClickedBlock();
            if (block != null && block.getType() == blockup) {
                if (block.getRelative(BlockFace.DOWN).getType() == blockdown) {
                    if (HCore.config.getBoolean("batuts.sound.enabled")) {
                        String sound = HCore.config.getString("batuts.sound.play");
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            all.playSound(player.getLocation(), Sound.valueOf(sound), 1.0F, 1.0F);
                        }
                    }
                    Vector v = player.getLocation().getDirection().multiply(direction)
                            .setY(player.getLocation().getDirection().getY() + height);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(HCore.getPlugin(), () -> player.setVelocity(v), 0L);
                }
            }
        }
    }
}