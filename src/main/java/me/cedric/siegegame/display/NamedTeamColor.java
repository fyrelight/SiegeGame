package me.cedric.siegegame.display;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.Locale;

public enum NamedTeamColor implements TeamColor {
    BLACK(BossBar.Color.PURPLE, NamedTextColor.BLACK, Material.BLACK_CONCRETE, Material.BLACK_WOOL, Material.BLACK_STAINED_GLASS),
    BLUE(BossBar.Color.BLUE, NamedTextColor.DARK_BLUE, Material.BLUE_CONCRETE, Material.BLUE_WOOL, Material.BLUE_STAINED_GLASS),
    GREEN(BossBar.Color.YELLOW, NamedTextColor.DARK_GREEN, Material.GREEN_CONCRETE, Material.GREEN_WOOL, Material.GREEN_STAINED_GLASS),
    CYAN(BossBar.Color.BLUE, NamedTextColor.DARK_AQUA, Material.CYAN_CONCRETE, Material.CYAN_WOOL, Material.CYAN_STAINED_GLASS),
    RED(BossBar.Color.RED, NamedTextColor.RED, Material.RED_CONCRETE, Material.RED_WOOL, Material.RED_STAINED_GLASS),
    PURPLE(BossBar.Color.PURPLE, NamedTextColor.DARK_PURPLE, Material.PURPLE_CONCRETE, Material.PURPLE_WOOL, Material.PURPLE_STAINED_GLASS),
    ORANGE(BossBar.Color.RED, NamedTextColor.GOLD, Material.ORANGE_CONCRETE, Material.ORANGE_WOOL, Material.ORANGE_STAINED_GLASS),
    LIGHT_GRAY(BossBar.Color.WHITE, NamedTextColor.GRAY, Material.LIGHT_GRAY_CONCRETE, Material.LIGHT_GRAY_WOOL, Material.LIGHT_GRAY_STAINED_GLASS),
    GRAY(BossBar.Color.WHITE, NamedTextColor.DARK_GRAY, Material.GRAY_CONCRETE, Material.GRAY_WOOL, Material.GRAY_STAINED_GLASS),
    LIME(BossBar.Color.YELLOW, NamedTextColor.GREEN, Material.LIME_CONCRETE, Material.LIME_WOOL, Material.LIME_STAINED_GLASS),
    LIGHT_BLUE(BossBar.Color.BLUE, NamedTextColor.AQUA, Material.LIGHT_BLUE_CONCRETE, Material.LIGHT_BLUE_WOOL, Material.LIGHT_BLUE_STAINED_GLASS),
    PINK(BossBar.Color.PINK, NamedTextColor.LIGHT_PURPLE, Material.PINK_CONCRETE, Material.PINK_WOOL, Material.PINK_STAINED_GLASS),
    YELLOW(BossBar.Color.YELLOW, NamedTextColor.YELLOW, Material.YELLOW_CONCRETE, Material.YELLOW_WOOL, Material.YELLOW_STAINED_GLASS),
    WHITE(BossBar.Color.WHITE, NamedTextColor.WHITE, Material.WHITE_CONCRETE, Material.WHITE_WOOL, Material.WHITE_STAINED_GLASS),
    // No close NamedTextColor for these colors
    MAGENTA(BossBar.Color.PURPLE, TextColor.color(0x9C299B), Material.MAGENTA_CONCRETE, Material.MAGENTA_WOOL, Material.MAGENTA_STAINED_GLASS),
    BROWN(BossBar.Color.PURPLE, TextColor.color(0x563117), Material.BROWN_CONCRETE, Material.BROWN_WOOL, Material.BROWN_STAINED_GLASS),
    ;

    private final BossBar.Color bossbarColor;
    private final TextColor textColor;
    private final Material hardBlock;
    private final Material softBlock;
    private final Material transparentBlock;

    NamedTeamColor(BossBar.Color bossbarColor, TextColor textColor, Material hardBlock, Material softBlock, Material transparentBlock) {
        this.bossbarColor = bossbarColor;
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
    public BossBar.Color getBossBarColor() {
        return bossbarColor;
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
