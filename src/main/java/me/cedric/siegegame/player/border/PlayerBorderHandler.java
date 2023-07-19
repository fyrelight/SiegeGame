package me.cedric.siegegame.player.border;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.border.blockers.EntityTracker;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerBorderHandler {

    private final SiegeGamePlugin plugin;
    private final GamePlayer player;
    private final EntityTracker entityTracker;
    private final Map<Border, FakeBorder> borders = new HashMap<>();

    public PlayerBorderHandler(SiegeGamePlugin plugin, GamePlayer player) {
        this.plugin = plugin;
        this.player = player;
        this.entityTracker = new EntityTracker();
    }

    public FakeBorder getBorderDisplay(Border border) {
        return borders.get(border);
    }

    public void addBorder(Border border, Material material) {
        borders.put(border, new FakeBorderWall(player, border, 10, 5, material));
    }

    public void addSafeZone(Team team) {
        borders.put(team.getSafeArea(), new FakeBorderSafeZone(player, team));
    }

    public Set<Border> getBorders() {
        return new HashSet<>(borders.keySet());
    }

    public void removeBorder(Border key) {
        borders.remove(key);
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public void clear() {
        borders.clear();
    }

    public EntityTracker getEntityTracker() {
        return entityTracker;
    }
}
