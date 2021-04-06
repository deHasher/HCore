package ru.dehasher.hcore.managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Cooldowner {

    private static final Map<String, Cooldowner> cooldowns = new HashMap<>();

    private long start;
    private final int time;
    private final String player;
    private final String name;

    public Cooldowner(Player player, String name, int time) {
        this.player = player.getName();
        this.name   = name;
        this.time   = time;
    }

    // Проверяем существует ли кулдаун.
    public static boolean isInCooldown(Player player, String name) {
        if (getTimeLeft(player, name) >= 1) {
            return true;
        } else {
            stop(player, name);
            return false;
        }
    }

    // Убираем кулдаун.
    private static void stop(Player player, String name) {
        Cooldowner.cooldowns.remove(player.getName() + name);
    }

    // Получаем кулдаун.
    private static Cooldowner getCooldown(Player player, String name) {
        return cooldowns.get(player.getName() + name);
    }

    // Получаем оставшееся время кулдауна.
    public static int getTimeLeft(Player player, String name) {
        Cooldowner cooldown = getCooldown(player, name);
        int f = -1;
        if (cooldown != null) {
            long now = System.currentTimeMillis();
            long time = cooldown.start;
            int r = (int) (now - time) / 1000;
            f = (r - cooldown.time) * (-1);
        }

        return f;
    }

    // Создаём кулдаун.
    public void start() {
        this.start = System.currentTimeMillis();
        cooldowns.put(this.player + this.name, this);
    }
}