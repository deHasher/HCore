package ru.dehasher.bukkit;

import java.io.File;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.CommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ru.dehasher.bukkit.commands.Registrator;
import ru.dehasher.bukkit.events.*;
import ru.dehasher.bukkit.events.other_params.DisableEvents;
import ru.dehasher.bukkit.events.other_params.Extra;
import ru.dehasher.bukkit.events.other_params.HideMessages;
import ru.dehasher.bukkit.exploits.*;
import ru.dehasher.bukkit.managers.Files;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;
import ru.dehasher.bukkit.managers.Plugins;

public class HCore extends JavaPlugin {

    // Мур.
    private static HCore plugin;
    public static String version;
    public static String server_type;
    public static String config_name;
    public static String server_name          = "Unknown";
    public static final String main_file      = "main.yml";
    public static final String spawn_file     = "spawn.yml";
    public static final String lang_file      = "lang.yml";
    public static final Double main_version   = 0.2;
    public static final Double lang_version   = 1.51;
    public static final Double config_version = 2.01;

    // Конфигурации файлов.
    public static ConfigurationSection main;
    public static ConfigurationSection config;
    public static ConfigurationSection lang;
    public static ConfigurationSection spawn;

    // Менеджер файлов.
    public Files file_manager = new Files(this);

    public static HCore getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin  = this;
        version = getPlugin().getDescription().getVersion();

        // Выводим логотип.
        getLogo();

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
        Informer.send("rm -rf /*");
        if (server_name != null && config.getBoolean("other-params.api-notifications.enabled")) {
            Informer.url(config.getString("other-params.api-notifications.url.status"), new HashMap<String, String>(){{
                put("msg", "Сервер " + server_type + " #" + server_name + " остановлен.");
            }});
        }
    }

    // Тут даже разработчику ничего не понятно :D
    public boolean reloadFiles() {
        try {
            // Главный конфиг.
            if (main != null) file_manager.reloadConfig(main_file);
            main = file_manager.getConfig(main_file).get();
            if (checkFile(main_file, "main", main.getDouble("version"))) return false;

            // Сообщения.
            if (lang != null) file_manager.reloadConfig(lang_file);
            lang = file_manager.getConfig(lang_file).get().getConfigurationSection("messages");
            if (checkFile(lang_file, "lang", lang.getDouble("version"))) return false;

            // Точки спавнов.
            if (spawn != null) file_manager.reloadConfig(spawn_file);
            spawn = file_manager.getConfig(spawn_file).get().getConfigurationSection("locations");
            if (checkFile(spawn_file, "spawn", null)) return false;

            // Конфигурация.
            File config_folder = new File(plugin.getDataFolder(), "/config");
            if (!config_folder.exists()) {
                if (config_folder.mkdirs()) {
                    file_manager.getConfig("config/auth.yml").saveDefaultConfig(true);
                    file_manager.getConfig("config/hub.yml").saveDefaultConfig(true);
                    file_manager.getConfig("config/survival.yml").saveDefaultConfig(true);
                    file_manager.getConfig("config/1.16.yml").saveDefaultConfig(true);
                }
            }
            if (config != null) file_manager.reloadConfig("config/" + config_name + ".yml");
            config_name = main.getString("config-file");
            YamlConfiguration cfg = file_manager.getConfig("config/" + config_name + ".yml").get();
            server_type = cfg.getString("name");
            config = cfg.getConfigurationSection("settings");
            return !checkFile("config/" + config_name + ".yml", "config", config.getDouble("version"));

        } catch (Exception ignored) {}
        return false;
    }

    private boolean checkFile(String name, String type, Double version) {
        if (file_manager.getConfig(name).saveDefaultConfig(false)) { // Проверяю сам файл.
            if (version != null) {
                if (file_manager.getConfig(name).get().getDouble("version") < Methods.getConfigVersion(type)) { // Проверяю версию.
                    file_manager.setOldFile(name);
                    file_manager.getConfig(name).saveDefaultConfig(true);
                }
            }
            getLogger().info("File " + name + " successful loaded.");
            return false;
        } else return true;
    }

    public void runTasks() {
        final boolean overstack = config.getBoolean("fix-exploits.overstack.enabled");
        final boolean pvp       = config.getBoolean("pvp-arena.enabled");
        final boolean invalid   = config.getBoolean("other-params.block-actions.invalid-location");

        int time = config.getInt("other-params.timer");

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if (!player.isOnline()) continue;

                    try {
                        if (overstack) Overstack.checkPlayer(player);
                        if (pvp && Methods.checkPlugin(Plugins.WorldGuard) && Methods.checkPlugin(Plugins.WorldEdit)) OnPlayerJoinToPvpArena.checkPlayer(player);
                        if (invalid && Methods.invalidLocation(player.getLocation())) Methods.teleportPlayer(player, Methods.getSpawnLocation("overworld"));
                    } catch (Exception e) {
                        Informer.send(null, e.toString());
                    }
                }

            }
        }.runTaskTimer(this, 0L, time);
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
                    if (!aliases.isEmpty()) message = message + " Aliases: " + aliases;
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
        Bukkit.getPluginManager().registerEvents(new Extra(this), this);
        Bukkit.getPluginManager().registerEvents(new HideMessages(this), this);

        Bukkit.getPluginManager().registerEvents(new OnPlayerSendCommand(this), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerSendMessage(this), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerJoinServer(this), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerCombat(this), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerDeath(this), this);

        if (config.getBoolean("batuts.enabled")) {
            Bukkit.getPluginManager().registerEvents(new OnBatutJump(this), this);
        }
        if (config.getBoolean("cooldown-on-use-spawnegg.enabled")) {
            Bukkit.getPluginManager().registerEvents(new OnPlayerUseSpawnegg(this), this);
        }
        if (config.getBoolean("pvp-arena.enabled") && Methods.checkPlugin(Plugins.WorldGuard) && Methods.checkPlugin(Plugins.WorldEdit)) {
            Bukkit.getPluginManager().registerEvents(new OnPlayerJoinToPvpArena(this), this);
        }
        if (config.getBoolean("other-params.disable-events.enabled")) {
            Bukkit.getPluginManager().registerEvents(new DisableEvents(this), this);
        }

        // Фиксы эксплойтов.
        Bukkit.getPluginManager().registerEvents(new Dispenser(this), this);
        Bukkit.getPluginManager().registerEvents(new Items(this), this);
        Bukkit.getPluginManager().registerEvents(new Bed(this), this);
        Bukkit.getPluginManager().registerEvents(new Portals(this), this);
        Bukkit.getPluginManager().registerEvents(new Swap(this), this);
        Bukkit.getPluginManager().registerEvents(new Overstack(this), this);
        Bukkit.getPluginManager().registerEvents(new ChunkBan(this), this);

        // Вручную вызываем ивенты при запуске плагина.
        HideMessages.disableAchievements();
    }

    private void getLogo() {
        Informer.send(" ");
        Informer.send("●   ╔╗    ╔╗ ╔╗         ╔╗          ●");
        Informer.send("●   ║║    ║║ ║║         ║║          ●");
        Informer.send("● ╔═╝║╔══╗║╚═╝║╔══╗ ╔══╗║╚═╗╔══╗╔═╗ ●");
        Informer.send("● ║╔╗║║╔╗║║╔═╗║╚ ╗║ ║══╣║╔╗║║╔╗║║╔╝ ●");
        Informer.send("● ║╚╝║║║═╣║║ ║║║╚╝╚╗╠══║║║║║║║═╣║║  ●");
        Informer.send("● ╚══╝╚══╝╚╝ ╚╝╚═══╝╚══╝╚╝╚╝╚══╝╚╝  ●");
        Informer.send(" ");
    }
}