package ru.dehasher.hcore.managers;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import ru.dehasher.hcore.HCore;

public class Files {
 
    private final JavaPlugin plugin;
    private static String slash = File.separator;
    private HashMap<String, Config> configs = new HashMap<String, Config>();
 
    public Files(JavaPlugin plugin) {
        this.plugin = plugin;
    }

	public Config getConfig(String name) {
		if (!configs.containsKey(name)) {
			configs.put(name, new Config(name));
		}

		return configs.get(name);
	}

	public Config saveConfig(String name) {
		return getConfig(name).save();
	}

	public boolean reloadConfig(String name) {
		return getConfig(name).reload() ? true : false;
	}

	public void setOldFile(String name) {
		Informer.CONSOLE("File " + name + " is deprecated...");

		// Создаём дату.
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH-mm-ss ");

		// Объявляем файл.
        File file  = new File(plugin.getDataFolder(), name);

        // Проверяем есть ли папка backups.
        File check = new File(plugin.getDataFolder(), slash + "backups");
        if (!check.exists()) check.mkdirs();

        // Переименовываем файл в папку.
        file.renameTo(new File(plugin.getDataFolder(), slash + "backups" + slash + formatter.format(date) + name));

        Informer.CONSOLE("File moved to backups" + slash + formatter.format(date) + name);
	}
 
	public class Config {

		private String name;
		private File file;
		private YamlConfiguration config;
     
		public Config(String name) {
			this.name = name;
		}

		public Config save() {
			if ((this.config == null) || (this.file == null)) {
				return this;
			}
			try {
				if (config.getConfigurationSection("").getKeys(true).size() != 0) {
					config.save(this.file);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return this;
		}

		public YamlConfiguration get() {
			if (this.config == null) reload();

			return this.config;
		}

		public boolean saveDefaultConfig(boolean state) {
			file = new File(plugin.getDataFolder(), this.name);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				try {
					plugin.saveResource(this.name, false);
					return true;
				} catch (Exception e) {
					Informer.CONSOLE("File " + this.name + " not found in " + plugin.getName() + ".jar.");
					setOldFile(HCore.main_name + ".yml");
					reloadConfig(HCore.main_name + ".yml");
					return false;
				}
			} else if (file.exists() && state) {
				file.getParentFile().mkdirs();
				plugin.saveResource(this.name, true);
				return true;
			}
			return true;
		}

		public boolean reload() {
			if (file == null) {
                this.file = new File(plugin.getDataFolder(), this.name);
			}

			this.config = YamlConfiguration.loadConfiguration(file);

			Reader reader;
			try {
				reader = new InputStreamReader(plugin.getResource(this.name.replace("\\", "/")), "UTF8");
				if (reader != null) {
					YamlConfiguration cfg = YamlConfiguration.loadConfiguration(reader);
					this.config.setDefaults(cfg);
					return true;
				}
			} catch (Exception e) {}
			return false;
		}

		public Config copyDefaults(boolean force) {
			get().options().copyDefaults(force);
			return this;
		}

		public Config set(String key, Object value) {
			get().set(key, value);
			return this;
		}

		public Object get(String key) {
			return get().get(key);
		}
	}
}