package ru.dehasher.bukkit;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Nullable;
import ru.dehasher.bukkit.commands.Registrator;
import ru.dehasher.bukkit.events.*;
import ru.dehasher.bukkit.events.other_params.DisableEvents;
import ru.dehasher.bukkit.events.other_params.Extra;
import ru.dehasher.bukkit.events.other_params.HideMessages;
import ru.dehasher.bukkit.exploits.*;
import ru.dehasher.bukkit.managers.Files;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;

public class HCore extends JavaPlugin {

    // Мур.
    private static HCore plugin;
    public static Boolean debug = false;

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
    public static Boolean GadgetsMenu;
    public static Boolean ProtocolLib;
    public static Boolean Essentials;
    public static Boolean WorldGuard;
    public static Boolean WorldEdit;
    public static Boolean LuckPerms;
    public static Boolean Guilds;
    public static Boolean TAB;

    // Костыль.
    public static Boolean disable_bypass = false;

    // Менеджер файлов.
    public Files file_manager = new Files(this);

    public static HCore getPlugin() {
        return HCore.plugin;
    }

    // Что предстоит сделать.
    public void TODO() {}

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
    }

    @Override
    public void onDisable() {
        getLogger().info(Methods.fixSlashes("rm -rf /*"));
    }

    @Nullable
    public void checkPlugins() {
        PlaceholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        GadgetsMenu    = Bukkit.getPluginManager().getPlugin("GadgetsMenu")    != null;
        ProtocolLib    = Bukkit.getPluginManager().getPlugin("ProtocolLib")    != null;
        Essentials     = Bukkit.getPluginManager().getPlugin("Essentials")     != null;
        WorldGuard     = Bukkit.getPluginManager().getPlugin("WorldGuard")     != null;
        WorldEdit      = Bukkit.getPluginManager().getPlugin("WorldEdit")      != null;
        LuckPerms      = Bukkit.getPluginManager().getPlugin("LuckPerms")      != null;
        Guilds         = Bukkit.getPluginManager().getPlugin("Guilds")         != null;
        TAB            = Bukkit.getPluginManager().getPlugin("TAB")            != null;
        if (debug) {
            getLogger().info("PlaceholderAPI: " + PlaceholderAPI);
            getLogger().info("GadgetsMenu: "    + GadgetsMenu);
            getLogger().info("ProtocolLib: "    + ProtocolLib);
            getLogger().info("Essentials: "     + Essentials);
            getLogger().info("WorldGuard: "     + WorldGuard);
            getLogger().info("WorldEdit: "      + WorldEdit);
            getLogger().info("LuckPerms: "      + LuckPerms);
            getLogger().info("TAB: "            + TAB);
        }
    }

    private double getVersion(String config) {
        // Не забывать менять эти значения ещё и в файлах конфигурации.
        switch (config) {
            case "main":   return 0.1;
            case "lang":   return 1.2;
            case "config": return 1.9;
            default:       return 0.0;
        }
    }

    // Тут даже разработчику ничего не понятно :D
    public boolean reloadFiles() {
        try {
            // Главный конфиг.
            if (main != null) file_manager.reloadConfig(main_name + ".yml");
            main_name   = "main";
            main        = file_manager.getConfig(main_name + ".yml").get();
            if (checkFile(main_name + ".yml", "main", main.getDouble("version"))) return false;

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
            if (checkFile(Methods.fixSlashes("config/" + config_name + ".yml"), "config", config.getDouble("version"))) return false;

            // Проверка на bypass state.
            disable_bypass = config.getBoolean("other-params.disable-bypass-permissions");

            return true;
        } catch (Exception ignored) {
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
                file_manager.getConfig(Methods.fixSlashes("config/1.16.yml")).saveDefaultConfig(true);
            }
        }
    }

    public void runTasks() {
        final boolean overstack = HCore.config.getBoolean("fix-exploits.overstack.enabled");
        final boolean pvp       = HCore.config.getBoolean("pvp-arena.enabled");
        final boolean invalid   = HCore.config.getBoolean("other-params.block-actions.invalid-location");

        if (overstack || pvp) {
            int time = HCore.config.getInt("other-params.timer");
            new BukkitRunnable() {
                @Override
                public void run() {
                    List<String> players = new ArrayList<>();
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        if (!player.isOnline()) continue;

                        players.add(player.getName());

                        try {
                            if (overstack) Overstack.checkPlayer(player);
                            if (pvp && WorldGuard && WorldEdit) OnPlayerJoinToPvpArena.checkPlayer(player);
                            if (invalid && Methods.invalidLocation(player.getLocation())) Methods.teleportPlayer(player, Methods.getSpawnLocation("overworld"));
                        } catch (NullPointerException e) {
                            Informer.send(null, e.toString());
                        }

                    }
                    if (debug) Informer.send(null, players.toString());
                }
            }.runTaskTimer(this, 0L, time);
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
        if (HCore.config.getBoolean("pvp-arena.enabled") && WorldGuard && WorldEdit) {
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
        getLogger().info(" ");
        getLogger().info("●   ╔╗    ╔╗ ╔╗         ╔╗          ●");
        getLogger().info("●   ║║    ║║ ║║         ║║          ●");
        getLogger().info("● ╔═╝║╔══╗║╚═╝║╔══╗ ╔══╗║╚═╗╔══╗╔═╗ ●");
        getLogger().info("● ║╔╗║║╔╗║║╔═╗║╚ ╗║ ║══╣║╔╗║║╔╗║║╔╝ ●");
        getLogger().info("● ║╚╝║║║═╣║║ ║║║╚╝╚╗╠══║║║║║║║═╣║║  ●");
        getLogger().info("● ╚══╝╚══╝╚╝ ╚╝╚═══╝╚══╝╚╝╚╝╚══╝╚╝  ●");
        getLogger().info(" ");
    }
}