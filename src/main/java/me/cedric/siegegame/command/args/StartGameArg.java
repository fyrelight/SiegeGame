package me.cedric.siegegame.command.args;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.command.FunctionalCommand;
import me.cedric.siegegame.enums.Messages;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StartGameArg extends FunctionalCommand {

    private final SiegeGamePlugin plugin;

    public StartGameArg(SiegeGamePlugin plugin) {
        //super("siegegame.admin.start");
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(@NotNull CommandSourceStack commandSourceStack, String[] args) {
        CommandSender sender = commandSourceStack.getSender();

        if (!sender.hasPermission("siegegame.admin.start")) {
            sender.sendMessage(Messages.ERROR_REQUIRES_PERMISSION);
            return;
        }

        plugin.getGameManager().startNextGame();
    }
}
