package ru.dehasher.bukkit.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Nullable;
import ru.dehasher.bukkit.HCore;
import ru.dehasher.bukkit.api.luckperms.LPAPI;
import ru.dehasher.bukkit.api.tab.TAPI;
import ru.dehasher.bukkit.managers.Informer;
import ru.dehasher.bukkit.managers.Methods;
import ru.dehasher.bukkit.managers.Plugins;

public class prefix {
    public static boolean send(CommandSender sender, String command, String[] args) {
        String[] message  = get(args).split(" ", 4);
        Player player     = null;
        String playerName = HCore.lang.getString("console-name");
        String info       = Methods.getCommandInfo(command);
        int length        = message.length;
        int max_chars     = HCore.config.getInt("send-command.plugin.commands.prefix.data.max-limit");

        if (sender instanceof Player) {
            player     = (Player) sender;
            playerName = player.getName();
        }

        if (!Methods.isPerm(player, "hcore.command.prefix")) {
            Informer.send(player, HCore.lang.getString("errors.no-perm"));
            return false;
        }

        if (length > 2) {
            String type    = message[1].toLowerCase();
            Player target  = HCore.getPlugin().getServer().getPlayer(message[2]);

            if (target == null || !target.isOnline()) {
                Informer.send(player, HCore.lang.getString("errors.player-not-found"));
                return false;
            }

            switch (type) {
                case "clear":
                    if (Methods.checkPlugin(Plugins.LuckPerms)) {
                        LPAPI.reset(target, "prefix");
                        LPAPI.reset(target, "suffix");
                    } else {
                        Informer.send(player, HCore.lang.getString("errors.no-plugin").replace("{plugin}", "LuckPerms"));
                        return false;
                    }
                    if (Methods.checkPlugin(Plugins.TAB)) {
                        TAPI.setPrefix(target, "");
                        TAPI.setSuffix(target, "");
                    } else {
                        Informer.send(player, HCore.lang.getString("errors.no-plugin").replace("{plugin}", "TAB"));
                        return false;
                    }
                    Informer.send(player, HCore.lang.getString("commands.prefix.clear.self").replace("{player}", target.getName()));
                    if (target != player) Informer.send(target, HCore.lang.getString("commands.prefix.clear.target").replace("{player}", playerName));
                    return true;
                case "tab":
                case "chat":
                    if (length > 3) {
                        String prefix = Methods.colorSet(fix(message[3]));
                        String suffix = "";

                        if (message[3].contains("<name>")) {
                            String[] msg = message[3].split("<name>", 2);
                            prefix = Methods.colorSet(fix(msg[0]));
                            if (msg.length > 1) suffix = Methods.colorSet(fix(msg[1]));
                        }

                        String prefixSpace = (Methods.colorClear(prefix).length() > 0 && !Methods.colorClear(prefix).endsWith(" "))   ? " " : "";
                        String suffixSpace = (Methods.colorClear(suffix).length() > 0 && !Methods.colorClear(suffix).startsWith(" ")) ? " " : "";

                        if (Methods.colorClear(prefix).length() > max_chars) {
                            Informer.send(player, HCore.lang.getString("commands.prefix.error").replace("{max}", "" + max_chars));
                            return false;
                        }

                        if (Methods.colorClear(suffix).length() > max_chars) {
                            Informer.send(player, HCore.lang.getString("commands.prefix.error").replace("{max}", "" + max_chars));
                            return false;
                        }

                        if (type.equals("chat")) {
                            if (Methods.checkPlugin("LuckPerms")) {
                                LPAPI.setPrefix(target, prefix + prefixSpace);
                                LPAPI.setSuffix(target, suffixSpace + suffix);
                            } else {
                                Informer.send(player, HCore.lang.getString("errors.no-plugin").replace("{plugin}", "LuckPerms"));
                                return false;
                            }
                        } else {
                            if (Methods.checkPlugin("TAB")) {
                                TAPI.setPrefix(target, prefix + prefixSpace);
                                TAPI.setSuffix(target, suffixSpace + suffix);
                            } else {
                                Informer.send(player, HCore.lang.getString("errors.no-plugin").replace("{plugin}", "TAB"));
                                return false;
                            }
                        }
                        Informer.send(player, HCore.lang.getString("commands.prefix.success.self")
                                .replace("{type}", HCore.lang.getString("commands.prefix.types." + type))
                                .replace("{player}", target.getName())
                                .replace("{prefix}", prefix + prefixSpace + target.getName() + suffixSpace + suffix)
                        );
                        if (target != player) {
                            Informer.send(target, HCore.lang.getString("commands.prefix.success.target")
                                    .replace("{type}", HCore.lang.getString("commands.prefix.types." + type))
                                    .replace("{player}", playerName)
                                    .replace("{prefix}", prefix + prefixSpace + target.getName() + suffixSpace + suffix)
                            );
                        }
                        return true;
                    }
            }
        }
        Informer.send(player, info);
        return false;
    }

    public static String fix(@Nullable String string) {
        if (string == null) return "";
        string = string
                .replace("\\", "")
                .replace("%", "")
                .replace("???", "")
                .replace("*", "");
        return string;
    }

    public static String get(String[] strings) {
        StringBuilder message = new StringBuilder();
        for (String string : strings) {
            message.append(" ").append(string);
        }
        return message.toString();
    }
}