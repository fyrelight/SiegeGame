package me.cedric.siegegame.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.enums.Messages;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpawnCommand extends FunctionalCommand {

    private SiegeGamePlugin plugin;

    public SpawnCommand(SiegeGamePlugin plugin) {
        this.plugin = plugin;
        registerCompletions(1, List.of("spawn"));
    }

    @Override
    public void commandLogic(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        CommandSender sender = commandSourceStack.getSender();

        if (!sender.hasPermission("siegegame.spawn")) {
            sender.sendMessage(Messages.ERROR_REQUIRES_PERMISSION);
            return;
        }

        Entity executor = commandSourceStack.getExecutor();
        if (executor == null) return;

        if (!(executor instanceof Player bukkitPlayer)) return;

        if (args.length < 1 || !args[0].equalsIgnoreCase("spawn"))
            return;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        GamePlayer player = match.getWorldGame().getPlayer(bukkitPlayer.getUniqueId());

        if (player == null || !player.hasTeam())
            return;

        player.getBukkitPlayer().teleport(player.getTeam().getSafeSpawn());
    }
}
