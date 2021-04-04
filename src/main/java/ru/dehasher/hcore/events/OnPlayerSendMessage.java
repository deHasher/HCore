package ru.dehasher.hcore.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
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

    public static List<Player> chat = new ArrayList<>();

    public OnPlayerSendMessage(HCore plugin) {
    	this.plugin = plugin;
    }

	// Отслеживаем что пишет игрок.
	@EventHandler(priority = EventPriority.HIGHEST)
    public boolean onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		String message = e.getMessage();
		
		// Проверяем прописал ли игрок секретную команду.
		String[] msg  = message.split(" ");
		if (HCore.config.getBoolean("send-message.hidden-console.enabled")) {
	        if (message.startsWith(HCore.config.getString("send-message.hidden-console.cmd"))) {
	    		e.setCancelled(true);

	    		if (Methods.isPerm(player, null)) {
			        if (msg.length > 1) {
				        String cmd = message.substring(2);
				    	Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd));
		        	}
	    		}
		        Informer.send(player, HCore.lang.getString("commands.hidden-console"));
		        return false;
	        }
		}

        // Проверяем включен ли чат в настройках.
		if (HCore.config.getBoolean("send-message.disable-messages")) {
			e.setCancelled(true);
			Informer.send(player, HCore.lang.getString("errors.chat-disabled"));
			return false;
		}

		// КД на чатикс.
		if (HCore.config.getBoolean("send-message.cooldown.enabled")) {
			if (!Methods.isPerm(player, "hcore.bypass.cooldown.message")) {
		        if (chat.contains(player)) {
		            e.setCancelled(true);
		            Informer.send(player, HCore.lang.getString("errors.message-cooldown"));
		            return false;
		        }
		        chat.add(player);
		        Bukkit.getScheduler().runTaskLater(plugin, new tChat(player), 20L * (long) HCore.config.getInt("send-message.cooldown.time"));
			}
		}

		// Если в сообщении пользователя обнаружена реклама.
		if (HCore.config.getBoolean("fix-advertisement.checks.messages")) {
			if (Methods.isAdv(message) && !Methods.isPerm(player, "hcore.bypass.advertisement")) {
				e.setCancelled(true);
				Informer.send(player, HCore.lang.getString("errors.advertisement.messages"));
				return false;
			}
		}

		// Проверяем надо-ли модифицировать сообщение.
		if (HCore.config.getBoolean("send-message.modify.enabled")) {
			String format      = HCore.config.getString("send-message.modify.format");
			String playername  = player.getName();

			if (HCore.config.getBoolean("join-server.custom-nickname.enabled")) {
				if (Methods.isPerm(player, null)) {
					playername = HCore.config.getString("join-server.custom-nickname.color.admins") + playername;
				}
			}

			ConfigurationSection placeholders = HCore.lang.getConfigurationSection("placeholders");
			if (placeholders != null) {
				for (String placeholder : placeholders.getKeys(false)) {
					format = format.replace("{" + placeholder + "}", HCore.lang.getString("placeholders." + placeholder));
				}
			}

			format = format.replace("{player}", playername);
			format = format.replace("{message}", message);
			format = Methods.color(format);

			e.setFormat(format);
			e.setMessage(message);
		}
		return true;
	}
}
