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
import org.bukkit.World;

import java.util.Objects;

public class FakeBorderSafeZone implements FakeBorder {
    private final Team team;
    private final TeamColor teamColor;
    private final Box box;
    private final Floor floor;
    private final GamePlayer gamePlayer;
    private final Border border;
    private final ICombatManager combatManager;
    private boolean isVisible;

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

        this.box = new Box(minX, maxX, minZ, maxZ, minY + 1, maxY);
        this.floor = new Floor(minX, maxX, minZ, maxZ, minY);

        ICombatLogX combatLogX = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
        this.combatManager = Objects.requireNonNull(combatLogX).getCombatManager();
    }


    @Override
    public void update() {
        if (shouldDisplay(gamePlayer)) {
            //if (isVisible) return;
            //isVisible = true;
            draw();
        } else {
            //if (!isVisible) return;
            //isVisible = false;
            destroy();
        }
    }

    private boolean shouldDisplay(GamePlayer gamePlayer) {
        if (!this.team.getPlayers().contains(gamePlayer)) return true;
        return !this.border.getBoundingBox().isColliding(gamePlayer.getBukkitPlayer().getLocation());
    }

    @Override
    public void destroy() {
        FakeBlockManager fakeBlockManager = gamePlayer.getFakeBlockManager();
        World world = gamePlayer.getBukkitPlayer().getWorld();

        //Destroy
        destroyBox(fakeBlockManager, world, box);
        destroyFloor(fakeBlockManager, world, floor);

        //Redraw
        drawFloor(fakeBlockManager, world, floor);

        fakeBlockManager.update();
    }

    private void destroyFloor(FakeBlockManager manager, World world, Floor floor) {
        for (int x = floor.minX; x <= floor.maxX; x++) {
            for (int z = floor.minZ; z <= floor.maxZ; z++) {
                manager.removeBlock(world, x, floor.y, z);
            }
        }
    }

    private void destroyBox(FakeBlockManager manager, World world, Box box) {
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int y = box.minY; y <= box.maxY; y++) {
                for (int z = box.minZ; z <= box.maxZ; z++) {
                    manager.removeBlock(world, x, y, z);
                }
            }
        }
    }

    private void draw() {
        FakeBlockManager fakeBlockManager = gamePlayer.getFakeBlockManager();
        World world = gamePlayer.getBukkitPlayer().getWorld();

        destroyBox(fakeBlockManager, world, box);
        destroyFloor(fakeBlockManager, world, floor);

        //Redraw
        drawBox(fakeBlockManager, world, box);
        drawFloor(fakeBlockManager, world, floor);

        fakeBlockManager.update();
    }

    private void drawFloor(FakeBlockManager manager, World world, Floor floor) {
        for (int x = floor.minX; x <= floor.maxX; x++) {
            for (int z = floor.minZ; z <= floor.maxZ; z++) {
                manager.addBlock(teamColor.getSolidBlock(), world, x, floor.y, z, true);
            }
        }
    }

    private void drawBox(FakeBlockManager manager, World world, Box box) {
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int y = box.minY; y <= box.maxY; y++) {
                for (int z = box.minZ; z <= box.maxZ; z++) {
                    manager.addBlock(teamColor.getTransparentBlock(), world, x, y, z, true);
                }
            }
        }
    }

    @Override
    public Border getBorder() {
        return border;
    }
}
