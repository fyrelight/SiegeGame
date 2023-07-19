package me.cedric.siegegame.player.border;

import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class FakeBorderBox implements FakeBorder {
    private final Material wallMaterial;
    protected final List<Wall> walls;
    private boolean wallVisible;
    private boolean floorVisible;
    private boolean ceilingVisible;
    private final GamePlayer gamePlayer;
    private final Border border;
    private final Material floorMaterial;
    private final Material ceilingMaterial;

    public FakeBorderBox(GamePlayer gamePlayer, Border border, Material wallMaterial, Material floorMaterial, Material ceilingMaterial) {
        this.gamePlayer = gamePlayer;
        this.border = border;
        this.wallMaterial = wallMaterial;
        this.walls = new ArrayList<>();
        this.floorMaterial = floorMaterial;
        this.ceilingMaterial = ceilingMaterial;
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
