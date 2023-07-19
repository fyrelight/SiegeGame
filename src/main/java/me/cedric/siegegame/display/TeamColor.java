package me.cedric.siegegame.display;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

public interface TeamColor {
    TextColor getTextColor();
    Material getSolidBlock();
    Material getSoftBlock();
    Material getTransparentBlock();
    default String getMinimessage() {
        TextColor color = this.getTextColor();
        if (color instanceof NamedTextColor name) {
            return "<" + name + ">";
        }
        return "<" + color.asHexString() + ">";
    }

    static TeamColor of(TextColor textColor, Material hardBlock, Material softBlock, Material transparentBlock) {
        if (textColor == null) textColor = NamedTextColor.WHITE;
        if (hardBlock == null || !hardBlock.isBlock()) hardBlock = Material.WHITE_CONCRETE;
        if (softBlock == null || !softBlock.isBlock()) softBlock = Material.WHITE_WOOL;
        if (transparentBlock == null || !transparentBlock.isBlock()) transparentBlock = Material.WHITE_STAINED_GLASS;
        return new TeamColorImpl(textColor, hardBlock, softBlock, transparentBlock);
    }
}
