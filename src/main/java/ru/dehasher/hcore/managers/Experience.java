package ru.dehasher.hcore.managers;

import org.bukkit.entity.Player;

// https://github.com/EssentialsX/Essentials/blob/2.x/Essentials/src/main/java/com/earth2me/essentials/craftbukkit/SetExpFix.java
public final class Experience {

    public static void setTotalExperience(Player player, int exp) {
        if (exp < 0) exp = 0;

        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);

        int amount = exp;

        while (amount > 0) {
            final int expToLevel = getExpAtLevel(player);
            amount -= expToLevel;
            if (amount >= 0) {
                player.giveExp(expToLevel);
            } else {
                amount += expToLevel;
                player.giveExp(amount);
                amount = 0;
            }
        }
    }

    private static int getExpAtLevel(Player player) {
        return getExpAtLevel(player.getLevel());
    }

    public static int getExpAtLevel(int level) {
        if (level <= 15) {
            return (2 * level) + 7;
        }
        if (level <= 30) {
            return (5 * level) - 38;
        }
        return (9 * level) - 158;
    }

    public static int getExpToLevel(int level) {
        int currentLevel = 0;
        int exp = 0;

        while (currentLevel < level) {
            exp += getExpAtLevel(currentLevel);
            currentLevel++;
        }
        if (exp < 0) {
            exp = Integer.MAX_VALUE;
        }
        return exp;
    }

    public static int getTotalExperience(Player player) {
        int exp = Math.round(getExpAtLevel(player) * player.getExp());
        int currentLevel = player.getLevel();

        while (currentLevel > 0) {
            currentLevel--;
            exp += getExpAtLevel(currentLevel);
        }
        if (exp < 0) {
            exp = Integer.MAX_VALUE;
        }
        return exp;
    }

    public static int getExpUntilNextLevel(Player player) {
        int exp = Math.round(getExpAtLevel(player) * player.getExp());
        int nextLevel = player.getLevel();
        return getExpAtLevel(nextLevel) - exp;
    }
}