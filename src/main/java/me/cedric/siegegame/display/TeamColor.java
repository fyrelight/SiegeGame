package me.cedric.siegegame.display;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

public interface TeamColor {
    BossBar.Color getBossBarColor();
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

    static TeamColor of(BossBar.Color bossbarColor, TextColor textColor, Material hardBlock, Material softBlock, Material transparentBlock) {
        if (bossbarColor == null) bossbarColor = BossBar.Color.WHITE;
        if (textColor == null) textColor = NamedTextColor.WHITE;
        if (hardBlock == null || !hardBlock.isBlock()) hardBlock = Material.WHITE_CONCRETE;
        if (softBlock == null || !softBlock.isBlock()) softBlock = Material.WHITE_WOOL;
        if (transparentBlock == null || !transparentBlock.isBlock()) transparentBlock = Material.WHITE_STAINED_GLASS;
        return new TeamColorImpl(bossbarColor, textColor, hardBlock, softBlock, transparentBlock);
    }
}
