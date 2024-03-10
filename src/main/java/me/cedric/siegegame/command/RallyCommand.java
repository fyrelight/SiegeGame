package me.cedric.siegegame.command;

import com.lunarclient.bukkitapi.LunarClientAPI;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.enums.Messages;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.modules.lunarclient.LunarClientSupport;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RallyCommand extends FunctionalCommand {

    private final SiegeGamePlugin plugin;

    public RallyCommand(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        CommandSender sender = commandSourceStack.getSender();

        if (!sender.hasPermission("siegegame.rally")) {
            sender.sendMessage(Messages.ERROR_REQUIRES_PERMISSION);
            return;
        }

        Entity executor = commandSourceStack.getExecutor();
        if (executor == null) return;

        if (!(executor instanceof Player player)) return;

        if (!LunarClientModule.isLunarClient(player.getUniqueId())) {
            player.sendMessage(Messages.ERROR_REQUIRES_LUNAR_CLIENT);
            return;
        }

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        WorldGame worldGame = match.getWorldGame();
        GamePlayer gamePlayer = worldGame.getPlayer(player.getUniqueId());

        if (gamePlayer == null || !gamePlayer.hasTeam())
            return;

        WaypointSender.sendTemporaryWaypoint(plugin, gamePlayer.getTeam(), player.getLocation(), "Rally", 30 * 20);
        player.sendMessage(Messages.COMMAND_RALLY_SET);
    }
}
