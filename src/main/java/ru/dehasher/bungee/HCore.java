package ru.dehasher.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import ru.dehasher.bungee.commands.list.skin;
import ru.dehasher.bungee.events.Extra;
import ru.dehasher.bungee.commands.list.hub;
import ru.dehasher.bungee.managers.Informer;
import ru.dehasher.bungee.managers.Lang;
import ru.dehasher.bungee.managers.Methods;

import java.util.HashMap;

public class HCore extends Plugin {

    public static String HUB    = "Hub-";
    public static String KL_API = "https://api.klaun.ch/";

    public void onEnable() {

        // Выводим логотип.
        getLogo();

        // Регистрируем все эвенты.
        registerEvents();

        // Генерируем команды.
        registerCommands();

        // Генерируем команды.
        Informer.kl("vk", new HashMap<String, String>(){{put("msg", Lang.serverEnabled);}});
    }

    public void onDisable() {
        Informer.send(Methods.fixSlashes(Lang.crash));
        Informer.kl("vk", new HashMap<String, String>(){{put("msg", Lang.serverDisabled);}});
    }

    private void registerCommands() {
        for (String cmd : new String[]{"hub", "рги"}) {
            ProxyServer.getInstance().getPluginManager().registerCommand(this, new hub(cmd));
        }
        for (String cmd : new String[]{"skin", "ылшт", "skins", "ылшты"}) {
            ProxyServer.getInstance().getPluginManager().registerCommand(this, new skin(cmd));
        }
    }

    private void registerEvents() {
        ProxyServer.getInstance().getPluginManager().registerListener(this, new Extra(this));
    }

    public static ProxyServer getPlugin() {
        return ProxyServer.getInstance();
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
