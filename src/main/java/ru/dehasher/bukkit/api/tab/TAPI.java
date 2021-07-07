package ru.dehasher.bukkit.api.tab;

import me.neznamy.tab.api.EnumProperty;
import me.neznamy.tab.api.TABAPI;
import org.bukkit.entity.Player;

public class TAPI {

    public static void setPrefix(Player player, String string) {
        TABAPI.getPlayer(player.getUniqueId()).setValuePermanently(EnumProperty.TABPREFIX, string);
        TABAPI.getPlayer(player.getUniqueId()).setValuePermanently(EnumProperty.TAGPREFIX, string);
    }

    public static void setSuffix(Player player, String string) {
        TABAPI.getPlayer(player.getUniqueId()).setValuePermanently(EnumProperty.TABSUFFIX, string);
        TABAPI.getPlayer(player.getUniqueId()).setValuePermanently(EnumProperty.TAGSUFFIX, string);
    }
}
