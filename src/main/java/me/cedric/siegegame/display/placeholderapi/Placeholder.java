package me.cedric.siegegame.display.placeholderapi;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.display.ColorUtil;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public enum Placeholder {

    TEAM_NAME("team_name", false, (siegeGame, gamePlayer, s, extra) -> {
        Team team = getTeam(siegeGame, gamePlayer, s);
        return team == null ? "" : LegacyComponentSerializer.legacySection().serialize(team.getName());
    }),

    TEAM_COLOR("team_color", false, (siegeGame, gamePlayer, s, extra) -> {
        Team team = getTeam(siegeGame, gamePlayer, s);
        return team == null ? "" : team.getColor().getTextColor().value() + "";
    }),

    TEAM_POINTS("team_points", false, (siegeGame, gamePlayer, s, extra) -> {
        Team team = getTeam(siegeGame, gamePlayer, s);
        return team == null ? "" : team.getPoints() + "";
    }),

    TEAM_YOU_OR_EMPTY("team_you_or_empty", false, (siegeGame, gamePlayer, s, extra) -> {
        return getTeamYouOrEmpty(siegeGame, gamePlayer, s);
    }),

    RELATIONAL_COLOR("rel_player_color", true, (siegeGame, gamePlayer, s, extra) -> {
        if (!(extra[0] instanceof Player))
            return "";
        GamePlayer two = siegeGame.getGameManager().getCurrentMatch().getWorldGame().getPlayer(((Player) extra[0]).getUniqueId());
        return ColorUtil.getRelationalColor(gamePlayer.getTeam(), two.getTeam()).getMinimessage();
    });

    private final String param;
    private final PlaceholderAction<SiegeGamePlugin, GamePlayer, String, String, Object[]> action;
    private final boolean relational;

    Placeholder(String param, boolean relational, PlaceholderAction<SiegeGamePlugin, GamePlayer, String, String, Object[]> action) {
        this.param = param;
        this.action = action;
        this.relational = relational;
    }

    public boolean isRelational() {
        return relational;
    }

    public String getParameter() {
        return param;
    }

    public PlaceholderAction<SiegeGamePlugin, GamePlayer, String, String, Object[]> getAction() {
        return action;
    }

    private static Team getTeam(SiegeGamePlugin plugin, GamePlayer gamePlayer, String configKey) {
        if (configKey == null || configKey.isEmpty())
            return gamePlayer.hasTeam() ? gamePlayer.getTeam() : null;

        SiegeGameMatch gameMatch = plugin.getGameManager().getCurrentMatch();

        if (gameMatch == null)
            return null;

        return gameMatch.getWorldGame().getTeam(configKey);
    }

    public static String getTeamYouOrEmpty(SiegeGamePlugin plugin, GamePlayer gamePlayer, String s) {
        Team team = getTeam(plugin, gamePlayer, s);
        if (team != null && gamePlayer.hasTeam() &&
                team.getIdentifier().equalsIgnoreCase(gamePlayer.getTeam().getIdentifier()))
            return "YOU";
        return "";
    }
}
