package me.cedric.siegegame.display;

import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.teams.Team;
import org.bukkit.DyeColor;
import org.bukkit.Material;


public class ColorUtil {
    public static TeamColor getRelationalColor(Team one, Team two) {
        if (one == null || two == null)
            return NamedTeamColor.WHITE;

        if (!one.getWorldGame().equals(two.getWorldGame()))
            return NamedTeamColor.WHITE;

        if (one.equals(two))
            return NamedTeamColor.CYAN;

        WorldGame worldGame = one.getWorldGame();

        if (worldGame.getTeams().size() == 2) {
            return NamedTeamColor.RED;
        }

        return two.getColor();
    }

    public static Material getRelationalWool(Team one, Team two) {
        TeamColor teamColor = getRelationalColor(one, two);
        return teamColor.getSoftBlock();
    }

    @Deprecated
    private static Material woolFromColor(int red, int green, int blue) {
        int distance = Integer.MAX_VALUE;
        org.bukkit.DyeColor closest = null;
        for (DyeColor dye : org.bukkit.DyeColor.values()) {
            org.bukkit.Color color = dye.getColor();
            int dist = Math.abs(color.getRed() - red) + Math.abs(color.getGreen() - green) + Math.abs(color.getBlue() - blue);
            if (dist < distance) {
                distance = dist;
                closest = dye;
            }
        }

        if (closest == null)
            return null;

        return Material.matchMaterial((closest.name() + "_wool").toUpperCase());
    }

}
