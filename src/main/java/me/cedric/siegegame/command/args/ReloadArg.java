package me.cedric.siegegame.command.args;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.command.FunctionalCommand;
import me.cedric.siegegame.enums.Messages;
import me.cedric.siegegame.enums.Permissions;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadArg extends FunctionalCommand {

    private final SiegeGamePlugin plugin;

    public ReloadArg(SiegeGamePlugin plugin) {
        //super(Permissions.RELOAD_FILES.getPermission(), "/siegegame reload", Message.valueOf("Reloads siegegame config"));
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(@NotNull CommandSourceStack commandSourceStack, String[] args) {
        CommandSender sender = commandSourceStack.getSender();

        if (!sender.hasPermission(Permissions.RELOAD_FILES.getPermission())) {
            sender.sendMessage(Messages.ERROR_REQUIRES_PERMISSION);
            return;
        }

        plugin.getGameConfig().reloadConfig();
        commandSourceStack.getSender().sendMessage("Reloaded");
    }
}
