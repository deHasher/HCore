package ru.dehasher.hcore.timers;

import org.bukkit.entity.Player;

import ru.dehasher.hcore.events.OnPlayerSendMessage;

public class tChat implements Runnable {
    Player player;

    public tChat(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        OnPlayerSendMessage.chat.remove((Object)this.player);
    }
}