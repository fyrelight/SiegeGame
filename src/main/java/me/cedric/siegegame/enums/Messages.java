package me.cedric.siegegame.enums;

import me.cedric.siegegame.SiegeGamePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;


public enum Messages implements ComponentLike {
    NOTIFICATION_CLAIMS_ENTERED(Component.text("%s")),

    COMMAND_RALLY_SET(Component.text("Rally set.").color(NamedTextColor.GRAY)),

    ERROR_FORBIDDEN_IN_ENEMY_TERRITORY(Component.text("You cannot do this in enemy territory.").color(NamedTextColor.RED)),
    ERROR_REQUIRES_PERMISSION(Component.text("You do not have the required permissions for this command.").color(NamedTextColor.RED)),
    ERROR_REQUIRES_LUNAR_CLIENT(Component.text("Lunar Client is required to use this feature.").color(NamedTextColor.RED)),
    ERROR_REQUIRES_HAVING_TEAM_TO_BUY(Component.text("You need to be in a team to buy items").color(NamedTextColor.RED)),
    ERROR_REQUIRES_BEING_IN_CLAIMS(Component.text("You need to be inside claims to do this").color(NamedTextColor.RED)),
    ERROR_REQUIRES_BEING_ALIVE(Component.text("You cannot do this while dead").color(NamedTextColor.RED)),
    ERROR_REQUIRES_MORE_LEVELS(Component.text("You do not have enough levels to buy this").color(NamedTextColor.RED)),
    ERROR_REQUIRES_INVENTORY_SLOTS(Component.text("You do not have any empty inventory slots").color(NamedTextColor.RED)),

    STATS_TITLE(Component.text("MATCH LEADERBOARD").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)),
    STATS_HEADER(Component.empty()),
    STATS_PLACEMENT_FORMAT(Component.empty().color(NamedTextColor.WHITE)
            .append(Component.text("%position%. "))
            .append(Component.text("%player% ").color(NamedTextColor.YELLOW))
            .append(Component.text("Damage: ").color(NamedTextColor.GRAY))
            .append(Component.text("%hearts% "))
            .append(Component.text("❤ ").color(NamedTextColor.RED))
            .append(Component.text("| ").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("Kills: ").color(NamedTextColor.GRAY))
            .append(Component.text("%kills%"))
            )
    ),

    ;

    private final Component component;

    Messages(Component component) {
        this.component = component;
    }

    @Override
    public @NotNull Component asComponent() {
        return this.component;
    }

    public static TextComponent prefix(SiegeGamePlugin plugin) {
        return Component.text("[")
                .append(plugin.getGameConfig().getServerName())
                .append(Component.text("] ")).color(NamedTextColor.YELLOW);
    }

    public static TextComponent welcome(SiegeGamePlugin plugin) {
        return Component.text("Welcome to ").color(NamedTextColor.DARK_AQUA)
                .append(plugin.getGameConfig().getServerName())
                .append(Component.text(" Use "))
                .append(Component.text("/resources").color(NamedTextColor.GOLD))
                .append(Component.text(" for gear."));
    }
}
