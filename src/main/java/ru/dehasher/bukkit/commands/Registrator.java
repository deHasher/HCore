package ru.dehasher.bukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import ru.dehasher.bukkit.commands.list.*;
import ru.dehasher.bukkit.managers.Informer;

import java.util.List;

public class Registrator extends BukkitCommand {

    public Registrator(String name, List<String> aliases) {
        super(name);

        this.setPermission(null);

        if (!aliases.isEmpty()) {
            this.setAliases(aliases);
        }
    }

    @Override
    public boolean execute(CommandSender sender, String command, String[] args) {
        command = this.getName();
        switch (command) {
            case "spawn":
                return spawn.send(sender, command, args);
            case "setspawn":
                return setspawn.send(sender, command, args);
            case "hreload":
                return hreload.send(sender, command, args);
            case "clearchat":
                return clearchat.send(sender, command, args);
            case "free":
                return free.send(sender, command, args);
            case "crash":
                return crash.send(sender, command, args);
            case "prefix":
                return prefix.send(sender, command, args);
            default:
                Informer.send("The command /" + command + " does not exist :P");
                Informer.send("Remove this command from the configuration and restart server.");
        }
        return false;
    }
}