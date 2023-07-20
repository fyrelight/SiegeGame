package me.cedric.siegegame.display;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.Locale;

public enum NamedTeamColor implements TeamColor {
    BLACK(NamedTextColor.BLACK, Material.BLACK_CONCRETE, Material.BLACK_WOOL, Material.BLACK_STAINED_GLASS),
    BLUE(NamedTextColor.DARK_BLUE, Material.BLUE_CONCRETE, Material.BLUE_WOOL, Material.BLUE_STAINED_GLASS),
    GREEN(NamedTextColor.DARK_GREEN, Material.GREEN_CONCRETE, Material.GREEN_WOOL, Material.GREEN_STAINED_GLASS),
    CYAN(NamedTextColor.DARK_AQUA, Material.CYAN_CONCRETE, Material.CYAN_WOOL, Material.CYAN_STAINED_GLASS),
    RED(NamedTextColor.RED, Material.RED_CONCRETE, Material.RED_WOOL, Material.RED_STAINED_GLASS),
    PURPLE(NamedTextColor.DARK_PURPLE, Material.PURPLE_CONCRETE, Material.PURPLE_WOOL, Material.PURPLE_STAINED_GLASS),
    ORANGE(NamedTextColor.GOLD, Material.ORANGE_CONCRETE, Material.ORANGE_WOOL, Material.ORANGE_STAINED_GLASS),
    LIGHT_GRAY(NamedTextColor.GRAY, Material.LIGHT_GRAY_CONCRETE, Material.LIGHT_GRAY_WOOL, Material.LIGHT_GRAY_STAINED_GLASS),
    GRAY(NamedTextColor.DARK_GRAY, Material.GRAY_CONCRETE, Material.GRAY_WOOL, Material.GRAY_STAINED_GLASS),
    LIME(NamedTextColor.GREEN, Material.LIME_CONCRETE, Material.LIME_WOOL, Material.LIME_STAINED_GLASS),
    LIGHT_BLUE(NamedTextColor.AQUA, Material.LIGHT_BLUE_CONCRETE, Material.LIGHT_BLUE_WOOL, Material.LIGHT_BLUE_STAINED_GLASS),
    PINK(NamedTextColor.LIGHT_PURPLE, Material.PINK_CONCRETE, Material.PINK_WOOL, Material.PINK_STAINED_GLASS),
    YELLOW(NamedTextColor.YELLOW, Material.YELLOW_CONCRETE, Material.YELLOW_WOOL, Material.YELLOW_STAINED_GLASS),
    WHITE(NamedTextColor.WHITE, Material.WHITE_CONCRETE, Material.WHITE_WOOL, Material.WHITE_STAINED_GLASS),
    // No close NamedTextColor for these colors
    MAGENTA(TextColor.color(0x9C299B), Material.MAGENTA_CONCRETE, Material.MAGENTA_WOOL, Material.MAGENTA_STAINED_GLASS),
    BROWN(TextColor.color(0x563117), Material.BROWN_CONCRETE, Material.BROWN_WOOL, Material.BROWN_STAINED_GLASS),
    ;

    private final TextColor textColor;
    private final Material hardBlock;
    private final Material softBlock;
    private final Material transparentBlock;

    NamedTeamColor(TextColor textColor, Material hardBlock, Material softBlock, Material transparentBlock) {
        this.textColor = textColor;
        this.hardBlock = hardBlock;
        this.softBlock = softBlock;
        this.transparentBlock = transparentBlock;
    }

    @Nullable public static NamedTeamColor matchNamedTextColor(@Nullable String name) {
        if (name == null) return null;
        try {
            return NamedTeamColor.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public TextColor getTextColor() {
        return textColor;
    }

    @Override
    public Material getSolidBlock() {
        return hardBlock;
    }

    @Override
    public Material getSoftBlock() {
        return softBlock;
    }

    @Override
    public Material getTransparentBlock() {
        return transparentBlock;
    }
}
