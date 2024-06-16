package me.cedric.siegegame.display;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

public class TeamColorImpl implements TeamColor {
    private final BossBar.Color bossbarColor;
    private final TextColor textColor;
    private final Material hardBlock;
    private final Material softBlock;
    private final Material transparentBlock;

    protected TeamColorImpl(BossBar.Color bossbarColor, TextColor textColor, Material hardBlock, Material softBlock, Material transparentBlock) {
        this.bossbarColor = bossbarColor;
        this.textColor = textColor;
        this.hardBlock = hardBlock;
        this.softBlock = softBlock;
        this.transparentBlock = transparentBlock;
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
