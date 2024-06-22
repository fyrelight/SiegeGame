
package me.cedric.siegegame.display;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.player.GamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.*;

public class TeamScoreboard extends ScoreboardWrapper {
    private static final Component TITLE = Component.text("Sieges").decorate(TextDecoration.BOLD).color(NamedTextColor.DARK_AQUA);

    private final SiegeGamePlugin plugin;
    private final Map<me.cedric.siegegame.model.teams.Team, Integer> teams;

    public TeamScoreboard(SiegeGamePlugin plugin, me.cedric.siegegame.model.teams.Team team, WorldGame worldGame) {
        super(TITLE, 5 + worldGame.getTeams().size());

        this.plugin = plugin;

        this.teams = new HashMap<>();

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        int i = 1;
        super.createLine(i, Component.empty(), null);
        for (me.cedric.siegegame.model.teams.Team gameTeam : worldGame.getTeams()) {
            i++;
            TextColor color = ColorUtil.getRelationalColor(team, gameTeam).getTextColor();
            super.createLine(i, gameTeam.getName().color(color).append(Component.text(": ")), Component.text("0 points"));
            this.teams.put(gameTeam, i);
        }
        super.createLine(i+1, Component.empty(), null);
        super.createLine(i+2, Component.text("Map: ").color(NamedTextColor.GOLD), match.getGameMap().getDisplayName().color(NamedTextColor.GRAY));
        super.createLine(i+3, Component.empty(), null);
        super.createLine(i+4, Component.empty(), plugin.getGameConfig().getServerName());
    }

    public void addPlayer(GamePlayer gamePlayer) {
        super.addPlayer(gamePlayer.getBukkitPlayer());
    }

    public void update() {
        for (me.cedric.siegegame.model.teams.Team gameTeam : this.teams.keySet()) {
            int points = gameTeam.getPoints();
            super.setLine(teams.get(gameTeam), Component.text(points).append(Component.text(" points")));
        }
    }
}
