package me.cedric.siegegame.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.command.args.ReloadArg;
import me.cedric.siegegame.command.args.SpawnControlArea;
import me.cedric.siegegame.command.args.StartGameArg;
import me.cedric.siegegame.enums.Messages;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SiegeGameCommand extends FunctionalCommand {

    private final SiegeGamePlugin plugin;

    public SiegeGameCommand(SiegeGamePlugin plugin) {
        this.plugin = plugin;
        registerArguments();
    }

    private void registerArguments() {
        registerArgument("start", new StartGameArg(plugin));
        registerArgument("reload", new ReloadArg(plugin));
        registerArgument("spawncontrolarea", new SpawnControlArea(plugin));
    }

    @Override
    public void commandLogic(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] strings) {
        CommandSender sender = commandSourceStack.getSender();

        if (!sender.hasPermission("siegegame.help")) {
            sender.sendMessage(Messages.ERROR_REQUIRES_PERMISSION);
            return;
        }

        commandSourceStack.getSender().sendMessage("/siegegame start");
    }
}
