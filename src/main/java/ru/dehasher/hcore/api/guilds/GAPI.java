package ru.dehasher.hcore.api.guilds;

import me.glaremasters.guilds.Guilds;
import me.glaremasters.guilds.api.GuildsAPI;
import org.bukkit.entity.Player;

public class GAPI {

    public static String getRole(Player player) {
        GuildsAPI api = Guilds.getApi();
        try {
            return api.getGuildRole(player).getName();
        } catch (NullPointerException e) {
            return "все";
        }

    }
}
