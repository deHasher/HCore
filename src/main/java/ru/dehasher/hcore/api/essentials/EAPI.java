package ru.dehasher.hcore.api.essentials;

import com.earth2me.essentials.Essentials;
import net.ess3.api.IUser;
import net.ess3.api.events.GodStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.dehasher.hcore.HCore;
import ru.dehasher.hcore.events.OnPlayerJoinToPvpArena;
import ru.dehasher.hcore.managers.Methods;

public class EAPI implements Listener {

    public static Essentials getPlugin() {
        return (Essentials) HCore.getPlugin().getServer().getPluginManager().getPlugin("Essentials");
    }

    // Попытки сменить игроку режим бессмертия.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGodStatusChangeEvent(GodStatusChangeEvent e) {
        if (HCore.config.getString("pvp-arena.flags.block-godmode") == null) return;
        Player player = HCore.getPlugin().getServer().getPlayer(e.getAffected().getName());
        if (OnPlayerJoinToPvpArena.cancelAction(player) && e.getValue()) e.setCancelled(true);
    }

    public static void offGodMode(Player player) {
        IUser user = getPlugin().getUser(player);
        if (user.isGodModeEnabled()) Methods.sendConsole("god " + player.getName());
    }
}
