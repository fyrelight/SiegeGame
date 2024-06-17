package me.cedric.siegegame.modules.abilityitems;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.enums.Messages;
import me.cedric.siegegame.model.game.Module;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.modules.abilityitems.items.MortyAntiQuickie;
import me.cedric.siegegame.modules.abilityitems.items.QxtiStick;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AbilityItemModule implements Module {

    private BukkitTask task;
    private final List<AbilityItem> abilityItems = new ArrayList<>();

    @Override
    public void onStartGame(SiegeGamePlugin plugin, WorldGame worldGame) {
        registerAbilities(plugin);
        // spawn an abilityitem every 30 seconds
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Random r = new Random();
            int index = abilityItems.isEmpty() ? 0 : r.nextInt(abilityItems.size());
            // spawn in without holo api
            AbilityItem abilityItem = abilityItems.get(index);
            Location location = generateLocation(plugin, worldGame, plugin.getGameManager().getCurrentMatch().getGameMap().getDefaultSpawn(), 50);

            FloatingItem floatingItem = new FloatingItem(plugin, location.clone(), abilityItem.getDisplayName(), abilityItem.getItem());

            worldGame.getPlayers().forEach(gamePlayer -> sendBeaconBeam(gamePlayer.getBukkitPlayer(), location.clone()));

            Bukkit.broadcast(Component.empty().color(NamedTextColor.LIGHT_PURPLE)
                    .append(Messages.prefix(plugin))
                    .append(Component.text("An "))
                    .append(Component.text("ability item").color(NamedTextColor.YELLOW))
                    .append(Component.text(" has spawned. Follow the beacon beam or go to "))
                    .append(Component.text(location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()).color(NamedTextColor.AQUA))
            );
        }, 10 * 20, 30 * 20);
    }

    private void registerAbilities(Plugin plugin) {
        abilityItems.add(new MortyAntiQuickie(plugin));
        abilityItems.add(new QxtiStick(plugin));
    }

    @Override
    public void onEndGame(SiegeGamePlugin plugin, WorldGame worldGame) {
        task.cancel();
    }

    private Location generateLocation(SiegeGamePlugin plugin, WorldGame worldGame, Location location, int offset) {
        Location loc = null;
        while (!isLocationValid(loc, plugin, worldGame)) {
            int maxX = location.getBlockX() + offset;
            int minX = location.getBlockX() - offset;
            int maxZ = location.getBlockZ() + offset;
            int minZ = location.getBlockZ() - offset;
            Random r = new Random();
            int newX = r.nextInt(minX, maxX);
            int newZ = r.nextInt(minZ, maxZ);
            Block block = location.getWorld().getHighestBlockAt(newX, newZ);
            loc = block.getLocation().clone();
        }
        return loc.clone().add(0, 2, 0);
    }

    private boolean isLocationValid(Location location, SiegeGamePlugin plugin, WorldGame worldGame) {
        return location != null && worldGame.getTeams().stream().noneMatch(team -> team.getTerritory().isInside(location)) &&
                plugin.getGameManager().getCurrentMatch() != null &&
                plugin.getGameManager().getCurrentMatch().getGameMap().getMapBorder().getBoundingBox().isColliding(location);

    }

    private void sendBeaconBeam(Player player, Location location) {
        Location beaconLoc = location.clone().subtract(0, 1, 0);
        BlockData blockData =  Bukkit.getServer().createBlockData(Material.BEACON);
        player.sendBlockChange(beaconLoc, blockData);
        List<Location> ironBlocks = new ArrayList<>();
        ironBlocks.add(beaconLoc.clone().subtract(0, 1, 0).add(1, 0, 0));
        ironBlocks.add(beaconLoc.clone().subtract(0, 1, 0).add(-1,0, 0));
        ironBlocks.add(beaconLoc.clone().subtract(0, 1, 0).add(0, 0, 1));
        ironBlocks.add(beaconLoc.clone().subtract(0, 1, 0).add(0, 0, -1));
        ironBlocks.add(beaconLoc.clone().subtract(0, 1, 0).add(1, 0, 1));
        ironBlocks.add(beaconLoc.clone().subtract(0, 1, 0).add(1, 0, -1));
        ironBlocks.add(beaconLoc.clone().subtract(0, 1, 0).add(-1,0, 1));
        ironBlocks.add(beaconLoc.clone().subtract(0, 1, 0).add(-1,0, -1));
        ironBlocks.add(beaconLoc.clone().subtract(0, 1, 0).add(0, 0, 0));
        ironBlocks.forEach(location1 -> {
            BlockData b = Bukkit.getServer().createBlockData(Material.IRON_BLOCK);
            player.sendBlockChange(location1, b);
        });

    }
}
