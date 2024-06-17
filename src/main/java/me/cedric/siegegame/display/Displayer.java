package me.cedric.siegegame.display;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.enums.Messages;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.model.teams.territory.Territory;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Displayer {
    private static final Component TITLE = Component.text("Sieges").decorate(TextDecoration.BOLD).color(NamedTextColor.DARK_AQUA);

    private final SiegeGamePlugin plugin;
    private final GamePlayer gamePlayer;
    private final Player player;
    private PlayerScoreboard scoreboard;
    private BossBar bossBar;

    public Displayer(SiegeGamePlugin plugin, GamePlayer gamePlayer) {
        this.plugin = plugin;
        this.gamePlayer = gamePlayer;
        this.player = gamePlayer.getBukkitPlayer();
        this.scoreboard = new PlayerScoreboard(TITLE, gamePlayer, player, plugin);
        this.bossBar = null;
    }

    public void updateScoreboard() {
        if (player == null)
            return;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        this.scoreboard.setScores(match.getWorldGame());
        this.scoreboard.setMap(match.getGameMap().getDisplayName().color(NamedTextColor.GRAY));
        this.scoreboard.setServer(plugin.getGameConfig().getServerName());
    }

    public void wipeScoreboard() {
        this.scoreboard = new PlayerScoreboard(TITLE, gamePlayer, player, plugin);
    }

    public void displayKill(GamePlayer dead, GamePlayer killerGamePlayer) {

        Team killerTeam = killerGamePlayer.getTeam();
        Player killer = killerGamePlayer.getBukkitPlayer();

        TextComponent textComponent = Component.text("")
                .color(TextColor.color(88, 140, 252))
                .append(Messages.prefix(plugin))
                .append(Component.text(killer.getName(), ColorUtil.getRelationalColor(gamePlayer.getTeam(), killerTeam).getTextColor())
                .append(Component.text(" has killed ", TextColor.color(252, 252, 53)))
                .append(Component.text(dead.getBukkitPlayer().getName() + " ", ColorUtil.getRelationalColor(gamePlayer.getTeam(), dead.getTeam()).getTextColor()))
                .append(Component.text(killerTeam.getName() + ": ", TextColor.color(255, 194, 97)))
                .append(Component.text("+" + plugin.getGameConfig().getPointsPerKill() + " points ", TextColor.color(255, 73, 23))));

        gamePlayer.getBukkitPlayer().sendMessage(textComponent);

        TextComponent xpLevels = Component.text("")
                .color(TextColor.color(0, 143, 26))
                .append(Component.text("+" + plugin.getGameConfig().getLevelsPerKill() + " XP Levels"));

        if (killerTeam.equals(gamePlayer.getTeam()))
            gamePlayer.getBukkitPlayer().sendMessage(xpLevels);
    }

    public void displayCombatLogKill(String dead) {
        TextComponent textComponent = Component.text("")
                .color(TextColor.color(88, 140, 252))
                .append(Messages.prefix(plugin))
                        .append(Component.text(dead, TextColor.color(237, 77, 255))
                                .append(Component.text(" has logged out in combat. ", TextColor.color(252, 252, 53)))
                                .append(Component.text("Enemies have received ", TextColor.color(255, 194, 97)))
                                .append(Component.text("+" + plugin.getGameConfig().getPointsPerKill() + " points ", TextColor.color(255, 73, 23))));

        gamePlayer.getBukkitPlayer().sendMessage(textComponent);
    }

    public void displayInsideClaims(WorldGame worldGame, Territory territory) {
        if (this.bossBar != null) player.hideBossBar(this.bossBar);

        Team team = worldGame.getTeam(territory.getTeam().getConfigKey());

        TeamColor color = ColorUtil.getRelationalColor(gamePlayer.getTeam(), team);


        player.sendActionBar(Messages.NOTIFICATION_CLAIMS_ENTERED.asComponent()
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("%s")
                        .replacement(team.getName().color(color.getTextColor()))
                        .build()));

        this.bossBar = BossBar.bossBar(
                Component.text("You are currently in ").color(color.getTextColor())
                        .append(team.getName())
                        .append(Component.text(" claims")),
                1, color.getBossBarColor(), BossBar.Overlay.PROGRESS);

        player.showBossBar(this.bossBar);
    }

    public void removeDisplayInsideClaims() {
        if (this.bossBar != null) player.hideBossBar(this.bossBar);
        this.bossBar = null;
    }

    public void displayActionCancelled() {
        //gamePlayer.getBukkitPlayer().sendMessage(Messages.ERROR_FORBIDDEN_IN_ENEMY_TERRITORY);
    }

    public void displayVictory() {
        gamePlayer.getBukkitPlayer().sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "VICTORY", ChatColor.YELLOW + "gg ez yall are dog z tier rands");
    }

    public void displayLoss() {
        gamePlayer.getBukkitPlayer().sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "DEFEAT", ChatColor.RED + "u folded gg ez dog");
    }

    public void displayXPGain(GamePlayer gamePlayer) {
        TextComponent xpLevels = Component.text("")
                .color(TextColor.color(0, 143, 26))
                .append(Component.text("+" + plugin.getGameConfig().getLevelsPerKill() + " XP Levels"));
        gamePlayer.getBukkitPlayer().sendMessage(xpLevels);
    }
}

