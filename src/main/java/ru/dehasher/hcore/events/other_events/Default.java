package ru.dehasher.hcore.events.other_events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;

import ru.dehasher.hcore.HCore;

@SuppressWarnings("deprecation")
public class Default implements Listener {

    public Default(HCore plugin) {}
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
		if (!HCore.config.getBoolean("settings.other-params.disable-events.PlayerInteractEvent")) return;
		e.setCancelled(true);
    }

    // Запретить ломать блоки в мире.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(BlockBreakEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.BlockBreakEvent")) return;
    	e.setCancelled(true);
    }

    // Блокировка изменения погоды.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWeather(WeatherChangeEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.WeatherChangeEvent")) return;
    	e.setCancelled(true);
    }

    // Запретить ставить блоки.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.BlockPlaceEvent")) return;
    	e.setCancelled(true);
    }

    // Запретить ставить блоки.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.EntityDamageEvent")) return;
    	e.setCancelled(true);
    }

    // Запретить взаимодействовать с сущностями.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.PlayerInteractEntityEvent")) return;
    	e.setCancelled(true);
    }

    // Запретить выкидывать предметы.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItemEvent(PlayerDropItemEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.PlayerDropItemEvent")) return;
    	e.setCancelled(true);
    }

    // Запретить подбирать предметы.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItemEvent(EntityPickupItemEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.EntityPickupItemEvent")) return;
    	e.setCancelled(true);
    }

    // Запретить блокам исчезать.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFadeEvent(BlockFadeEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.BlockFadeEvent")) return;
    	e.setCancelled(true);
    }

    // Запретить блокам иметь физику
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPhysicsEvent(BlockPhysicsEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.BlockPhysicsEvent")) return;
    	e.setCancelled(true);
    }

    // Запретить игрокам взаимодействовать с блоками.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFromToEvent(BlockFromToEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.BlockFromToEvent")) return;
    	e.setCancelled(true);
    }

    // Запретить распространение огня.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBurnEvent(BlockBurnEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.BlockBurnEvent")) return;
    	e.setCancelled(true);
    }

    // Запретить поджигать блоки.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockIgniteEvent(BlockIgniteEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.BlockIgniteEvent")) return;
    	e.setCancelled(true);
    }

    // Запретить распространение травы.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockSpreadEvent(BlockSpreadEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.BlockSpreadEvent")) return;
    	e.setCancelled(true);
    }

    // Запретить терять голод.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.FoodLevelChangeEvent")) return;
    	e.setCancelled(true);
    }

    // Блокировка взрывов.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplodeEvent(EntityExplodeEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.EntityExplodeEvent")) return;
    	e.setCancelled(true);
    }

    // Блокировка роста растений.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStructureGrowEvent(StructureGrowEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.StructureGrowEvent")) return;
    	e.setCancelled(true);
    }

    // Блокировка табуляции команд.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChatTabCompleteEvent(PlayerChatTabCompleteEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.PlayerChatTabCompleteEvent")) return;
    	e.getTabCompletions().clear();
    }

    // Блокировка формирования новых блоков.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFormEvent(BlockFormEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.BlockFormEvent")) return;
    	e.setCancelled(true);
    }

    // Блокировка спавна мобов.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawnEvent(CreatureSpawnEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.CreatureSpawnEvent")) return;
    	e.setCancelled(true);
    }

    // Запретить листве исчезать.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeavesDecayEvent(LeavesDecayEvent e) {
    	if (!HCore.config.getBoolean("settings.other-params.disable-events.LeavesDecayEvent")) return;
    	e.setCancelled(true);
    }
}