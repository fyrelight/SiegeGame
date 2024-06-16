package me.cedric.siegegame.model.teams;

import me.cedric.siegegame.display.TeamColor;
import me.cedric.siegegame.model.teams.territory.Territory;
import me.cedric.siegegame.player.border.TeamBorder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;

public class TeamFactory {

    private Territory territory;
    private Location safeSpawn;
    private final TeamBorder safeArea;
    private final String configKey;
    private final Component name;
    private final TeamColor color;

    public TeamFactory(TeamBorder safeArea, Location safeSpawn, String name, String configKey, TeamColor color) {
        this.safeArea = safeArea;
        this.safeSpawn = safeSpawn;
        this.name = MiniMessage.miniMessage().deserialize(name);
        this.configKey = configKey;
        this.color = color;
    }

    public Territory getTerritory() {
        return territory;
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;
    }

    public TeamBorder getSafeArea() {
        return safeArea;
    }

    public Location getSafeSpawn() {
        return safeSpawn.clone();
    }

    public void setSafeSpawn(Location safeSpawn) {
        this.safeSpawn = safeSpawn;
    }

    public Component getName() {
        return name;
    }

    public String getConfigKey() {
        return configKey;
    }

    public TeamColor getColor() {
        return color;
    }


}
