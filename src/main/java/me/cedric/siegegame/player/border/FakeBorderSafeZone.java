package me.cedric.siegegame.player.border;

import me.cedric.siegegame.display.ColorUtil;
import me.cedric.siegegame.display.TeamColor;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;

import java.util.ArrayList;
import java.util.List;

public class FakeBorderSafeZone implements FakeBorder {
    private final Team team;
    private final TeamColor teamColor;
    protected final List<Wall> walls;
    private final GamePlayer gamePlayer;
    private final Border border;

    public FakeBorderSafeZone(GamePlayer gamePlayer, Team team) {
        this.gamePlayer = gamePlayer;
        this.border = team.getSafeArea();
        this.walls = new ArrayList<>();
        this.team = team;
        this.teamColor = ColorUtil.getRelationalColor(gamePlayer.getTeam(), team);
    }

    @Override
    public void update() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void create() {

    }

    @Override
    public Border getBorder() {
        return border;
    }
}
