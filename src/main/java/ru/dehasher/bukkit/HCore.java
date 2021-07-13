package ru.dehasher.bukkit;

import java.io.File;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
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
    public static String server_name;
    public static String server_type;
    public static String main_file       = "main.yml";
    public static String spawn_file      = "spawn.yml";
    public static Double main_version    = 0.1;
    public static Double lang_version    = 1.42;
    public static Double config_version  = 1.97;
    public static Boolean disable_bypass = false;

    // Конфигурации файлов.
    public static ConfigurationSection main;
    public static ConfigurationSection config;
    public static ConfigurationSection lang;
    public static ConfigurationSection spawn;

    // Названия файлов.
    public static String config_name;
    public static String lang_name;

    // Менеджер файлов.
    public Files file_manager = new Files(this);

    public static HCore getPlugin() {
        return HCore.plugin;
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
        Informer.send("rm -rf /*");
        if (HCore.server_name != null && HCore.config.getBoolean("other-params.api-notifications.enabled")) {
            Informer.url(HCore.config.getString("other-params.api-notifications.url.status"), new HashMap<String, String>(){{put("msg", "Сервер " + HCore.server_type + " #{server} остановлен.");}});
        }
    }

    // Тут даже разработчику ничего не понятно :D
    public boolean reloadFiles() {
        try {
            // Главный конфиг.
            if (main != null) file_manager.reloadConfig(main_file);
            main = file_manager.getConfig(main_file).get();
            if (checkFile(main_file, "main", main.getDouble("version"))) return false;

            // Перевод.
            if (lang != null) file_manager.reloadConfig("lang/" + lang_name + ".yml");
            lang_name = main.getString("lang-file");
            lang = file_manager.getConfig("lang/" + lang_name + ".yml").get().getConfigurationSection("messages");
            if (checkFile("lang/" + lang_name + ".yml", "lang", lang.getDouble("version"))) return false;

            // Точки спавнов.
            if (spawn != null) file_manager.reloadConfig(spawn_file);
            spawn = file_manager.getConfig(spawn_file).get().getConfigurationSection("locations");
            if (checkFile(spawn_file, "spawn", null)) return false;

            // Конфигурация.
            if (config != null) file_manager.reloadConfig("config/" + config_name + ".yml");
            config_name = main.getString("config-file");
            YamlConfiguration cfg = file_manager.getConfig("config/" + config_name + ".yml").get();
            server_type = cfg.getString("name");
            config = cfg.getConfigurationSection("settings");
            if (checkFile("config/" + config_name + ".yml", "config", config.getDouble("version"))) return false;

            // Проверка на bypass state.
            disable_bypass = config.getBoolean("other-params.disable-bypass-permissions");

            return true;
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

    private void checkFolders() {
        File lang   = new File(plugin.getDataFolder(), "/lang");
        File config = new File(plugin.getDataFolder(), "/config");
        if (!config.exists()) {
            if (lang.mkdirs()) {
                file_manager.getConfig("lang/ru_RU.yml").saveDefaultConfig(true);
                file_manager.getConfig("lang/en_US.yml").saveDefaultConfig(true);
            }
        }
        if (!config.exists()) {
            if (config.mkdirs()) {
                file_manager.getConfig("config/auth.yml").saveDefaultConfig(true);
                file_manager.getConfig("config/hub.yml").saveDefaultConfig(true);
                file_manager.getConfig("config/survival.yml").saveDefaultConfig(true);
                file_manager.getConfig("config/1.16.yml").saveDefaultConfig(true);
            }
        }
    }

    public void runTasks() {
        final boolean overstack = HCore.config.getBoolean("fix-exploits.overstack.enabled");
        final boolean pvp       = HCore.config.getBoolean("pvp-arena.enabled");
        final boolean invalid   = HCore.config.getBoolean("other-params.block-actions.invalid-location");

        int time = HCore.config.getInt("other-params.timer");
        new BukkitRunnable() {
            @Override
            public void run() {
                // Отправка состояния ЦП на апи.
                if (HCore.config.getBoolean("other-params.api-notifications.enabled")) {
                    OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                    double SystemCpuLoad = bean.getSystemCpuLoad();
                    if (SystemCpuLoad != -1) {
                        long cpu = Math.round(SystemCpuLoad * 100);
                        Informer.url(HCore.config.getString("other-params.api-notifications.url.cpu"), new HashMap<String, String>() {{put("data", "" + cpu);}});
                    }
                }

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

        if (HCore.config.getBoolean("batuts.enabled")) {
            Bukkit.getPluginManager().registerEvents(new OnBatutJump(this), this);
        }
        if (HCore.config.getBoolean("cooldown-on-use-spawnegg.enabled")) {
            Bukkit.getPluginManager().registerEvents(new OnPlayerUseSpawnegg(this), this);
        }
        if (HCore.config.getBoolean("pvp-arena.enabled") && Methods.checkPlugin(Plugins.WorldGuard) && Methods.checkPlugin(Plugins.WorldEdit)) {
            Bukkit.getPluginManager().registerEvents(new OnPlayerJoinToPvpArena(this), this);
        }
        if (HCore.config.getBoolean("other-params.disable-events.enabled")) {
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