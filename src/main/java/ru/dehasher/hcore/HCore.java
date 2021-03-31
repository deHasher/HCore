package ru.dehasher.hcore;

import java.io.File;
import java.lang.reflect.Field;

import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.FileConfiguration;
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

	public static String slash           = File.separator;
    public static Boolean disable_bypass = false;

    private static HCore plugin;

	public Files file_manager = new Files(this);

	public static HCore getPlugin() {
		return HCore.plugin;
	}

	public void TODO() {
//		Исправить звук ока эндера.
//		Сделать при входе и выходе в портал незера тп на спавны.
//		Сделать выборку целого блока settings при запросе к конфигу.
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

		// Грузим readme информацию.
		readme();

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
				return 0.2;
			case "lang":
				return 0.2;
		}
		return 0.0;
	}

	// Тут даже разработчику ничего не понятно :D
    public boolean reloadFiles() {
		try {
			if (main != null) file_manager.reloadConfig(main_name + ".yml");
			main_name   = "main";
			main        = file_manager.getConfig(main_name + ".yml").get();
			if (checkFile(main_name + ".yml", "major", main.getDouble("version"))) return false;

			if (spawn != null) file_manager.reloadConfig(spawn_name + ".yml");
			spawn_name  = "spawn";
			spawn       = file_manager.getConfig(spawn_name + ".yml").get();
			if (checkFile(spawn_name + ".yml", "spawn", null)) return false;



			if (lang != null) file_manager.reloadConfig("lang" + slash + lang_name + ".yml");
			lang_name   = main.getString("lang-file");
			lang        = file_manager.getConfig("lang" + slash + lang_name + ".yml").get();
			if (checkFile("lang" + slash + lang_name + ".yml", "lang", lang.getDouble("version"))) return false;

			if (config != null) file_manager.reloadConfig("config" + slash + config_name + ".yml");
			config_name = main.getString("config-file");
			config      = file_manager.getConfig("config" + slash + config_name + ".yml").get();
			if (checkFile("config" + slash + config_name + ".yml", "minor", config.getDouble("version"))) return false;


			// Проверка на bypass state.
			if (config.getBoolean("settings.other-params.disable-bypass-permissions")) disable_bypass = true;

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
		File config = new File(plugin.getDataFolder(), slash + "config");
		File lang   = new File(plugin.getDataFolder(), slash + "lang");
		if (!config.exists()) {
			if (config.mkdirs()) {
				file_manager.getConfig("config" + slash + "auth.yml").saveDefaultConfig(true);
				file_manager.getConfig("config" + slash + "hub.yml").saveDefaultConfig(true);
				file_manager.getConfig("config" + slash + "survival.yml").saveDefaultConfig(true);
			}
		}
		if (!config.exists()) {
			if (lang.mkdirs()) {
				file_manager.getConfig("lang" + slash + "ru_RU.yml").saveDefaultConfig(true);
				file_manager.getConfig("lang" + slash + "en_US.yml").saveDefaultConfig(true);
			}
		}
	}

	private void readme() {
		saveResource("readme" + slash + "permissions.txt", true);
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

    public void registerCommands() {
		try {
			final Field serverCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			serverCommandMap.setAccessible(true);

			CommandMap commandMap = (CommandMap) serverCommandMap.get(Bukkit.getServer());

			for (String command : config.getStringList("settings.send-command.plugin-commands")) {
				commandMap.register(command, new Registrator(command));
				Informer.send("The command /" + command + " successful registered!");
			}
		} catch (NoSuchFieldException | IllegalAccessException e) {
			Informer.send("An error occurred during initialization of the 'commandMap' class!");
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

        if (HCore.config.getBoolean("settings.chance-on-drop-items-after-death.enabled")) {
        	Bukkit.getPluginManager().registerEvents(new OnPlayerDeath(this), this);
        }
    	if (HCore.config.getBoolean("settings.batuts.enabled")) {
    		Bukkit.getPluginManager().registerEvents(new OnPlayerMove(this), this);
    	}
    	if (HCore.config.getBoolean("settings.cooldown-on-use-spawnegg.enabled")) {
    		Bukkit.getPluginManager().registerEvents(new OnPlayerUseSpawnegg(this), this);
    	}

        // Фиксы эксплойтов.
    	Bukkit.getPluginManager().registerEvents(new Dispenser(this), this);
        Bukkit.getPluginManager().registerEvents(new Items(this), this);
        Bukkit.getPluginManager().registerEvents(new Bed(this), this);
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