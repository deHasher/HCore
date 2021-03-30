package ru.dehasher.hcore;

import java.io.File;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import ru.dehasher.hcore.commands.hreload;
import ru.dehasher.hcore.commands.setspawn;
import ru.dehasher.hcore.commands.spawn;
import ru.dehasher.hcore.events.OnPlayerDeath;
import ru.dehasher.hcore.events.OnPlayerJoinServer;
import ru.dehasher.hcore.events.OnPlayerMove;
import ru.dehasher.hcore.events.OnPlayerSendCommand;
import ru.dehasher.hcore.events.OnPlayerSendMessage;
import ru.dehasher.hcore.events.OnPlayerUseSpawnegg;
import ru.dehasher.hcore.events.other_events.Default;
import ru.dehasher.hcore.events.other_events.Extra;
import ru.dehasher.hcore.events.other_events.HideMessages;
import ru.dehasher.hcore.exploits.Bed;
import ru.dehasher.hcore.exploits.Dispenser;
import ru.dehasher.hcore.exploits.Items;
import ru.dehasher.hcore.exploits.Overstack;
import ru.dehasher.hcore.exploits.Swap;
import ru.dehasher.hcore.managers.Files;
import ru.dehasher.hcore.managers.Informer;
import ru.dehasher.hcore.managers.Methods;

public class HCore extends JavaPlugin {

	public static FileConfiguration main;
	public static FileConfiguration config;
	public static FileConfiguration lang;
	public static FileConfiguration spawn;

	public static String config_name;
	public static String lang_name;

	public static String main_name;
	public static String spawn_name;

	public static String slash = File.separator;

	private static HCore plugin;

	public Files file_manager = new Files(this);

	public static HCore getPlugin() {
		return HCore.plugin;
	}

	public void TODO() {
//		Сделать registerCommands() что б он создавал собственные команды и они не брались из plugin.yml.
//		Проверять команды на рекламу.
//		Выяснить почему когда происходит создание файлов - не загружаются заполнители.
//
//		Пофиксить: Это если в main.yml задать несуществующий файл.
//		[HCore] File main.yml successful loaded.
//		[HCore] File spawn.yml successful loaded.
//		[HCore] File config\survival1.yml not found in HCoreReforged.jar.
//		[HCore] File main.yml is deprecated...
//		[HCore] File moved to backups\29.03.2021 18-41-02 main.yml
//		[HCore] File main.yml successful reloaded.
//		[HCore] File config\survival1.yml is deprecated...
//		[HCore] File moved to backups\29.03.2021 18-41-02 config\survival1.yml
//		[HCore] File config\survival1.yml not found in HCoreReforged.jar.
//		[HCore] File main.yml is deprecated...
//		[HCore] File moved to backups\29.03.2021 18-41-02 main.yml
//		[HCore] File main.yml successful reloaded.
//		[HCore] File config\survival1.yml successful loaded.
//		[HCore] File lang\ru_RU.yml successful loaded.
	}

    @Override
    public void onEnable() {
    	HCore.plugin = HCore.this;

    	// Выводим логотип.
    	getLogo();

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
    }

    @Override
    public void onDisable() {
    	getLogger().info("rm -rf " + slash + "*");
    }

	private double getVersion(String config) {
		// Не забывать менять эти значения ещё и в файлах конфигурации.
		switch (config) {
			case "major":
				return 0.1;
			case "minor":
				return 0.1;
			case "lang":
				return 0.1;
		}
		return 0.0;
	}

	// Тут даже разработчику ничего не понятно :D
    public boolean reloadFiles() {
    	boolean error = false;
    	if (main != null) {
    		if (!file_manager.reloadConfig(main_name + ".yml")) error = true;
    	}
		main_name   = "main";
    	main        = file_manager.getConfig(main_name + ".yml").get();
		checkFile(main_name + ".yml", "major", main.getDouble("version"));

		if (spawn != null) {
			if (!file_manager.reloadConfig(spawn_name + ".yml")) error = true;
		}
		spawn_name  = "spawn";
		spawn       = file_manager.getConfig(spawn_name + ".yml").get();
		checkFile(spawn_name + ".yml", "spawn", null);

		if (config != null) {
			if (!file_manager.reloadConfig("config" + slash + config_name + ".yml")) error = true;
		}
		config_name = main.getString("config-file");
		config      = file_manager.getConfig("config" + slash + config_name + ".yml").get();
		checkFile("config" + slash + config_name + ".yml", "minor", config.getDouble("version"));

		if (lang != null) {
			if (!file_manager.reloadConfig("lang" + slash + lang_name + ".yml")) error = true;
		}
		lang_name   = main.getString("lang-file");
		lang        = file_manager.getConfig("lang" + slash + lang_name + ".yml").get();
		checkFile("lang" + slash + lang_name + ".yml", "lang", lang.getDouble("version"));

		return error ? false : true;
    }

    private void checkFile(String name, String type, Double version) {
		if (file_manager.getConfig(name).saveDefaultConfig(false)) {
			if (version != null) {
				if (file_manager.getConfig(name).get().getDouble("version") != getVersion(type)) {
					file_manager.setOldFile(name);
					file_manager.getConfig(name).saveDefaultConfig(true);
				}
			}
			getLogger().info("File " + name + " successful loaded.");
		} else {
			getLogger().info("An error occurred while loading the " + name + " file.");
		}
    }

    private boolean checkFolders() {
    	File config  = new File(plugin.getDataFolder(), slash + "config");
    	File lang    = new File(plugin.getDataFolder(), slash + "lang");
    	if (!config.exists()) {
    		config.mkdirs();
    		file_manager.getConfig("config" + slash + "auth.yml"    ).saveDefaultConfig(true);
    		file_manager.getConfig("config" + slash + "hub.yml"     ).saveDefaultConfig(true);
    		file_manager.getConfig("config" + slash + "survival.yml").saveDefaultConfig(true);
    	}
    	if (!config.exists()) {
    		lang.mkdirs();
    		file_manager.getConfig("lang"   + slash + "ru_RU.yml"   ).saveDefaultConfig(true);
    		file_manager.getConfig("lang  " + slash + "en_US.yml"   ).saveDefaultConfig(true);
    	}
    	return false;
    }

	public void runTasks() {
		if (HCore.config.getBoolean("settings.join-server.custom-health.enabled")) {
	        new BukkitRunnable() {
	        	@Override
	            public void run() {
	            	for (Player player : Bukkit.getServer().getOnlinePlayers()) {
	            		Methods.editHealth(player, false);
	            	}
	            }
	        }.runTaskTimer(plugin, 0L, 8L);
		}
		if (HCore.config.getBoolean("settings.fix-exploits.overstack.enabled")) {
			int time = HCore.config.getInt("settings.fix-exploits.overstack.time");
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

    private void registerCommands() {
    	if (getCommand("spawn") != null) {
    		getCommand("spawn").setExecutor((CommandExecutor)new spawn(this));
    		Informer.CONSOLE("Command /spawn successful registered.");
    	}
    	if (getCommand("setspawn") != null) {
    		getCommand("setspawn").setExecutor((CommandExecutor)new setspawn(this));
    		Informer.CONSOLE("Command /setspawn successful registered.");
    	}
    	if (getCommand("hreload") != null) {
    		getCommand("hreload").setExecutor((CommandExecutor)new hreload(this));
    		Informer.CONSOLE("Command /hreload successful registered.");
    	}
    }

    private void registerEvents() {
    	// Ивенты.
        Bukkit.getPluginManager().registerEvents((Listener)new Default(this), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new Extra(this), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new HideMessages(this), (Plugin)this);

        Bukkit.getPluginManager().registerEvents((Listener)new OnPlayerJoinServer(this), (Plugin)this);

        Bukkit.getPluginManager().registerEvents((Listener)new OnPlayerSendCommand(this), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new OnPlayerSendMessage(this), (Plugin)this);

        if (HCore.config.getBoolean("settings.chance-on-drop-items-after-death.enabled")) {
        	Bukkit.getPluginManager().registerEvents((Listener)new OnPlayerDeath(this), (Plugin)this);
        }
    	if (HCore.config.getBoolean("settings.batuts.enabled")) {
    		Bukkit.getPluginManager().registerEvents((Listener)new OnPlayerMove(this), (Plugin)this);
    	}
    	if (HCore.config.getBoolean("settings.cooldown-on-use-spawnegg.enabled")) {
    		Bukkit.getPluginManager().registerEvents((Listener)new OnPlayerUseSpawnegg(this), (Plugin)this);
    	}

        // Фиксы эксплойтов.
    	Bukkit.getPluginManager().registerEvents((Listener)new Dispenser(this), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new Items(this), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new Bed(this), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new Swap(this), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new Overstack(this), (Plugin)this);
    }

    private void getLogo() {
    	getLogger().info("");
    	getLogger().info("●   ╔╗    ╔╗ ╔╗         ╔╗          ●");
    	getLogger().info("●   ║║    ║║ ║║         ║║          ●");
    	getLogger().info("● ╔═╝║╔══╗║╚═╝║╔══╗ ╔══╗║╚═╗╔══╗╔═╗ ●");
    	getLogger().info("● ║╔╗║║╔╗║║╔═╗║╚ ╗║ ║══╣║╔╗║║╔╗║║╔╝ ●");
    	getLogger().info("● ║╚╝║║║═╣║║ ║║║╚╝╚╗╠══║║║║║║║═╣║║  ●");
    	getLogger().info("● ╚══╝╚══╝╚╝ ╚╝╚═══╝╚══╝╚╝╚╝╚══╝╚╝  ●");
    	getLogger().info("4");
    }
}