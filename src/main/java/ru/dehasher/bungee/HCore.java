package ru.dehasher.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import ru.dehasher.bungee.commands.list.skin;
import ru.dehasher.bungee.events.Extra;
import ru.dehasher.bungee.commands.list.hub;
import ru.dehasher.bungee.managers.Informer;

public class HCore extends Plugin {

    public static String hub = "Hub-1";

    public void onEnable() {

        // Выводим логотип.
        getLogo();

        // Регистрируем все эвенты.
        registerEvents();

        // Генерируем команды.
        registerCommands();
    }

    private void registerCommands() {
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new hub("hub"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new hub("рги"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new skin("skin"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new skin("ылшт"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new skin("skins"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new skin("ылшты"));
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
