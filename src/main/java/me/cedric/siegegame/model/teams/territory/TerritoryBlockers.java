package me.cedric.siegegame.model.teams.territory;

import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TerritoryBlockers implements Listener {

    private final Territory territory;
    private final WorldGame worldGame;

    private final static List<Material> interactProhibited = new ArrayList<>();

    public TerritoryBlockers(WorldGame worldGame, Territory territory) {
        this.worldGame = worldGame;
        this.territory = territory;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!event.hasChangedBlock())
            return;

        GamePlayer gamePlayer = worldGame.getPlayer(player.getUniqueId());

        Location to = event.getTo();

        if (gamePlayer == null)
            return;

        if (territory.isInside(to) && !territory.isInside(event.getFrom())) {
            gamePlayer.getDisplayer().displayInsideClaims(worldGame, territory);
            return;
        }

        if (territory.isInside(event.getFrom()) && !territory.isInside(to))
            gamePlayer.getDisplayer().removeDisplayInsideClaims();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        GamePlayer gamePlayer = worldGame.getPlayer(player.getUniqueId());

        Location to = event.getTo();

        if (gamePlayer == null)
            return;

        if (territory.isInside(to)) {
            gamePlayer.getDisplayer().displayInsideClaims(worldGame, territory);
            return;
        }

        if (territory.isInside(event.getFrom()) && !territory.isInside(to))
            gamePlayer.getDisplayer().removeDisplayInsideClaims();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        evaluateCancel(event.getPlayer(), event.getBlock().getLocation(), event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        evaluateCancel(event.getPlayer(), event.getBlock().getLocation(), event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        Location loc = event.getInventory().getLocation();

        if (loc == null)
            return;

        if (!interactProhibited.contains(loc.getBlock().getType()))
            return;

        evaluateCancel((Player) event.getPlayer(), loc, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBucketFill(PlayerBucketFillEvent event) {
        evaluateCancel(event.getPlayer(), event.getBlock().getLocation(), event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        evaluateCancel(event.getPlayer(), event.getBlock().getLocation(), event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        ItemStack item = event.getItem();

        if (item != null && interactProhibited.contains(item.getType()))
            evaluateCancel(event.getPlayer(), event.getPlayer().getLocation(), event);

        if (block != null && interactProhibited.contains(block.getType()))
            evaluateCancel(event.getPlayer(), block.getLocation(), event);
    }

    private void evaluateCancel(Player player, Location location, Cancellable event) {
        GamePlayer gamePlayer = worldGame.getPlayer(player.getUniqueId());

        if (gamePlayer == null)
            return;

        if (gamePlayer.getBukkitPlayer().hasPermission(Permissions.CLAIMS_BYPASS.getPermission()))
            return;

        if (location == null || !territory.isInside(location.getWorld(), location.getBlockX(), location.getBlockZ()))
            return;

        if (gamePlayer.isDead() || !gamePlayer.hasTeam())
            event.setCancelled(true);

        if (gamePlayer.hasTeam() && gamePlayer.getTeam().getSafeArea().getBoundingBox().isColliding(location))
            event.setCancelled(true);

        if (gamePlayer.getTeam().getIdentifier().equalsIgnoreCase(territory.getTeam().getConfigKey()))
            return;

        event.setCancelled(true);
        gamePlayer.getDisplayer().displayActionCancelled();
    }

    static {
        // Regular blocks
        interactProhibited.add(Material.HONEYCOMB);
        interactProhibited.add(Material.ARMOR_STAND);
        interactProhibited.add(Material.END_CRYSTAL);
        interactProhibited.add(Material.CAKE);
        interactProhibited.add(Material.CANDLE);
        interactProhibited.add(Material.CHEST);

        // Pressure plates
        interactProhibited.add(Material.ACACIA_PRESSURE_PLATE);
        interactProhibited.add(Material.MANGROVE_PRESSURE_PLATE);
        interactProhibited.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        interactProhibited.add(Material.DARK_OAK_PRESSURE_PLATE);
        interactProhibited.add(Material.BIRCH_PRESSURE_PLATE);
        interactProhibited.add(Material.POLISHED_BLACKSTONE_PRESSURE_PLATE);
        interactProhibited.add(Material.WARPED_PRESSURE_PLATE);
        interactProhibited.add(Material.OAK_PRESSURE_PLATE);
        interactProhibited.add(Material.SPRUCE_PRESSURE_PLATE);
        interactProhibited.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        interactProhibited.add(Material.STONE_PRESSURE_PLATE);
        interactProhibited.add(Material.JUNGLE_PRESSURE_PLATE);
        interactProhibited.add(Material.CRIMSON_PRESSURE_PLATE);

        // Buttons
        interactProhibited.add(Material.ACACIA_BUTTON);
        interactProhibited.add(Material.DARK_OAK_BUTTON);
        interactProhibited.add(Material.STONE_BUTTON);
        interactProhibited.add(Material.POLISHED_BLACKSTONE_BUTTON);
        interactProhibited.add(Material.JUNGLE_BUTTON);
        interactProhibited.add(Material.BIRCH_BUTTON);
        interactProhibited.add(Material.CRIMSON_BUTTON);
        interactProhibited.add(Material.MANGROVE_BUTTON);
        interactProhibited.add(Material.OAK_BUTTON);
        interactProhibited.add(Material.SPRUCE_BUTTON);
        interactProhibited.add(Material.WARPED_BUTTON);

        // Signs
        interactProhibited.add(Material.JUNGLE_SIGN);
        interactProhibited.add(Material.CRIMSON_WALL_SIGN);
        interactProhibited.add(Material.DARK_OAK_SIGN);
        interactProhibited.add(Material.WARPED_WALL_SIGN);
        interactProhibited.add(Material.OAK_WALL_SIGN);
        interactProhibited.add(Material.MANGROVE_WALL_SIGN);
        interactProhibited.add(Material.SPRUCE_WALL_SIGN);
        interactProhibited.add(Material.BIRCH_SIGN);
        interactProhibited.add(Material.MANGROVE_SIGN);
        interactProhibited.add(Material.ACACIA_WALL_SIGN);
        interactProhibited.add(Material.SPRUCE_SIGN);
        interactProhibited.add(Material.ACACIA_SIGN);
        interactProhibited.add(Material.CRIMSON_SIGN);
        interactProhibited.add(Material.WARPED_SIGN);
        interactProhibited.add(Material.JUNGLE_WALL_SIGN);
        interactProhibited.add(Material.DARK_OAK_WALL_SIGN);
        interactProhibited.add(Material.OAK_SIGN);
        interactProhibited.add(Material.BIRCH_WALL_SIGN);

        // Fence gates
        interactProhibited.add(Material.OAK_FENCE_GATE);
        interactProhibited.add(Material.SPRUCE_FENCE_GATE);
        interactProhibited.add(Material.BIRCH_FENCE_GATE);
        interactProhibited.add(Material.JUNGLE_FENCE_GATE);
        interactProhibited.add(Material.ACACIA_FENCE_GATE);
        interactProhibited.add(Material.DARK_OAK_FENCE_GATE);
        interactProhibited.add(Material.CRIMSON_FENCE_GATE);
        interactProhibited.add(Material.WARPED_FENCE_GATE);

        // Doors and trapdoors
        interactProhibited.add(Material.IRON_DOOR);
        interactProhibited.add(Material.OAK_DOOR);
        interactProhibited.add(Material.SPRUCE_DOOR);
        interactProhibited.add(Material.BIRCH_DOOR);
        interactProhibited.add(Material.JUNGLE_DOOR);
        interactProhibited.add(Material.ACACIA_DOOR);
        interactProhibited.add(Material.DARK_OAK_DOOR);
        interactProhibited.add(Material.CRIMSON_DOOR);
        interactProhibited.add(Material.WARPED_DOOR);
        interactProhibited.add(Material.IRON_TRAPDOOR);
        interactProhibited.add(Material.OAK_TRAPDOOR);
        interactProhibited.add(Material.SPRUCE_TRAPDOOR);
        interactProhibited.add(Material.BIRCH_TRAPDOOR);
        interactProhibited.add(Material.JUNGLE_TRAPDOOR);
        interactProhibited.add(Material.ACACIA_TRAPDOOR);
        interactProhibited.add(Material.DARK_OAK_TRAPDOOR);
        interactProhibited.add(Material.CRIMSON_TRAPDOOR);
        interactProhibited.add(Material.WARPED_TRAPDOOR);
    }

}
