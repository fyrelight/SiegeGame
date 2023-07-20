package me.cedric.siegegame.display;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

public class TeamColorImpl implements TeamColor {
    private final TextColor textColor;
    private final Material hardBlock;
    private final Material softBlock;
    private final Material transparentBlock;

    protected TeamColorImpl(TextColor textColor, Material hardBlock, Material softBlock, Material transparentBlock) {
        this.textColor = textColor;
        this.hardBlock = hardBlock;
        this.softBlock = softBlock;
        this.transparentBlock = transparentBlock;
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
