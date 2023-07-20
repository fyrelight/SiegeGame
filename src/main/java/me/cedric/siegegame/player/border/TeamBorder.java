package me.cedric.siegegame.player.border;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.util.BoundingBox;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TeamBorder extends Border {
    private Team team;
    private final ICombatManager combatManager;

    public TeamBorder(@NotNull BoundingBox boundingBox) {
        super(boundingBox);
        ICombatLogX combatLogX = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
        this.combatManager = Objects.requireNonNull(combatLogX).getCombatManager();
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public boolean canLeave(GamePlayer gamePlayer) {
        if (team == null) return true;
        if (!this.team.getPlayers().contains(gamePlayer)) return false;
        return (!combatManager.isInCombat(gamePlayer.getBukkitPlayer()));
    }
}
