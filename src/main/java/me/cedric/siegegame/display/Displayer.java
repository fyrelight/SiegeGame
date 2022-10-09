package me.cedric.siegegame.display;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.display.placeholderapi.Placeholder;
import me.cedric.siegegame.enums.Messages;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.WorldGame;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.superitems.SuperItem;
import me.cedric.siegegame.territory.Territory;
import me.deltaorion.bukkit.display.actionbar.ActionBar;
import me.deltaorion.bukkit.display.bossbar.BarColor;
import me.deltaorion.bukkit.display.bossbar.EBossBar;
import me.deltaorion.bukkit.display.bukkit.BukkitApiPlayer;
import me.deltaorion.bukkit.display.scoreboard.EScoreboard;
import me.deltaorion.common.locale.message.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Displayer {

    private final SiegeGamePlugin plugin;
    private final GamePlayer gamePlayer;
    private final BukkitApiPlayer apiPlayer;

    public Displayer(SiegeGamePlugin plugin, GamePlayer gamePlayer) {
        this.plugin = plugin;
        this.gamePlayer = gamePlayer;
        this.apiPlayer = plugin.getBukkitPlayerManager().getPlayer(gamePlayer.getUUID());
    }

    public void updateScoreboard() {
        if (apiPlayer == null)
            return;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        List<String> lines = new ArrayList<>();
        lines.add("");

        List<Team> teams = match.getWorldGame().getTeams().stream().sorted(Comparator.comparing(Team::getName)).collect(Collectors.toList());

        for (Team team : teams) {
            lines.add(Placeholder.getRelationalColor(gamePlayer.getTeam(), team) + team.getName() + ": " +
                    ChatColor.WHITE + team.getPoints() + " points");
        }

        lines.add("");
        lines.add("    " + ChatColor.DARK_AQUA + "Super Items");

        for (int i = 0; i < match.getWorldGame().getSuperItemManager().getSuperItems().size(); i++) {
            List<SuperItem> superItems = new ArrayList<>(match.getWorldGame().getSuperItemManager().getSuperItems());
            SuperItem superItem = superItems.get(i);
            String owner = superItem.getOwner() == null ? "" : superItem.getOwner().getBukkitPlayer().getName();

            lines.add(ChatColor.LIGHT_PURPLE + superItem.getDisplayName() + ": " + ChatColor.GRAY + owner);
        }

        lines.add("");
        lines.add(ChatColor.GOLD + "Map: " + ChatColor.GRAY + match.getGameMap().getDisplayName());
        lines.add("");
        lines.add(ChatColor.YELLOW + "ceedric.com");

        EScoreboard scoreboard = apiPlayer.getScoreboard() == null ? apiPlayer.setScoreboard(apiPlayer.getUniqueID().toString(), lines.size()) : apiPlayer.getScoreboard();
        scoreboard.setTitle(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Sieges");

        int i = 0;
        for (String line : lines) {
            scoreboard.setLine(line, i);
            i++;
        }
    }

    public void wipeScoreboard() {
        BukkitApiPlayer apiPlayer = plugin.getBukkitPlayerManager().getPlayer(gamePlayer.getUUID());

        if (apiPlayer == null)
            return;

        apiPlayer.removeScoreboard();
    }

    public void displayKill(GamePlayer dead, GamePlayer killerGamePlayer) {

        Team killerTeam = killerGamePlayer.getTeam();
        Player killer = killerGamePlayer.getBukkitPlayer();

        TextComponent textComponent = Component.text("")
                .color(TextColor.color(88, 140, 252))
                .append(Component.text(Messages.PREFIX.toString() + " ")
                .append(Component.text(Placeholder.getRelationalColor(gamePlayer.getTeam(), killerTeam) + killer.getName())
                .append(Component.text(" has killed ", TextColor.color(252, 252, 53)))
                .append(Component.text(Placeholder.getRelationalColor(gamePlayer.getTeam(), dead.getTeam()) + dead.getBukkitPlayer().getName() + " "))
                .append(Component.text(killerTeam.getName() + ": ", TextColor.color(255, 194, 97)))
                .append(Component.text("+" + plugin.getGameConfig().getPointsPerKill() + " points ", TextColor.color(255, 73, 23)))));

        gamePlayer.getBukkitPlayer().sendMessage(textComponent);

        TextComponent xpLevels = Component.text("")
                .color(TextColor.color(0, 143, 26))
                .append(Component.text("+" + plugin.getGameConfig().getLevelsPerKill() + " XP Levels"));

        if (killerTeam.equals(gamePlayer.getTeam()))
            gamePlayer.getBukkitPlayer().sendMessage(xpLevels);
    }

    public void displayInsideEnemyClaims(WorldGame worldGame, Territory territory) {
        Message message = Messages.CLAIMS_ENTERED;
        Team team = worldGame.getTeam(territory.getTeam().getConfigKey());
        String s = String.format(message.toString(), Placeholder.getRelationalColor(gamePlayer.getTeam(), team) + team.getName());
        ActionBar actionBar = new ActionBar(s, Duration.ofSeconds(3));
        apiPlayer.getActionBarManager().send(actionBar);

        EBossBar bossBar = apiPlayer.setBossBar("siegegame-enemy-claims");
        bossBar.setMessage(ChatColor.YELLOW + "You are currently in " + s + ChatColor.YELLOW + " claims");
        bossBar.setColor(BarColor.YELLOW);
    }

    public void removeDisplayInsideEnemyClaims() {
        apiPlayer.removeBossBar();
    }

    public void displayActionCancelled() {
        //gamePlayer.getBukkitPlayer().sendMessage(Messages.CLAIMS_ACTION_CANCELLED.toString());
    }

    public void displayVictory() {
        gamePlayer.getBukkitPlayer().sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "VICTORY", ChatColor.YELLOW + "gg ez yall are dog z tier rands");
    }

    public void displayLoss() {

    }
}

