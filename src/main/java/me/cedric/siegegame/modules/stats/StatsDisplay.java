package me.cedric.siegegame.modules.stats;

import me.cedric.siegegame.enums.Messages;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.player.GamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class StatsDisplay {

    private static final int displayUntilTop = 10;

    public static void display(WorldGame worldGame, HashMap<UUID, Double> damageMap, HashMap<UUID, Integer> killMap) {
        List<Map.Entry<UUID, Double>> damageSorted = damageMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList());

        Collections.reverse(damageSorted);

        broadcastMessage(worldGame, Messages.STATS_HEADER);
        broadcastMessage(worldGame, Messages.STATS_TITLE);
        broadcastMessage(worldGame, Component.empty());

        int i = 1;
        for (Map.Entry<UUID, Double> damageEntry : damageSorted) {
            GamePlayer gamePlayer = worldGame.getPlayer(damageEntry.getKey());
            if (gamePlayer == null)
                continue;

            if (i > displayUntilTop)
                break;

            double damage = roundToHalf(damageMap.getOrDefault(gamePlayer.getUUID(), 0D));
            double hearts = damageToHearts(damage);

            Component toDisplay = Messages.STATS_PLACEMENT_FORMAT.asComponent()
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("%position%")
                            .replacement(i + "")
                            .build()
                    )
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("%damage%")
                            .replacement(damage + "")
                            .build()
                    )
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("%kills%")
                            .replacement((killMap.getOrDefault(gamePlayer.getUUID(), 0)) + "")
                            .build()
                    )
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("%hearts%")
                            .replacement(hearts + "")
                            .build()
                    )
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("%player%")
                            .replacement(gamePlayer.getBukkitPlayer().getName())
                            .build()
                    );
            broadcastMessage(worldGame, toDisplay);
            i++;
        }

        broadcastMessage(worldGame, Messages.STATS_HEADER);

    }

    private static void broadcastMessage(WorldGame worldGame, ComponentLike message) {
        broadcastMessage(worldGame, message.asComponent());
    }

    private static void broadcastMessage(WorldGame worldGame, Component message) {
        for (GamePlayer gamePlayer : worldGame.getPlayers()) {
            gamePlayer.getBukkitPlayer().sendMessage(message);
        }
    }

    private static double damageToHearts(double damage) {
        double hearts = damage / 2;
        return roundToHalf(hearts);
    }

    private static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }



}













