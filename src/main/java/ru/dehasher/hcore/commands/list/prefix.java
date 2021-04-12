package ru.dehasher.hcore.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.api.luckperms.LPAPI;
import ru.dehasher.hcore.api.tab.TAPI;
import ru.dehasher.hcore.managers.Informer;
import ru.dehasher.hcore.managers.Methods;

import javax.annotation.Nullable;

public class prefix {
    public static boolean send(CommandSender sender, String command, String[] args) {
        String[] message  = get(args).split(" ", 4);
        Player player     = null;
        String playerName = HCore.lang.getString("console-name");
        String info       = Methods.getCommandInfo(command);
        int    length     = message.length;
        int max_chars     = HCore.config.getInt("send-command.plugin.commands.prefix.data.max-limit");


        if (sender instanceof Player) {
            player = (Player) sender;
            playerName = player.getName();
        }

        if (Methods.isPerm(player, "hcore.command.prefix")) {
            if (length > 2) {

                String type    = message[1].toLowerCase();
                Player target  = HCore.getPlugin().getServer().getPlayer(message[2]);

                if (target != null && target.isOnline()) {
                    if (type.equals("clear")) {
                        if (HCore.LuckPerms) {
                            LPAPI.reset(target, "prefix");
                            LPAPI.reset(target, "suffix");
                        } else {
                            Informer.send(player, HCore.lang.getString("errors.no-plugin")
                                    .replace("{plugin}", "LuckPerms")
                            );
                            return false;
                        }
                        if (HCore.TAB) {
                            TAPI.setPrefix(target, "");
                            TAPI.setSuffix(target, "");
                        } else {
                            Informer.send(player, HCore.lang.getString("errors.no-plugin")
                                    .replace("{plugin}", "TAB")
                            );
                            return false;
                        }
                        Informer.send(player, HCore.lang.getString("commands.prefix.clear.self")
                                .replace("{player}", target.getName())
                        );
                        if (target != player) {
                            Informer.send(target, HCore.lang.getString("commands.prefix.clear.target")
                                    .replace("{player}", playerName)
                            );
                        }
                        return true;
                    } else if (type.equals("chat") || type.equals("tab")) {
                        if (length > 3) {
                            String prefix = Methods.color(fix(message[3]));
                            String suffix = "";

                            if (message[3].contains("<name>")) {
                                String[] msg = message[3].split("<name>", 2);
                                prefix = Methods.color(fix(msg[0]));
                                if (msg.length > 1) {
                                    suffix = Methods.color(fix(msg[1]));
                                }
                            }

                            if (prefix.length() > max_chars) {
                                Informer.send(player, HCore.lang.getString("commands.prefix.error")
                                        .replace("{max}", "" + max_chars));
                                return false;
                            }

                            if (suffix.length() > max_chars) {
                                Informer.send(player, HCore.lang.getString("commands.prefix.error")
                                        .replace("{max}", "" + max_chars));
                                return false;
                            }

                            if (type.equals("chat")) {
                                if (HCore.LuckPerms) {
                                    LPAPI.setPrefix(target, prefix);
                                    LPAPI.setSuffix(target, suffix);
                                    Informer.send(player, HCore.lang.getString("commands.prefix.success.self")
                                            .replace("{type}", HCore.lang.getString("commands.prefix.types.chat"))
                                            .replace("{player}", target.getName())
                                            .replace("{prefix}", prefix + target.getName() + suffix)
                                    );
                                    if (target != player) {
                                        Informer.send(target, HCore.lang.getString("commands.prefix.success.target")
                                                .replace("{type}", HCore.lang.getString("commands.prefix.types.chat"))
                                                .replace("{player}", playerName)
                                                .replace("{prefix}", prefix + target.getName() + suffix)
                                        );
                                    }
                                    return true;
                                } else {
                                    Informer.send(player, HCore.lang.getString("errors.no-plugin")
                                            .replace("{plugin}", "LuckPerms")
                                    );
                                }
                            } else {
                                if (HCore.TAB) {
                                    TAPI.setPrefix(target, prefix);
                                    TAPI.setSuffix(target, suffix);
                                    Informer.send(player, HCore.lang.getString("commands.prefix.success.self")
                                            .replace("{type}", HCore.lang.getString("commands.prefix.types.tab"))
                                            .replace("{player}", target.getName())
                                            .replace("{prefix}", prefix + target.getName() + suffix)
                                    );
                                    if (target != player) {
                                        Informer.send(target, HCore.lang.getString("commands.prefix.success.target")
                                                .replace("{type}", HCore.lang.getString("commands.prefix.types.tab"))
                                                .replace("{player}", playerName)
                                                .replace("{prefix}", prefix + target.getName() + suffix)
                                        );
                                    }
                                    return true;
                                } else {
                                    Informer.send(player, HCore.lang.getString("errors.no-plugin")
                                            .replace("{plugin}", "TAB")
                                    );
                                }
                            }
                            return false;
                        }
                    }
                    Informer.send(player, info);
                } else {
                    Informer.send(player, HCore.lang.getString("errors.player-not-found"));
                }
            } else {
                    Informer.send(player, info);
            }
        } else {
                Informer.send(player, HCore.lang.getString("errors.no-perm"));
        }
        return false;
    }

    public static String fix(@Nullable String string) {
        if (string == null) return "";
        string = string
                .replace("\\", "")
                .replace("%", "")
                .replace("№", "")
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