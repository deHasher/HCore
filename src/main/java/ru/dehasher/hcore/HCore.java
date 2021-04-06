package ru.dehasher.hcore;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.command.CommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ru.dehasher.hcore.commands.Registrator;
import ru.dehasher.hcore.events.OnPlayerDeath;
import ru.dehasher.hcore.events.OnPlayerJoinServer;
import ru.dehasher.hcore.events.OnPlayerMove;
import ru.dehasher.hcore.events.OnPlayerSendCommand;
import ru.dehasher.hcore.events.OnPlayerSendMessage;
import ru.dehasher.hcore.events.OnPlayerUseSpawnegg;
import ru.dehasher.hcore.events.other_params.DisableEvents;
import ru.dehasher.hcore.events.other_params.Extra;
import ru.dehasher.hcore.events.other_params.HideMessages;
import ru.dehasher.hcore.exploits.*;
import ru.dehasher.hcore.managers.Files;
import ru.dehasher.hcore.managers.Informer;
import ru.dehasher.hcore.managers.Methods;

import javax.annotation.Nullable;

public class HCore extends JavaPlugin {

	// Мур.
	private static HCore plugin;

	// Конфигурации файлов.
	public static ConfigurationSection main;
	public static ConfigurationSection config;
	public static ConfigurationSection lang;
	public static ConfigurationSection spawn;

	// Названия файлов.
	public static String config_name;
	public static String lang_name;
	public static String main_name;
	public static String spawn_name;

	// Плагины.
	public static Boolean PlaceholderAPI;
	public static Boolean ProtocolLib;

	// Костыль.
    public static Boolean disable_bypass = false;

    // Менеджер файлов.
	public Files file_manager = new Files(this);

	public static HCore getPlugin() {
		return HCore.plugin;
	}

	public void TODO() {
		// Исправить звук ока эндера.
	}

    @Override
    public void onEnable() {
    	HCore.plugin = HCore.this;

		// Выводим логотип.
		getLogo();

    	// Проверка плагинов.
    	checkPlugins();

    	/* 
    	* Проверяем наличие папок /lang/, /config/ и т.д. в папке плагина.
    	* При отсутствии папок создаём их и добавляем в них ВСЕ файлы.
    	*/
    	checkFolders();

		// Шабим.
		reloadFiles();

        // Регистрируем все эвенты.
        registerEvents();

        // Генерируем команды.
        registerCommands();

        // Запускаем автоматические задачи.
        runTasks();

//		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
//		pm.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.WORLD_EVENT) {
//			@Override
//			public void onPacketSending(PacketEvent e) {
//				PacketContainer packet = e.getPacket();
//				Player player = e.getPlayer();
//				int actionID = packet.getIntegers().read(0);
//
//				if (actionID == 1038) {
//					Informer.send(player, "Убран звук создания портала в ЭНД.");
//					Informer.send(player, packet.);
//					e.setCancelled(true);
//				}
//			}
//		});

				// Для того, чтобы контролировать отправляемые сервером пакеты.
//				@Override
//				public void onPacketSending(PacketEvent e) {
//					PacketContainer packet = e.getPacket();
//					Player player          = e.getPlayer();
//					List<Sound> sounds = packet.getSoundEffects().getValues();
//					for (Sound sound : sounds) {
//						Informer.send(sound.name());
//					}
//					Informer.send(player, 123);
//					player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 500.0f, 1.0f);
//					if (ProtocolLibrary.getProtocolManager().getProtocolVersion(e.getPlayer()) <= 47) {
//						if (soundName("random.levelup")) {
//							e.setCancelled(true);
//						} else if (ProtocolLibrary.getProtocolManager().getProtocolVersion(e.getPlayer()) >= 107) {
//							if (soundName.equalsIgnoreCase("entity.player.levelup")) {
//								e.setCancelled(true);
//							}
//						}
//					}
//				}
//			}
//		);
    }

    @Override
    public void onDisable() {
    	getLogger().info(Methods.fixSlashes("rm -rf /*"));
    }

    @Nullable
    public void checkPlugins() {
		PlaceholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
		ProtocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib") != null;
	}

	private double getVersion(String config) {
		// Не забывать менять эти значения ещё и в файлах конфигурации.
		switch (config) {
			case "major":
				return 0.1;
			case "minor":
				return 0.4;
			case "lang":
				return 0.4;
		}
		return 0.0;
	}

	// Тут даже разработчику ничего не понятно :D
    public boolean reloadFiles() {
		try {
			// Главный конфиг.
			if (main != null) file_manager.reloadConfig(main_name + ".yml");
			main_name   = "main";
			main        = file_manager.getConfig(main_name + ".yml").get();
			if (checkFile(main_name + ".yml", "major", main.getDouble("version"))) return false;

			// Перевод.
			if (lang != null) file_manager.reloadConfig(Methods.fixSlashes("lang/" + lang_name + ".yml"));
			lang_name   = main.getString("lang-file");
			lang        = file_manager.getConfig(Methods.fixSlashes("lang/" + lang_name + ".yml")).get().getConfigurationSection("messages");
			if (checkFile(Methods.fixSlashes("lang/" + lang_name + ".yml"), "lang", lang.getDouble("version"))) return false;

			// Точки спавнов.
			if (spawn != null) file_manager.reloadConfig(spawn_name + ".yml");
			spawn_name  = "spawn";
			spawn       = file_manager.getConfig(spawn_name + ".yml").get().getConfigurationSection("locations");
			if (checkFile(spawn_name + ".yml", "spawn", null)) return false;

			// Конфигурация.
			if (config != null) file_manager.reloadConfig(Methods.fixSlashes("config/" + config_name + ".yml"));
			config_name = main.getString("config-file");
			config      = file_manager.getConfig(Methods.fixSlashes("config/" + config_name + ".yml")).get().getConfigurationSection("settings");
			if (checkFile(Methods.fixSlashes("config/" + config_name + ".yml"), "minor", config.getDouble("version"))) return false;

			// Проверка на bypass state.
			disable_bypass = config.getBoolean("other-params.disable-bypass-permissions");

			return true;
		} catch (Exception e) {
			return false;
		}
    }

    private boolean checkFile(String name, String type, Double version) {
		if (file_manager.getConfig(name).saveDefaultConfig(false)) { // Проверяю сам файл.
			if (version != null) {
				if (file_manager.getConfig(name).get().getDouble("version") < getVersion(type)) { // Проверяю версию.
					file_manager.setOldFile(name);
					file_manager.getConfig(name).saveDefaultConfig(true);
				}
			}
			getLogger().info("File " + name + " successful loaded.");
			return false;
		} else return true;
    }

	private void checkFolders() {
		File lang   = new File(plugin.getDataFolder(), Methods.fixSlashes("/lang"));
		File config = new File(plugin.getDataFolder(), Methods.fixSlashes("/config"));
		if (!config.exists()) {
			if (lang.mkdirs()) {
				file_manager.getConfig(Methods.fixSlashes("lang/ru_RU.yml")).saveDefaultConfig(true);
				file_manager.getConfig(Methods.fixSlashes("lang/en_US.yml")).saveDefaultConfig(true);
			}
		}
		if (!config.exists()) {
			if (config.mkdirs()) {
				file_manager.getConfig(Methods.fixSlashes("config/auth.yml")).saveDefaultConfig(true);
				file_manager.getConfig(Methods.fixSlashes("config/hub.yml")).saveDefaultConfig(true);
				file_manager.getConfig(Methods.fixSlashes("config/survival.yml")).saveDefaultConfig(true);
			}
		}
	}

	public void runTasks() {
		if (HCore.config.getBoolean("join-server.custom-health.enabled")) {
	        new BukkitRunnable() {
	        	@Override
	            public void run() {
					for (Player player : Bukkit.getServer().getOnlinePlayers()) {
						Methods.editHealth(player, false);
					}
	            }
	        }.runTaskTimer(plugin, 0L, 8L);
		}

		if (HCore.config.getBoolean("fix-exploits.overstack.enabled")) {
			int time = HCore.config.getInt("fix-exploits.overstack.time");
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Player player : Bukkit.getServer().getOnlinePlayers()) {
						Overstack.checkPlayer(player);
					}
				}
			}.runTaskTimer(plugin, time, time);
		}
	}

    public boolean registerCommands() {
		try {
			final Field serverCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			serverCommandMap.setAccessible(true);

			CommandMap commandMap = (CommandMap) serverCommandMap.get(Bukkit.getServer());

			ConfigurationSection commands = config.getConfigurationSection("send-command.plugin.commands");
			if (commands != null) {
				for (String command : commands.getKeys(false)){
					ConfigurationSection info = commands.getConfigurationSection(command);
					List<String> aliases      = info.getStringList("aliases");
					commandMap.register(command, new Registrator(command, aliases));
					String message = "Command /" + command + " successful registered!";
					if (!aliases.isEmpty()) message = message + " Aliases: " + aliases.toString();
					Informer.send(message);
				}
			}
			return true;
		} catch (NoSuchFieldException | IllegalAccessException e) {
			Informer.send("An error occurred during initialization of the 'commandMap' class!");
			return false;
		}
    }

    private void registerEvents() {
    	// Ивенты.
        Bukkit.getPluginManager().registerEvents(new DisableEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new Extra(this), this);
        Bukkit.getPluginManager().registerEvents(new HideMessages(this), this);

        Bukkit.getPluginManager().registerEvents(new OnPlayerJoinServer(this), this);

        Bukkit.getPluginManager().registerEvents(new OnPlayerSendCommand(this), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerSendMessage(this), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerDeath(this), this);

    	if (HCore.config.getBoolean("batuts.enabled")) {
    		Bukkit.getPluginManager().registerEvents(new OnPlayerMove(this), this);
    	}
    	if (HCore.config.getBoolean("cooldown-on-use-spawnegg.enabled")) {
    		Bukkit.getPluginManager().registerEvents(new OnPlayerUseSpawnegg(this), this);
    	}

        // Фиксы эксплойтов.
    	Bukkit.getPluginManager().registerEvents(new Dispenser(this), this);
        Bukkit.getPluginManager().registerEvents(new Items(this), this);
		Bukkit.getPluginManager().registerEvents(new Bed(this), this);
		Bukkit.getPluginManager().registerEvents(new Portals(this), this);
        Bukkit.getPluginManager().registerEvents(new Swap(this), this);
        Bukkit.getPluginManager().registerEvents(new Overstack(this), this);
    }

    private void getLogo() {
    	getLogger().info("");
    	getLogger().info("●   ╔╗    ╔╗ ╔╗         ╔╗          ●");
    	getLogger().info("●   ║║    ║║ ║║         ║║          ●");
    	getLogger().info("● ╔═╝║╔══╗║╚═╝║╔══╗ ╔══╗║╚═╗╔══╗╔═╗ ●");
    	getLogger().info("● ║╔╗║║╔╗║║╔═╗║╚ ╗║ ║══╣║╔╗║║╔╗║║╔╝ ●");
    	getLogger().info("● ║╚╝║║║═╣║║ ║║║╚╝╚╗╠══║║║║║║║═╣║║  ●");
    	getLogger().info("● ╚══╝╚══╝╚╝ ╚╝╚═══╝╚══╝╚╝╚╝╚══╝╚╝  ●");
    	getLogger().info("");
    }
}