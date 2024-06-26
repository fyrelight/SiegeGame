package me.cedric.siegegame.player.border.blockers;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.player.border.Border;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.player.border.PlayerBorderHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ProjectileFollowTask extends BukkitRunnable {
    
    private final GamePlayer player;
    private final SiegeGamePlugin plugin;
    private final SiegeGameMatch gameMatch;
    private final EntityTracker entityTracker;
    private final Projectile projectile;

    public ProjectileFollowTask(SiegeGamePlugin plugin, GamePlayer player, SiegeGameMatch gameMatch, Projectile projectile) {
        this.player = player;
        this.plugin = plugin;
        this.gameMatch = gameMatch;
        this.projectile = projectile;
        this.entityTracker = player.getBorderHandler().getEntityTracker();
    }

    @Override
    public void run() {
        PlayerBorderHandler playerBorderHandler = player.getBorderHandler();
        Location lastSafe = entityTracker.getLastPosition(projectile.getUniqueId()).clone();
        for (Border border : playerBorderHandler.getBorders()) {
            if (border.canLeave(player))
                continue;

            if (!checkBorder(border, lastSafe.toVector()))
                deleteProjectileAndCancel(projectile, lastSafe, playerBorderHandler);
        }

        // changed block
        if (!projectile.getLocation().equals(lastSafe))
            entityTracker.setLastPosition(projectile.getUniqueId(), projectile.getLocation());

        if (projectile.isDead()) {
            entityTracker.stopTracking(projectile.getUniqueId());
            cancel();
        }
    }

    private boolean checkBorder(Border border, Vector lastSafe) {
        int distance = border.isInverse() ? 3 : -3;

        // If you are inside a border and it is not inverse (regular border), is good
        // Otherwise, you are inside an inverse border, is bad
        if (border.getBoundingBox().clone().expand(distance).isColliding(lastSafe))
            return !border.isInverse();

        // If we get here player is outside the border, this is good if it is an inverse border
        return border.isInverse();
    }

    private void deleteProjectileAndCancel(Projectile projectile, Location lastSafe, PlayerBorderHandler playerBorderHandler) {
        if (projectile instanceof EnderPearl) {
            lastSafe.setYaw(player.getBukkitPlayer().getLocation().getYaw());
            lastSafe.setPitch(player.getBukkitPlayer().getLocation().getPitch());
            player.getBukkitPlayer().teleport(lastSafe);
        }

        entityTracker.stopTracking(projectile.getUniqueId());
        projectile.remove();
        this.cancel();
        player.getBukkitPlayer().sendMessage(Component.text("You cannot use projectiles near a border or safe area").color(NamedTextColor.RED));
    }
}
