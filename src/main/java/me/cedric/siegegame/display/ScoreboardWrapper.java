package me.cedric.siegegame.display;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ScoreboardWrapper {
    private static final int MAXIMUM_LINES = 15;
    private final Map<Integer, Team> teams;
    private final int size;

    private final Scoreboard scoreboard;
    private final Objective objective;

    public ScoreboardWrapper(ComponentLike title, int size) {
        this(title.asComponent(), size);
    }

    public ScoreboardWrapper(Component title, int size) {
        this.size = size + 1;
        this.teams = new HashMap<>();

        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        this.objective = scoreboard.registerNewObjective("siegegame", Criteria.DUMMY, title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private void validateLine(int line) {
        if (line < 1) throw new IllegalArgumentException("Line number cannot be less than 1");
        if (line > size) throw new IllegalArgumentException("Line number cannot be greater than size of scoreboard: " + size);
        if (line > MAXIMUM_LINES) throw new IllegalArgumentException("Line number cannot be greater than 15");
    }

    public void addPlayer(Player player) {
        player.setScoreboard(this.scoreboard);
    }

    private Team addTeam(int line) {
        String name = "siegegame_team_" + line;
        String entry = ChatColor.values()[line].toString();

        this.scoreboard.registerNewTeam(name);

        Team team = this.scoreboard.getTeam(name);
        if (team == null) throw new UnsupportedOperationException("Team could not be created");
        this.teams.put(line, team);
        team.addEntry(entry);

        Score score = this.objective.getScore(entry);
        score.setScore(size - line);
        score.customName(Component.empty());
        score.numberFormat(NumberFormat.blank());

        return team;
    }

    public void createLine(int line, ComponentLike prefix, @Nullable ComponentLike initial) {
        if (initial != null) this.createLine(line, prefix.asComponent(), initial.asComponent());
        else this.createLine(line, prefix.asComponent(), null);
    }

    public void createLine(int line, Component prefix, @Nullable Component initial) {
        validateLine(line);

        Team team = this.teams.get(line);
        if (team == null) team = addTeam(line);

        team.prefix(prefix);
        if (initial != null) team.suffix(initial);
    }

    public void setLine(int line, ComponentLike text) {
        this.setLine(line, text.asComponent());
    }

    public void setLine(int line, Component text) {
        validateLine(line);

        Team team = this.teams.get(line);
        if (team == null) team = addTeam(line);

        team.suffix(text);
    }

    public void removeLine(int line) {
        Team team = this.teams.get(line);
        if (team == null) return;

        team.removeEntry(ChatColor.values()[line].toString());
    }

    public void removeAllLines() {
        this.teams.forEach((line, team) -> team.removeEntry(ChatColor.values()[line].toString()));
    }
}
