package me.cedric.siegegame.display;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

public interface TeamColor {
    TextColor getTextColor();
    Material getHardBlock();
    Material getSoftBlock();
    Material getTransparentBlock();
}
