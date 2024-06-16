
package me.cedric.siegegame.display;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.player.GamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class PlayerScoreboard {
    private static final int CHARACTER_LIMIT = 64;

    private final SiegeGamePlugin plugin;

    private final Scoreboard scoreboard;
    private final Objective objective;
    private final GamePlayer gamePlayer;


    private final Collection<Team> gameTeams;
    private final Team emptyTeam;
    private final Team mapTeam;
    private final Team serverTeam;

    public PlayerScoreboard(Component title, GamePlayer gamePlayer, Player player, SiegeGamePlugin plugin) {
        //Title, Empty, Teams, Empty, Map, Empty, Server
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        this.objective = scoreboard.registerNewObjective("siegegame", Criteria.DUMMY, title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.gamePlayer = gamePlayer;

        this.plugin = plugin;

        this.emptyTeam = addEmptyTeam();
        this.mapTeam = addMapTeam();
        this.serverTeam = addServerTeam();
        this.gameTeams = addGameTeams();

        player.setScoreboard(this.scoreboard);
    }

    public void setMap(Component map) {
        mapTeam.suffix(map);
    }

    public void setServer(Component server) {
        serverTeam.suffix(server);
    }

    public void setScores(WorldGame worldGame) {
        Set<me.cedric.siegegame.model.teams.Team> gameTeams = worldGame.getTeams();

        for (me.cedric.siegegame.model.teams.Team gameTeam : gameTeams) {
            Team team = scoreboard.getTeam(gameTeam.getIdentifier());
            if (team == null) throw new IllegalArgumentException("Team " + gameTeam.getIdentifier() + " is not registered to the Scoreboard");

            TextColor color = ColorUtil.getRelationalColor(gameTeam, gamePlayer.getTeam()).getTextColor();
            team.prefix(gameTeam.getName().color(color).append(Component.text(": ")));

            int points = gameTeam.getPoints();
            team.suffix(Component.text(points + " points").color(NamedTextColor.WHITE));
        }
    }

    private Collection<Team> addGameTeams() {
        WorldGame worldGame = this.plugin.getGameManager().getCurrentMatch().getWorldGame();
        Collection<me.cedric.siegegame.model.teams.Team> gameTeams = worldGame.getTeams();

        Collection<Team> teams = new HashSet<>();

        int i = 5 + gameTeams.size();
        this.objective.getScore("siegegame_empty_teams").setScore(i);

        for (me.cedric.siegegame.model.teams.Team gameTeam : gameTeams) {
            i--;

            Team team = scoreboard.registerNewTeam(gameTeam.getIdentifier());

            team.addEntry(gameTeam.getIdentifier());
            this.objective.getScore(gameTeam.getIdentifier()).setScore(i);
            this.objective.getScore(gameTeam.getIdentifier()).customName(Component.empty());

            teams.add(team);
        }

        return teams;
    }

    private Team addMapTeam() {
        Team team = scoreboard.registerNewTeam("siegegame_map");
        team.displayName(Component.empty());
        team.prefix(Component.text("Map: ").color(NamedTextColor.GOLD));

        team.addEntry("siegegame_map_entry");
        this.objective.getScore("siegegame_map_entry").setScore(3);
        this.objective.getScore("siegegame_map_entry").customName(Component.empty());

        return team;
    }

    private Team addServerTeam() {
        Team team = scoreboard.registerNewTeam("siegegame_server");
        team.displayName(Component.empty());

        team.addEntry("siegegame_server_entry");
        this.objective.getScore("siegegame_server_entry").setScore(1);
        this.objective.getScore("siegegame_server_entry").customName(Component.empty());

        return team;
    }

    private Team addEmptyTeam() {
        Team team = scoreboard.registerNewTeam("siegegame_empty");
        team.displayName(Component.empty());

        team.addEntry("siegegame_empty_teams");
        this.objective.getScore("siegegame_empty_teams").setScore(5);
        this.objective.getScore("siegegame_empty_teams").customName(Component.empty());

        team.addEntry("siegegame_empty_map");
        this.objective.getScore("siegegame_empty_map").setScore(4);
        this.objective.getScore("siegegame_empty_map").customName(Component.empty());

        team.addEntry("siegegame_empty_server");
        this.objective.getScore("siegegame_empty_server").setScore(2);
        this.objective.getScore("siegegame_empty_server").customName(Component.empty());

        return team;
    }
}
