package me.cedric.siegegame.player.border;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.fake.FakeBlockManager;
import me.cedric.siegegame.model.GameManager;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.player.border.blockers.EntityTracker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class BorderUpdateTask extends BukkitRunnable {

    private final GameManager manager;
    private final UUID uuid;

    public BorderUpdateTask(GameManager manager, UUID uuid) {
        this.manager = manager;
        this.uuid = uuid;
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
        SiegeGameMatch match = manager.getCurrentMatch();

        if (match == null)
            return;

        GamePlayer gamePlayer = match.getWorldGame().getPlayer(uuid);

        if (gamePlayer == null)
            return;

        if (!shouldCheck(gamePlayer))
            return;

        PlayerBorderHandler handler = gamePlayer.getBorderHandler();

        FakeBlockManager fakeBlockManager = gamePlayer.getFakeBlockManager();
        handler.getBorders().forEach(border -> handler.getBorderDisplay(border).update(fakeBlockManager));
        fakeBlockManager.setAllVisible(true);
        fakeBlockManager.update();
    }
}
