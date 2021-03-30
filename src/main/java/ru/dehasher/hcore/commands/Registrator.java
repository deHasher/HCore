package ru.dehasher.hcore.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import ru.dehasher.hcore.commands.list.spawn;
import ru.dehasher.hcore.commands.list.setspawn;
import ru.dehasher.hcore.commands.list.hreload;
import ru.dehasher.hcore.managers.Informer;

public class Registrator extends BukkitCommand {

    public Registrator(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender sender, String command, String[] args) {
        switch (command) {
            case "spawn":
                return spawn.send(sender, command, args);
            case "setspawn":
                return setspawn.send(sender, command, args);
            case "hreload":
                return hreload.send(sender, command, args);
            default:
                Informer.send("The command /" + command + " does not exist :P");
                Informer.send("Remove this command from the configuration and restart the server.");
        }
        return false;
    }
}