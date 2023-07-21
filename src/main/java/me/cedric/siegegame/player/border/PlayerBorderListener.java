package me.cedric.siegegame.player.border;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.player.border.blockers.EntityTracker;
import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.player.border.blockers.ProjectileFollowTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class PlayerBorderListener implements Listener {

    private final SiegeGamePlugin plugin;

    public PlayerBorderListener(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        GamePlayer gamePlayer = match.getWorldGame().getPlayer(event.getPlayer().getUniqueId());

        if (gamePlayer == null)
            return;

        BorderUpdateTask borderUpdateTask = new BorderUpdateTask(plugin.getGameManager(), event.getPlayer().getUniqueId());

        borderUpdateTask.runTaskTimer(plugin, 0, 1);
    }

    /*
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInitialSpawn(PlayerJoinEvent event) {
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        GamePlayer gamePlayer = match.getWorldGame().getPlayer(event.getPlayer().getUniqueId());

        if (gamePlayer == null)
            return;

        if (!shouldCheck(gamePlayer))
            return;

        PlayerBorderHandler handler = gamePlayer.getBorderHandler();

        Bukkit.getScheduler().runTaskLater(plugin, () -> handler.getBorders().forEach(border -> handler.getBorderDisplay(border).update()), 15);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        GamePlayer gamePlayer = match.getWorldGame().getPlayer(event.getPlayer().getUniqueId());

        if (gamePlayer == null)
            return;

        if (!shouldCheck(gamePlayer))
            return;

        PlayerBorderHandler handler = gamePlayer.getBorderHandler();

        Bukkit.getScheduler().runTaskLater(plugin, () -> handler.getBorders().forEach(border -> handler.getBorderDisplay(border).update()), 0);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTagged(PlayerTagEvent event) {
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        GamePlayer gamePlayer = match.getWorldGame().getPlayer(event.getPlayer().getUniqueId());

        if (gamePlayer == null)
            return;

        if (!shouldCheck(gamePlayer))
            return;

        PlayerBorderHandler handler = gamePlayer.getBorderHandler();

        handler.getBorders().forEach(border -> handler.getBorderDisplay(border).update());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUnTagged(PlayerUntagEvent event) {
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        GamePlayer gamePlayer = match.getWorldGame().getPlayer(event.getPlayer().getUniqueId());

        if (gamePlayer == null)
            return;

        if (!shouldCheck(gamePlayer))
            return;

        PlayerBorderHandler handler = gamePlayer.getBorderHandler();

        handler.getBorders().forEach(border -> handler.getBorderDisplay(border).update());
    }*/

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        GamePlayer gamePlayer = match.getWorldGame().getPlayer(event.getPlayer().getUniqueId());

        if (gamePlayer == null)
            return;

        if (!shouldCheck(gamePlayer))
            return;

        SiegeGameMatch gameMatch = plugin.getGameManager().getCurrentMatch();

        if (gameMatch == null)
            return;

        if (!gameMatch.getWorld().equals(gamePlayer.getBukkitPlayer().getWorld()))
            return;

        PlayerBorderHandler handler = gamePlayer.getBorderHandler();

        List<Border> affectedBorders = handler.getBorders().stream().filter(border -> isAffected(event.getTo(), border)).toList();

        if (affectedBorders.isEmpty()) {
            // Only set last position IF it can never be affected by a border. This ensures they are never rolled back to somewhere they can't be
            gamePlayer.getBorderHandler().getEntityTracker().setLastPosition(event.getPlayer().getUniqueId(), event.getTo().clone());
        } else if (affectedBorders.stream().anyMatch(border -> !border.canLeave(gamePlayer))) {
            // If they are affected by a border that they can't leave, rollback
            rollback(gamePlayer);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {

        Projectile projectile = event.getEntity();
        SiegeGameMatch gameMatch = plugin.getGameManager().getCurrentMatch();

        if (gameMatch == null)
            return;

        if (!(projectile.getShooter() instanceof Player player))
            return;

        List<EntityType> projectiles = plugin.getGameConfig().getBlacklistedProjectiles();
        if (!projectiles.contains(projectile.getType()))
            return;

        GamePlayer gamePlayer = gameMatch.getWorldGame().getPlayer(player.getUniqueId());

        gamePlayer.getBorderHandler().getEntityTracker().trackEntity(projectile);
        ProjectileFollowTask followTask = new ProjectileFollowTask(plugin, gamePlayer, gameMatch, projectile);
        followTask.runTaskTimer(plugin, 0, 1);
    }

    private boolean isAffected(Location location, Border border) {
        // If you are inside a border and it is not inverse (regular border), movement is good
        // Otherwise, you are inside an inverse border, movement is bad
        if (border.getBoundingBox().isColliding(location))
            return !border.isInverse();

        // If we get here player is outside the border, this is good if it is an inverse border
        return border.isInverse();
    }

    private void rollback(GamePlayer player) {
        EntityTracker entityTracker = player.getBorderHandler().getEntityTracker();
        player.getBukkitPlayer().teleport(entityTracker.getLastPosition(player.getUUID()));
        player.getBukkitPlayer().sendMessage(ChatColor.RED + "You have been rolled back for getting through a border.");
    }

    private boolean shouldCheck(GamePlayer gamePlayer) {
        EntityTracker entityTracker = gamePlayer.getBorderHandler().getEntityTracker();
        Location lastSafe = entityTracker.getLastPosition(gamePlayer.getUUID());

        if (lastSafe == null)
            return true;

        return !gamePlayer.getBukkitPlayer().hasPermission(Permissions.BORDER_BYPASS.getPermission());
    }

}
