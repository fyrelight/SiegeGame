package me.cedric.siegegame.model.map;

import me.cedric.siegegame.player.border.Border;
import me.cedric.siegegame.util.BoundingBox;
import me.cedric.siegegame.model.teams.TeamFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Set;

public class GameMap {

    private final FileMapLoader fileMapLoader;
    private final Set<TeamFactory> teamFactories;
    private final Component displayName;
    private final String name;
    private Border mapBorder;
    private final Material borderMaterial;
    private final Location defaultSpawn;

    public GameMap(FileMapLoader fileMapLoader, String displayName, Set<TeamFactory> teamFactory, Border border, Location defaultSpawn, Material borderMaterial) {
        this.fileMapLoader = fileMapLoader;
        this.teamFactories = teamFactory;
        this.name = MiniMessage.miniMessage().stripTags(displayName);
        this.displayName = MiniMessage.miniMessage().deserialize(displayName);
        this.mapBorder = border;
        this.defaultSpawn = defaultSpawn;
        this.borderMaterial = borderMaterial;
    }

    public TeamFactory getTeam(String configKey) {
        return teamFactories.stream().filter(factory -> factory.getConfigKey().equalsIgnoreCase(configKey)).findAny().orElse(null);
    }

    public Border getMapBorder() {
        return mapBorder;
    }

    public Material getBorderMaterial() {
        return borderMaterial;
    }

    public Location getDefaultSpawn() {
        return new Location(getWorld(), defaultSpawn.getBlockX(), defaultSpawn.getY(), defaultSpawn.getBlockZ(), defaultSpawn.getYaw(), defaultSpawn.getPitch());
    }

    public void unload() {
        fileMapLoader.unload();
    }

    public World getWorld() {
        return fileMapLoader.getWorld();
    }

    public boolean isWorldLoaded() {
        return fileMapLoader.isLoaded();
    }

    public boolean load() {
        if (!fileMapLoader.load())
            return false;

        defaultSpawn.setWorld(fileMapLoader.getWorld());
        fileMapLoader.getWorld().setSpawnLocation(defaultSpawn);
        BoundingBox box = mapBorder.getBoundingBox();
        mapBorder = new Border(new BoundingBox(fileMapLoader.getWorld(),
                (int) box.getMinX(), (int) box.getMinY(), (int) box.getMinZ(),
                (int) box.getMaxX(), (int) box.getMaxY(), (int) box.getMaxZ()));
        mapBorder.setAllowBlockChanges(false);
        for (TeamFactory team : teamFactories) {
            Location location = team.getSafeSpawn();
            location.setWorld(fileMapLoader.getWorld());
            team.getSafeArea().getBoundingBox().setWorld(fileMapLoader.getWorld());
            team.setSafeSpawn(location);
        }

        return true;
    }

    public void addTeam(TeamFactory team) {
        teamFactories.add(team);
    }

    public Component getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name;
    }
}
