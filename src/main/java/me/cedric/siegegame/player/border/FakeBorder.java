package me.cedric.siegegame.player.border;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public interface FakeBorder {
    void update();
    void destroy();
    void create();
    Border getBorder();
    final class WallProjection {
        final int XZ;
        final int perpendicular;
        final int Y;
        final boolean xDimension;
        final boolean facingPositive;

        WallProjection(int XZ, int perpendicular, int Y, boolean xDimension, boolean facingPositive) {
            this.XZ = XZ;
            this.perpendicular = perpendicular;
            this.Y = Y;
            this.xDimension = xDimension;
            this.facingPositive = facingPositive;
        }

        public int getXZ() {
            return XZ;
        }

        public int getPerpendicular() {
            return perpendicular;
        }

        public int getY() {
            return Y;
        }

        public boolean xDimension() {
            return xDimension;
        }

        public boolean isFacingPositive() {
            return facingPositive;
        }
    }

    final class Box {
        final int minX;
        final int maxX;
        final int minZ;
        final int maxZ;
        final int minY;
        final int maxY;
        final Material floorMaterial;
        final Material ceilingMaterial;
        final Floor floor;
        final Floor ceiling;
        final List<Wall> walls;

        Box(int minX, int maxX, int minZ, int maxZ, int minY, int maxY, Material floor, Material ceiling) {
            this.minX = minX;
            this.maxX = maxX;
            this.minZ = minZ;
            this.maxZ = maxZ;
            this.minY = minY;
            this.maxY = maxY;
            this.floorMaterial = floor;
            this.ceilingMaterial = ceiling;
            this.walls = new ArrayList<>();
            createWalls();
            this.floor = new Floor(minX, maxX, minZ, maxZ, minY);
            this.ceiling = new Floor(minX, maxX, minZ, maxZ, maxY);
        }

        private void createWalls() {
            walls.add(new Wall(minX, maxX, minZ, minY + 1, maxY - 1, true, true));
            walls.add(new Wall(minX, maxX, maxZ, minY + 1, maxY - 1, true, false));
            walls.add(new Wall(minZ, maxZ, minX, minY + 1, maxY - 1, false, true));
            walls.add(new Wall(minZ, maxZ, maxX, minY + 1, maxY - 1, false, false));
        }

        public int getMinX() {
            return minX;
        }

        public int getMaxX() {
            return maxX;
        }

        public int getMinZ() {
            return minZ;
        }

        public int getMaxZ() {
            return maxZ;
        }

        public int getMinY() {
            return minY;
        }

        public int getMaxY() {
            return maxY;
        }

        public Material getFloorMaterial() {
            return floorMaterial;
        }

        public Material getCeilingMaterial() {
            return ceilingMaterial;
        }
    }

    final class Floor {
        final int minX;
        final int maxX;
        final int minZ;
        final int maxZ;
        final int y;

        Floor(int minX, int maxX, int minZ, int maxZ, int y) {
            this.minX = minX;
            this.maxX = maxX;
            this.minZ = minZ;
            this.maxZ = maxZ;
            this.y = y;
        }

        public int getMinX() {
            return minX;
        }

        public int getMaxX() {
            return maxX;
        }

        public int getMinZ() {
            return minZ;
        }

        public int getMaxZ() {
            return maxZ;
        }

        public int getY() {
            return y;
        }
    }

    final class Wall {
        final int minXZ;
        final int maxXZ;
        final int minY;
        final int maxY;
        final int perpendicular;
        //whether the wall is on the x-direction, that it the wall is facing
        //a z direction!
        final boolean xDimension;
        final boolean facingPositive;

        Wall(int minXZ, int maxXZ, int perpendicular, int minY, int maxY, boolean xDimension, boolean facingPositive) {
            this.minXZ = minXZ;
            this.maxXZ = maxXZ;
            this.minY = minY;
            this.maxY = maxY;
            this.perpendicular = perpendicular;
            this.xDimension = xDimension;
            this.facingPositive = facingPositive;
        }

        public int getMinXZ() {
            return minXZ;
        }

        public int getMaxXZ() {
            return maxXZ;
        }

        public int getMinY() {
            return minY;
        }

        public int getMaxY() {
            return maxY;
        }

        public boolean isxDimension() {
            return xDimension;
        }

        public int getPerpendicular() {
            return perpendicular;
        }

        public int getX(int xz) {
            if (xDimension)
                return xz;

            return perpendicular;
        }


        public int getZ(int xz) {
            if (xDimension)
                return perpendicular;

            return xz;
        }
    }
}
