package me.cedric.siegegame.command.kits;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.command.FunctionalCommand;
import me.cedric.siegegame.enums.Messages;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.player.GamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class KitsCommand extends FunctionalCommand {

    private final SiegeGamePlugin plugin;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public KitsCommand(SiegeGamePlugin plugin) {
        this.plugin = plugin;
        registerCompletions(1, List.of("set", "delete"));
        List<String> maps = new ArrayList<>(plugin.getGameConfig().getMapIDs());
        maps.add("allmaps");
        registerCompletions(2, maps);
    }

    @Override
    public void commandLogic(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        CommandSender sender = commandSourceStack.getSender();

        if (!sender.hasPermission("siegegame.kits")) {
            sender.sendMessage(Messages.ERROR_REQUIRES_PERMISSION);
            return;
        }

        Entity executor = commandSourceStack.getExecutor();
        if (executor == null) return;

        if (!(executor instanceof Player player)) return;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null) {
            player.sendMessage(Component.text("You need to be in a match to do this").color(NamedTextColor.RED));
            return;
        }

        if (args.length != 2) {
            player.sendMessage(Component.text("/kits set <map or allmaps>").color(NamedTextColor.RED));
            player.sendMessage(Component.text("/kits delete <map or allmaps>").color(NamedTextColor.RED));
            return;
        }

        List<String> maps = new ArrayList<>(plugin.getGameConfig().getMapIDs());
        maps.add("allmaps");

        String identifier = args[1];
        if (!maps.contains(identifier)) {
            player.sendMessage(Component.text("Could not validate map: " + identifier).color(NamedTextColor.RED));
            return;
        }

        WorldGame worldGame = match.getWorldGame();
        GamePlayer gamePlayer = worldGame.getPlayer(player.getUniqueId());

        if (gamePlayer == null)
            return;

        if (isOnCooldown(player.getUniqueId())) {
            long cooldown = getCooldown(player.getUniqueId());
            player.sendMessage(Component.text("You need to wait another " + cooldown + " seconds to do this.").color(NamedTextColor.RED));
            return;
        }

        if (args[0].equalsIgnoreCase("delete")) {
            plugin.getGameManager().getKitStorage().removeKit(player.getUniqueId(), identifier);
            player.sendMessage(Component.text("Deleted kit for map: " + identifier).color(NamedTextColor.GREEN));
            putOnCooldown(player.getUniqueId());
            return;
        }

        plugin.getGameManager().getKitStorage().setKit(player.getUniqueId(), identifier, worldGame, player.getInventory().getContents().clone());
        player.sendMessage(Component.text("Set your current inventory as your kit for map: " + identifier).color(NamedTextColor.GREEN));
        putOnCooldown(player.getUniqueId());
    }

    private boolean isOnCooldown(UUID uuid) {
        if (!cooldowns.containsKey(uuid))
            return false;

        long lastTime = cooldowns.get(uuid);
        long currentTime = System.currentTimeMillis();

        return currentTime - lastTime < 15 * 1000L;
    }

    private void putOnCooldown(UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis());
    }

    private long getCooldown(UUID uuid) {
        if (!cooldowns.containsKey(uuid))
            return 0L;

        long lastTime = cooldowns.get(uuid);
        long currentTime = System.currentTimeMillis();

        return ((15 * 1000L) - (currentTime - lastTime)) / 1000;
    }
}
























