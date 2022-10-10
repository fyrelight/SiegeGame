package me.cedric.siegegame.model;

import com.google.common.collect.ImmutableSet;
import me.cedric.siegegame.SiegeGamePlugin;
import org.bukkit.Bukkit;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public final class GameManager {

    private final Set<SiegeGameMatch> siegeGameMatches = new HashSet<>();
    private final Queue<SiegeGameMatch> gameMatchQueue = new ArrayDeque<>();
    private final SiegeGamePlugin plugin;
    private SiegeGameMatch currentMatch;
    private SiegeGameMatch lastMatch = null;

    public GameManager(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    public void addGame(SiegeGameMatch siegeGameMatch) {
        siegeGameMatches.add(siegeGameMatch);
        gameMatchQueue.add(siegeGameMatch);
    }

    public void removeGame(String identifier) {
        siegeGameMatches.removeIf(siegeGameMatch -> siegeGameMatch.getIdentifier().equalsIgnoreCase(identifier));
    }

    public SiegeGameMatch getNextMatch() {
        return gameMatchQueue.peek();
    }

    public SiegeGameMatch getCurrentMatch() {
        return currentMatch;
    }

    public SiegeGameMatch getLastMatch() {
        return lastMatch;
    }

    public Set<SiegeGameMatch> getLoadedMatches() {
        return ImmutableSet.copyOf(siegeGameMatches);
    }

    public void startNextGame() {
        boolean wait = false;
        if (getCurrentMatch() != null) {
            endGame(currentMatch);
            wait = true;
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            SiegeGameMatch gameMatch = gameMatchQueue.poll();

            if (gameMatch == null) {
                plugin.getLogger().severe("NO MAP AT THE HEAD OF QUEUE. COULD NOT START GAME");
                return;
            }

            currentMatch = gameMatch;
            currentMatch.startGame();
        }, wait ? 30 * 20 : 10);
    }

    private void endGame(SiegeGameMatch siegeGameMatch) {
        siegeGameMatch.endGame();
        lastMatch = siegeGameMatch;
        gameMatchQueue.add(siegeGameMatch);
        currentMatch = null;
    }
}
