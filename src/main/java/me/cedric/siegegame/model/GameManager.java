package me.cedric.siegegame.model;

import com.google.common.collect.ImmutableSet;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.player.kits.KitStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public final class GameManager {

    private final Set<SiegeGameMatch> siegeGameMatches = new HashSet<>();
    private final SiegeGamePlugin plugin;
    private final KitStorage kitStorage;
    private Queue<SiegeGameMatch> gameMatchQueue = new ArrayDeque<>();
    private SiegeGameMatch currentMatch;
    private SiegeGameMatch lastMatch = null;

    public GameManager(SiegeGamePlugin plugin) {
        this.plugin = plugin;
        this.kitStorage = new KitStorage(plugin);
    }

    public void addGame(SiegeGameMatch siegeGameMatch) {
        siegeGameMatches.add(siegeGameMatch);
        gameMatchQueue.add(siegeGameMatch);
    }

    public void shuffleQueue() {
        List<SiegeGameMatch> shuffled = new ArrayList<>(gameMatchQueue);
        Collections.shuffle(shuffled);
        gameMatchQueue = new ArrayDeque<>(shuffled);
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

    public KitStorage getKitStorage() {
        return kitStorage;
    }

    public void startNextGame() {
        boolean wait = false;
        if (getCurrentMatch() != null) {
            endGame(false, true); // Do not unload now. We will unload later with no players
            wait = true;
            Bukkit.broadcast(Component.text("Next game starting in 15 seconds!").color(NamedTextColor.DARK_AQUA));
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            SiegeGameMatch gameMatch = gameMatchQueue.poll();

            if (gameMatch == null) {
                plugin.getLogger().severe("NO MAP AT THE HEAD OF QUEUE. COULD NOT START GAME");
                return;
            }

            currentMatch = gameMatch;
            currentMatch.startGame();

            Bukkit.broadcast(Component.text("Starting next game...").color(NamedTextColor.DARK_AQUA));

            if (lastMatch == null)
                return;

            Bukkit.getScheduler().runTaskLater(plugin, () -> lastMatch.getGameMap().unload(), 10 * 20);
        }, wait ? 15 * 20 : 10);
    }

    public void endGame(boolean unload, boolean loadNextMap) {
        if (currentMatch != null) {
            currentMatch.endGame(unload);
            gameMatchQueue.add(currentMatch);
        }

        lastMatch = currentMatch;
        currentMatch = null;

        if (loadNextMap) {
            SiegeGameMatch match = getNextMatch();

            if (match == null)
                return;

            match.getGameMap().load();
        }
    }

}
