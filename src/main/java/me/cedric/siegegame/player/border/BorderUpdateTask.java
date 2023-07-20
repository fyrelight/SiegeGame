package me.cedric.siegegame.player.border;

import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.player.border.blockers.EntityTracker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class BorderUpdateTask extends BukkitRunnable {

    GamePlayer player;

    public BorderUpdateTask(GamePlayer player) {
        this.player = player;
    }

    private boolean shouldCheck(GamePlayer gamePlayer) {
        EntityTracker entityTracker = gamePlayer.getBorderHandler().getEntityTracker();
        Location lastSafe = entityTracker.getLastPosition(gamePlayer.getUUID());

        if (lastSafe == null)
            return true;

        return !gamePlayer.getBukkitPlayer().hasPermission(Permissions.BORDER_BYPASS.getPermission());
    }

    @Override
    public void run() {
        if (player.getBukkitPlayer() == null || !player.getBukkitPlayer().isOnline()) {
            cancel();
            return;
        }
        if (!shouldCheck(player))
            return;
        player.getBorderHandler().getBorders().forEach(border -> player.getBorderHandler().getBorderDisplay(border).update());
    }
}
