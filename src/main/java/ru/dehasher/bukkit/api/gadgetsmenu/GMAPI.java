package ru.dehasher.bukkit.api.gadgetsmenu;

import com.yapzhenyie.GadgetsMenu.api.GadgetsMenuAPI;
import com.yapzhenyie.GadgetsMenu.player.PlayerManager;
import org.bukkit.entity.Player;

public class GMAPI {
    public static PlayerManager getPlugin(Player player) {
        return GadgetsMenuAPI.getPlayerManager(player);
    }
}
