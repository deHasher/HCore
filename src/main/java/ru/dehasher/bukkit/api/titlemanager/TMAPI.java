package ru.dehasher.bukkit.api.titlemanager;

import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import ru.dehasher.bukkit.HCore;

public class TMAPI {
    public static TitleManagerAPI getPlugin() {
        return (TitleManagerAPI) HCore.getPlugin().getServer().getPluginManager().getPlugin("TitleManager");
    }
}