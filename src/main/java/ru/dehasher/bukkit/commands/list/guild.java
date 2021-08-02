package ru.dehasher.bukkit.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.api.guilds.GAPI;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;
import ru.dehasher.bukkit.managers.Plugins;

public class guild {
    public static boolean send(CommandSender sender, String command, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Methods.checkPlugin(Plugins.Guilds)) {
                for (String guild : HCore.config.getString("guilds-fix.commands").split("\\|")) {
                    if (command.substring(1).equals(guild)) {
                        String playerRole = Methods.colorClear(GAPI.getRole(player)).toLowerCase();
                        Informer.send(player, HCore.config.getString("guilds-fix.prefix"));
                        for (String role : HCore.config.getConfigurationSection("guilds-fix.roles").getKeys(false)) {
                            Informer.send(player, HCore.config.getString("guilds-fix.roles." + role));
                            if (role.equals(playerRole)) return true;
                        }
                    }
                }
            } else {
                Informer.send(player, HCore.lang.getString("errors.no-plugin")
                        .replace("{plugin}", Plugins.Guilds)
                );
            }
        } else {
            Informer.send(HCore.lang.getString("errors.only-player"));
        }
        return false;
    }
}