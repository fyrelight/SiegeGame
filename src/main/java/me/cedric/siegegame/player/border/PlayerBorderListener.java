package me.cedric.siegegame.player.border;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.player.border.blockers.EntityTracker;
import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.player.border.blockers.ProjectileFollowTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        BorderUpdateTask borderUpdateTask = new BorderUpdateTask(plugin.getGameManager(), event.getPlayer().getUniqueId());

        borderUpdateTask.runTaskTimer(plugin, 0, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
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

        if (!gamePlayer.getBukkitPlayer().getWorld().equals(gameMatch.getWorld()))
            return;

        PlayerBorderHandler handler = gamePlayer.getBorderHandler();

        List<Border> affectedBorders = handler.getBorders().stream().filter(border -> isAffected(event.getTo(), border)).toList();

        if (affectedBorders.isEmpty()) {
            // Only set last position IF it can never be affected by a border. This ensures they are never rolled back to somewhere they can't be
            gamePlayer.getBorderHandler().getEntityTracker().setLastPosition(event.getPlayer().getUniqueId(), event.getTo().clone());
        } else if (affectedBorders.stream().anyMatch(border -> !border.canLeave(gamePlayer))) {
            // If they are affected by a border that they can't leave, rollback
            rollback(gamePlayer, gameMatch);
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
            return border.isInverse();

        // If we get here player is outside the border, this is good if it is an inverse border
        return !border.isInverse();
    }

    private void rollback(GamePlayer player, SiegeGameMatch match) {
        EntityTracker entityTracker = player.getBorderHandler().getEntityTracker();
        Location location = entityTracker.getLastPosition(player.getUUID());
        if (location == null) {
            if (player.getTeam().getSafeArea().canLeave(player)) location = player.getTeam().getSafeSpawn();
            else location = match.getGameMap().getDefaultSpawn();
        }
        player.getBukkitPlayer().teleport(location);
        player.getBukkitPlayer().sendMessage(Component.text("You have been rolled back for getting through a border.").color(NamedTextColor.RED));
    }

    private boolean shouldCheck(GamePlayer gamePlayer) {
        EntityTracker entityTracker = gamePlayer.getBorderHandler().getEntityTracker();
        Location lastSafe = entityTracker.getLastPosition(gamePlayer.getUUID());

        if (lastSafe == null)
            return true;

        return !gamePlayer.getBukkitPlayer().hasPermission(Permissions.BORDER_BYPASS.getPermission());
    }

}
