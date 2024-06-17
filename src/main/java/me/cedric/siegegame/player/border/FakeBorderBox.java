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

public class FakeBorderBox implements FakeBorder {
    private final Material transparent;
    private final Material solid;
    protected final Box box;
    protected final Floor floor;
    private boolean boxVisible;
    private final GamePlayer gamePlayer;
    private final Border border;
    private static final int MIN_DISTANCE = 30;

    public FakeBorderBox(GamePlayer gamePlayer, Team team) {
        TeamColor color = ColorUtil.getRelationalColor(gamePlayer.getTeam(), team);

        this.transparent = color.getTransparentBlock();
        this.solid = color.getSolidBlock();

        this.border = team.getSafeArea();
        BoundingBox box = border.getBoundingBox();
        this.box = new Box((int)box.getMinX(), (int)box.getMaxX(), (int)box.getMinZ(), (int)box.getMaxZ(), (int)box.getMinY() + 1, (int)box.getMaxY());
        this.floor = new Floor((int)box.getMinX(), (int)box.getMaxX(), (int)box.getMinZ(), (int)box.getMaxZ(), (int)box.getMinY());
        boxVisible = false;
        this.gamePlayer = gamePlayer;
    }

    @Override
    public void update(FakeBlockManager fakeBlockManager) {
        Location location = gamePlayer.getBukkitPlayer().getLocation();

        destroyFloor(fakeBlockManager);
        buildFloor(fakeBlockManager);

        if (shouldDisplay(border,location)) {
            if (boxVisible) return;
            boxVisible = true;
            buildBox(fakeBlockManager);
        } else {
            if (!boxVisible) return;
            boxVisible = false;
            destroyBox(fakeBlockManager);
        }
    }

    private boolean shouldDisplay(Border border, Location location) {
        if(border.isInverse()) {
            return shouldDisplayInverse(border,location);
        } else {
            return shouldDisplayNorm(border,location);
        }
    }

    private boolean shouldDisplayInverse(Border border, Location location) {
        BoundingBox borderBox = border.getBoundingBox();
        BoundingBox minBox = borderBox.clone().expand(MIN_DISTANCE);
        return !borderBox.isColliding(location) && minBox.isColliding(location);
    }

    private boolean shouldDisplayNorm(Border border, Location location) {
        BoundingBox borderBox = border.getBoundingBox();
        BoundingBox minBox = borderBox.clone().expand(-MIN_DISTANCE);
        return borderBox.isColliding(location) && !minBox.isColliding(location);
    }

    protected void destroyFloor(FakeBlockManager fakeBlockManager) {
        World world = gamePlayer.getBukkitPlayer().getWorld();
        for (int x = floor.minX; x <= floor.maxX; x++) {
            for (int z = floor.minZ; z <= floor.maxZ; z++) {
                int y = floor.y;
                fakeBlockManager.removeBlock(world, x, y, z);
            }
        }
    }

    public void destroyBox(FakeBlockManager fakeBlockManager) {
        World world = gamePlayer.getBukkitPlayer().getWorld();
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int y = box.minY; y <= box.maxY; y++) {
                for (int z = box.minZ; z <= box.maxZ; z++) {
                    fakeBlockManager.removeBlock(world, x, y, z);
                }
            }
        }
    }

    protected void buildFloor(FakeBlockManager fakeBlockManager) {
        World world = gamePlayer.getBukkitPlayer().getWorld();
        for (int x = floor.minX; x <= floor.maxX; x++) {
            for (int z = floor.minZ; z <= floor.maxZ; z++) {
                int y = floor.y;
                fakeBlockManager.addBlock(solid, world, x, y, z, border.blockChangesAllowed());
            }
        }
    }

    protected void buildBox(FakeBlockManager fakeBlockManager) {
        World world = gamePlayer.getBukkitPlayer().getWorld();
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int y = box.minY; y <= box.maxY; y++) {
                for (int z = box.minZ; z <= box.maxZ; z++) {
                    fakeBlockManager.addBlock(transparent, world, x, y, z, border.blockChangesAllowed());
                }
            }
        }
    }

    @Override
    public Border getBorder() {
        return border;
    }
}
