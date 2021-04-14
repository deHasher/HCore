package ru.dehasher.hcore.managers;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import ru.dehasher.hcore.HCore;

public class Files {
 
    private final JavaPlugin plugin;
    private final HashMap<String, Config> configs = new HashMap<>();
 
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

	public void reloadConfig(String name) {
		getConfig(name).reload();
	}

	public void setOldFile(String name) {
		Informer.send("File " + name + " is deprecated...");

		// Создаём дату.
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH-mm-ss ");

		// Объявляем файл.
        File old_file  = new File(plugin.getDataFolder(), name);

        // Создаём папку с бекапами, если такой нема.
        File backups = new File(plugin.getDataFolder(), Methods.fixSlashes("/backups/"));
        if (!backups.exists()) {
			Informer.send("Creating /backups/ folder...");
        	if (!backups.mkdir()) Informer.send("Failed to create folder /backups/");
		}

        // Получаем настоящее имя файла.
		String[] file = name.split(File.separator.replace("\\","\\\\"));
		String new_file = file[(file.length > 1) ? 1 : 0];

        // Переименовываем файл в папку.
        if (old_file.renameTo(new File(plugin.getDataFolder(), Methods.fixSlashes("/backups/" + formatter.format(date) + new_file)))) {
			Informer.send(Methods.fixSlashes("File moved to backups/" + formatter.format(date) + new_file));
		}
	}
 
	public class Config {

		private final String name;
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
				if (Objects.requireNonNull(config.getConfigurationSection("")).getKeys(true).size() != 0) {
					config.save(this.file);
				}
			} catch (IOException e) {
				e.printStackTrace();
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
				try {
					plugin.saveResource(this.name, false);
					return true;
				} catch (Exception e) {
					Informer.send("File " + this.name + " not found in " + plugin.getName() + ".jar.");
					setOldFile(HCore.main_name + ".yml");
					HCore.getPlugin().reloadFiles();
					return false;
				}
			} else if (file.exists() && state) {
				plugin.saveResource(this.name, true);
				return true;
			}
			return true;
		}

		public void reload() {
			if (file == null) {
                this.file = new File(plugin.getDataFolder(), this.name);
			}

			this.config = YamlConfiguration.loadConfiguration(file);

			try {
				Reader reader;
				reader = new InputStreamReader(plugin.getResource(this.name.replace("\\", "/")), StandardCharsets.UTF_8);
				YamlConfiguration cfg = YamlConfiguration.loadConfiguration(reader);
				this.config.setDefaults(cfg);
			} catch (NullPointerException ignored) {
				HCore.getPlugin().getLogger().info("Files error");
			}
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