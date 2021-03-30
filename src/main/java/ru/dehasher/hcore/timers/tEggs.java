package ru.dehasher.hcore.timers;

import org.bukkit.entity.Player;

import ru.dehasher.hcore.events.OnPlayerUseSpawnegg;

public class tEggs implements Runnable {
    Player player;

    public tEggs(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        OnPlayerUseSpawnegg.eggs.remove(this.player);
    }
}