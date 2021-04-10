package ru.dehasher.hcore.api.tab;

import me.neznamy.tab.api.EnumProperty;
import me.neznamy.tab.api.TABAPI;
import org.bukkit.entity.Player;

public class TAPI {

    public static void setPrefix(Player player, String string) {
        TABAPI.setValuePermanently(player.getUniqueId(), EnumProperty.TABPREFIX, string);
        TABAPI.setValuePermanently(player.getUniqueId(), EnumProperty.TAGPREFIX, string);
    }

    public static void setSuffix(Player player, String string) {
        TABAPI.setValuePermanently(player.getUniqueId(), EnumProperty.TABSUFFIX, string);
        TABAPI.setValuePermanently(player.getUniqueId(), EnumProperty.TAGSUFFIX, string);
    }
}
