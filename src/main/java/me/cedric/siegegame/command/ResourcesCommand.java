package me.cedric.siegegame.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.enums.Messages;
import me.cedric.siegegame.model.SiegeGameMatch;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ResourcesCommand extends FunctionalCommand {

    private final SiegeGamePlugin plugin;

    public ResourcesCommand(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        CommandSender sender = commandSourceStack.getSender();

        if (!sender.hasPermission("siegegame.resources")) {
            sender.sendMessage(Messages.ERROR_REQUIRES_PERMISSION);
            return;
        }

        Entity executor = commandSourceStack.getExecutor();
        if (executor == null) return;

        if (!(executor instanceof Player player)) return;

        SiegeGameMatch gameMatch = plugin.getGameManager().getCurrentMatch();

        if (gameMatch == null)
            return;

        gameMatch.getWorldGame().getShopGUI().show(player);
    }
}
