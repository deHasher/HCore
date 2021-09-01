package ru.dehasher.bukkit.api.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.entity.Player;
import ru.dehasher.bukkit.managers.Informer;

import java.util.UUID;

// Вообще эта залупа работает через жопу и ставит преф только в контексте server=global.
// Автор хуесос, либо я долбоеб. Мне похуй, я заебался эту хуйню дебажить.
// А вики для ботанов и там читать дохуя.
public class LPAPI {
    private static final int weight = 1000;

    private static LuckPerms getPlugin() {
        return LuckPermsProvider.get();
    }

    public static void setPrefix(Player player, String string) {
        UUID uuid = player.getUniqueId();
        PrefixNode prefixNode = PrefixNode.builder(string, weight).build();

        // Чтобы блять не баговалось сука!
        if (string.equals(getPrefix(uuid))) return;

        reset(player, "prefix");
        loadUser(uuid).data().add(prefixNode);
        saveUser(uuid);
    }

    public static void setSuffix(Player player, String string) {
        UUID uuid = player.getUniqueId();
        SuffixNode suffixNode = SuffixNode.builder(string, weight).build();

        // Чтобы блять не баговалось сука!
        if (string.equals(getSuffix(uuid))) return;

        reset(player, "suffix");
        loadUser(uuid).data().add(suffixNode);
        saveUser(uuid);
    }

    static String getPrefix(UUID uuid) {
        return loadUser(uuid).getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getPrefix();
    }

    static String getSuffix(UUID uuid) {
        return loadUser(uuid).getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getSuffix();
    }

    public static void reset(Player player, String type) {
        try {
            UUID uuid = player.getUniqueId();
            User user = loadUser(uuid);
            switch (type) {
                case "prefix":
                    if (getPrefix(uuid) != null) {
                        PrefixNode prefixNode = PrefixNode.builder(getPrefix(uuid), weight).build();
                        user.data().clear(n -> n.getType().matches(prefixNode));
                        saveUser(uuid);
                    }
                    break;
                case "suffix":
                    if (getSuffix(uuid) != null) {
                        SuffixNode suffixNode = SuffixNode.builder(getSuffix(uuid), weight).build();
                        user.data().clear(n -> n.getType().matches(suffixNode));
                        saveUser(uuid);
                    }
                    break;
            }
        } catch (NullPointerException e) {
            Informer.send(null, "LPAPI error");
        }
    }

    private static User loadUser(UUID uuid) {
        return getPlugin().getUserManager().getUser(uuid);
    }

    private static void saveUser(UUID uuid) {
        User user = loadUser(uuid);
        getPlugin().getUserManager().saveUser(user).thenRun(
                () -> getPlugin().getMessagingService().ifPresent(service -> service.pushUserUpdate(user))
        );
    }
}
