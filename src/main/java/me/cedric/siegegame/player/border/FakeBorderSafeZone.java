package me.cedric.siegegame.player.border;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import me.cedric.siegegame.display.ColorUtil;
import me.cedric.siegegame.display.TeamColor;
import me.cedric.siegegame.fake.FakeBlockManager;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.util.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Objects;

public class FakeBorderSafeZone implements FakeBorder {
    private final Team team;
    private final TeamColor teamColor;
    private final Box box;
    private final GamePlayer gamePlayer;
    private final Border border;
    private final ICombatManager combatManager;
    private boolean wallVisible;

    public FakeBorderSafeZone(GamePlayer gamePlayer, Team team) {
        this.gamePlayer = gamePlayer;
        this.border = team.getSafeArea();
        this.team = team;
        this.teamColor = ColorUtil.getRelationalColor(gamePlayer.getTeam(), team);

        BoundingBox borderBox = border.getBoundingBox();
        int minX = (int) borderBox.getMinX();
        int maxX = (int) borderBox.getMaxX();
        int minZ = (int) borderBox.getMinZ();
        int maxZ = (int) borderBox.getMaxZ();
        int minY = (int) borderBox.getMinY();
        int maxY = (int) borderBox.getMaxY();

        this.box = new Box(minX, maxX, minZ, maxZ, minY, maxY);
        createFloor();

        ICombatLogX combatLogX = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
        this.combatManager = Objects.requireNonNull(combatLogX).getCombatManager();
    }


    @Override
    public void update() {
        if (shouldDisplay(gamePlayer)) {
            if (wallVisible) {
                // Exists; remove and redraw
                draw();
                return;
            }
            wallVisible = true;
            //Doesn't exist; create and draw
            create();
        } else {
            if (!wallVisible) {
                // Doesn't exist; create and draw
                createFloor();
                return;
            }
            wallVisible = false;
            // Exists; destroy and drawFloor
            destroy();
        }
    }

    private boolean shouldDisplay(GamePlayer gamePlayer) {
        Bukkit.getLogger().info("Checking if should display for " + gamePlayer.getBukkitPlayer().getName());
        if (this.team.getPlayers().contains(gamePlayer)) return true;
        Bukkit.getLogger().info("Player is not in team " + team.getName());
        return combatManager.isInCombat(gamePlayer.getBukkitPlayer());
    }

    @Override
    public void destroy() {
        FakeBlockManager fakeBlockManager = gamePlayer.getFakeBlockManager();
        World world = gamePlayer.getBukkitPlayer().getWorld();

        box.walls.forEach(wall -> destroyWall(fakeBlockManager, world, wall));
        destroyFloor(fakeBlockManager, world, box.ceiling);
        destroyFloor(fakeBlockManager, world, box.floor);

        box.walls.clear();
        box.ceiling = null;
        box.floor = null;

        createFloor();

        fakeBlockManager.update();
    }

    private void destroyFloor(FakeBlockManager manager, World world, Floor floor) {
        for (int x = floor.minX; x <= floor.maxX; x++) {
            for (int z = floor.minZ; z <= floor.maxZ; z++) {
                manager.removeBlock(world, x, floor.y, z);
            }
        }
    }

    private void destroyWall(FakeBlockManager manager, World world, Wall wall) {
        for (int xz = wall.minXZ; xz <= wall.maxXZ; xz++) {
            for (int y = wall.minY; y <= wall.maxY; y++) {
                int x = wall.getX(xz);
                int z = wall.getZ(xz);
                manager.removeBlock(world, x, y, z);
            }
        }
    }

    @Override
    public void create() {
        box.walls.clear();
        box.ceiling = null;
        box.floor = null;
        createWalls();
        createCeiling();
        createFloor();
        draw();
    }

    private void createCeiling() {
        box.ceiling = new Floor(box.minX, box.maxX, box.minZ, box.maxZ, box.maxY);
    }

    private void createFloor() {
        box.floor = new Floor(box.minX, box.maxX, box.minZ, box.maxZ, box.minY);
        drawFloor();
    }

    private void createWalls() {
        box.walls.add(new Wall(box.minX, box.maxX, box.minZ, box.minY, box.maxY, true, true));
        box.walls.add(new Wall(box.minX, box.maxX, box.maxZ, box.minY, box.maxY, true, false));
        box.walls.add(new Wall(box.minZ, box.maxZ, box.minX, box.minY, box.maxY, false, true));
        box.walls.add(new Wall(box.minZ, box.maxZ, box.maxX, box.minY, box.maxY, false, false));
    }

    private void draw() {
        FakeBlockManager fakeBlockManager = gamePlayer.getFakeBlockManager();
        World world = gamePlayer.getBukkitPlayer().getWorld();

        //Remove
        box.walls.forEach(wall -> destroyWall(fakeBlockManager, world, wall));
        destroyFloor(fakeBlockManager, world, box.ceiling);
        destroyFloor(fakeBlockManager, world, box.floor);

        //Redraw
        for (Wall wall : box.walls) {
            drawWall(fakeBlockManager,world,wall);
        }
        drawFloor(fakeBlockManager,world,box.ceiling, teamColor.getTransparentBlock(), false);
        drawFloor(fakeBlockManager,world,box.floor, teamColor.getSolidBlock(), true);

        fakeBlockManager.update();
    }

    private void drawFloor() {
        FakeBlockManager fakeBlockManager = gamePlayer.getFakeBlockManager();
        World world = gamePlayer.getBukkitPlayer().getWorld();

        //Remove
        destroyFloor(fakeBlockManager, world, box.floor);

        //Redraw
        drawFloor(fakeBlockManager,world,box.floor, teamColor.getSolidBlock(), true);

        fakeBlockManager.update();
    }

    private void drawFloor(FakeBlockManager manager, World world, Floor floor, Material material, boolean replaceSolid) {
        for (int x = floor.minX; x <= floor.maxX; x++) {
            for (int z = floor.minZ; z <= floor.maxZ; z++) {
                int y = floor.y;
                if (!replaceSolid && world.getBlockAt(x, y, z).isSolid())
                    continue;
                manager.addBlock(material, world, x, y, z, border.blockChangesAllowed());
            }
        }
    }

    private void drawWall(FakeBlockManager manager, World world , Wall wall) {
        for (int xz = wall.minXZ; xz <= wall.maxXZ; xz++) {
            for (int y = wall.minY + 1; y < wall.maxY; y++) {
                int x = wall.getX(xz);
                int z = wall.getZ(xz);
                manager.addBlock(teamColor.getTransparentBlock(), world, x, y, z, border.blockChangesAllowed());
            }
        }
    }

    @Override
    public Border getBorder() {
        return border;
    }
}
