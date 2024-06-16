package me.cedric.siegegame.modules.abilityitems;

import me.cedric.siegegame.SiegeGamePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FloatingItem {
    private static final Transformation ITEM_ROTATION_TRANSFORMATION = new Transformation(new Vector3f(0, 0, 0), new AxisAngle4f(1.570796F, 0, 0, 0), new Vector3f(0, 0, 0), new AxisAngle4f(0, 0, 0, 0));
    private final ItemDisplay hologram;

    public FloatingItem(SiegeGamePlugin plugin, Location location, ComponentLike title, ItemStack item) {
        this(plugin, location, title.asComponent(), item);
    }

    public FloatingItem(SiegeGamePlugin plugin, Location location, Component title, ItemStack item) {
        World world = location.getWorld();
        location.add(0.5, 0.75, 0.5);

        this.hologram = world.createEntity(location, ItemDisplay.class);

        Item itemEntity = world.dropItem(location, item);

        TextDisplay textDisplay = world.createEntity(location, TextDisplay.class);

        textDisplay.text(title);
        textDisplay.setBillboard(Display.Billboard.VERTICAL);

        Transformation textTransformation = textDisplay.getTransformation();
        textTransformation.getTranslation().set(0, 0.25, 0);
        textDisplay.setInterpolationDelay(-1);
        textDisplay.setInterpolationDuration(0);
        textDisplay.setTransformation(textTransformation);

        hologram.setItemStack(item);
        hologram.setGlowColorOverride(Color.YELLOW);
        hologram.setGlowing(true);

        itemEntity.setGlowing(true);
        itemEntity.setWillAge(false);
        itemEntity.setVelocity(new Vector());
        itemEntity.customName(title);
        itemEntity.setCustomNameVisible(true);
        itemEntity.setCanMobPickup(false);
        itemEntity.setGravity(false);

        //hologram.spawnAt(location);
        //textDisplay.spawnAt(location);

        Matrix4f mat = new Matrix4f();

        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (!hologram.isValid()) { // display was removed from the world, abort task
                task.cancel();
                return;
            }

            hologram.setTransformationMatrix(mat.rotateY(((float) Math.toRadians(180)) + 0.1F /* prevent the client from interpolating in reverse */));
            hologram.setInterpolationDelay(0); // no delay to the interpolation
            hologram.setInterpolationDuration(63); // set the duration of the interpolated rotation
        }, 1 /* delay the initial transformation by one tick from display creation */, 63);

        //hologram.addPassenger(textDisplay);
    }

    public boolean isCreated() {
        return (hologram != null && hologram.isValid());
    }

    public void setLocation(Location location) {
        if (isCreated())
            hologram.teleport(location);
    }
}
