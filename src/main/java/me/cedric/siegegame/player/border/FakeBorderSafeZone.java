package me.cedric.siegegame.player.border;

import me.cedric.siegegame.display.ColorUtil;
import me.cedric.siegegame.display.TeamColor;
import me.cedric.siegegame.fake.FakeBlockManager;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.util.BoundingBox;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class FakeBorderSafeZone implements FakeBorder {
    private final Team team;
    private final TeamColor teamColor;
    private final Material wallMaterial;
    private final Box box;
    private final GamePlayer gamePlayer;
    private final Border border;
    private boolean wallVisible;

    public FakeBorderSafeZone(GamePlayer gamePlayer, Team team) {
        this.gamePlayer = gamePlayer;
        this.border = team.getSafeArea();
        this.team = team;
        this.teamColor = ColorUtil.getRelationalColor(gamePlayer.getTeam(), team);
        this.wallMaterial = teamColor.getTransparentBlock();

        BoundingBox borderBox = border.getBoundingBox();
        int minX = (int) borderBox.getMinX();
        int maxX = (int) borderBox.getMaxX();
        int minZ = (int) borderBox.getMinZ();
        int maxZ = (int) borderBox.getMaxZ();
        int minY = (int) borderBox.getMinY();
        int maxY = (int) borderBox.getMaxY();

        this.box = new Box(minX, maxX, minZ, maxZ, minY, maxY);
        createFloor();
    }


    @Override
    public void update() {
        Location location = gamePlayer.getBukkitPlayer().getLocation();
        boolean destroy = false;
        boolean update = false;

        if (shouldDisplay(border,location)) {
            if (wallVisible)
                update = true;
            wallVisible = true;
        } else {
            if (!wallVisible) {
                drawFloor();
                return;
            }
            wallVisible = false;
            destroy = true;
        }

        if (destroy) {
            //System.out.println("Destroying");
            destroy();
            return;
        }

        if(update) {
            //System.out.println("Updating");
            updateBox(border.getBoundingBox());
            return;
        }

        //System.out.println("Creating");
        create();
    }

    private void updateBox(BoundingBox borderBox) {
        List<Wall> oldWalls = new ArrayList<>(box.walls);
        box.walls.clear();

        Floor oldCeiling = box.ceiling;
        Floor oldFloor = box.floor;
        box.ceiling = null;

        createWalls();
        createCeiling();

        drawUpdate(oldWalls,box.walls,oldCeiling,box.ceiling,oldFloor,box.floor);
    }

    private boolean shouldDisplay(Border border, Location location) {
        return !border.getBoundingBox().isColliding(location);
    }

    @Override
    public void destroy() {
        FakeBlockManager fakeBlockManager = gamePlayer.getFakeBlockManager();
        fakeBlockManager.removeAll();
        World world = gamePlayer.getBukkitPlayer().getWorld();

        box.walls.forEach(wall -> destroyWall(fakeBlockManager, world, wall));
        destroyFloor(fakeBlockManager, world, box.ceiling);

        box.walls.clear();
        box.ceiling = null;

        drawFloor();

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
        createWalls();
        createCeiling();
        draw();
    }

    private void createCeiling() {
        box.ceiling = new Floor(box.minX, box.maxX, box.minZ, box.maxZ, box.maxY);
    }

    private void createFloor() {
        box.floor = new Floor(box.minX, box.maxX, box.minZ, box.maxZ, box.minY);
    }

    private void createWalls() {
        box.walls.add(new Wall(box.minX, box.maxX, box.minZ, box.minY, box.maxY, true, true));
        box.walls.add(new Wall(box.minX, box.maxX, box.maxZ, box.minY, box.maxY, true, false));
        box.walls.add(new Wall(box.minZ, box.maxZ, box.minX, box.minY, box.maxY, false, true));
        box.walls.add(new Wall(box.minZ, box.maxZ, box.maxX, box.minY, box.maxY, false, false));
    }

    private void draw() {
        FakeBlockManager fakeBlockManager = gamePlayer.getFakeBlockManager();
        fakeBlockManager.removeAll();
        World world = gamePlayer.getBukkitPlayer().getWorld();
        for (Wall wall : box.walls) {
            drawWall(fakeBlockManager,world,wall,false);
        }
        drawFloor(fakeBlockManager,world,box.ceiling, teamColor.getTransparentBlock(), false);
        drawFloor(fakeBlockManager,world,box.floor, teamColor.getSolidBlock(), true);

        fakeBlockManager.update();
    }

    private void drawFloor() {
        FakeBlockManager fakeBlockManager = gamePlayer.getFakeBlockManager();
        fakeBlockManager.removeAll();
        World world = gamePlayer.getBukkitPlayer().getWorld();

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

    private void drawWall(FakeBlockManager manager, World world , Wall wall, boolean replaceSolid) {
        for (int xz = wall.minXZ; xz <= wall.maxXZ; xz++) {
            for (int y = wall.minY; y <= wall.maxY; y++) {
                int x = wall.getX(xz);
                int z = wall.getZ(xz);
                if (!replaceSolid && world.getBlockAt(x, y, z).isSolid())
                    continue;
                manager.addBlock(teamColor.getTransparentBlock(), world, x, y, z, border.blockChangesAllowed());
            }
        }
    }

    private void drawUpdate(List<Wall> oldWalls, List<Wall> newWalls, Floor oldCeiling, Floor newCeiling, Floor oldFloor, Floor newFloor) {
        //new walls
        World world = gamePlayer.getBukkitPlayer().getWorld();
        FakeBlockManager manager = gamePlayer.getFakeBlockManager();

        for(Wall newWall : newWalls) {
            drawWall(manager,world,newWall,false);
        }

        drawFloor(manager,world,newCeiling, teamColor.getTransparentBlock(),false);
        drawFloor(manager,world,newFloor, teamColor.getSolidBlock(),true);

        manager.update();
    }

    @Override
    public Border getBorder() {
        return border;
    }
}
