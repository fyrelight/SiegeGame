package me.cedric.siegegame.config;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;

import java.util.List;

public interface GameConfig {

    int getPointsPerKill();

    int getLevelsPerKill();

    int getPointsToEnd();

    int getRespawnTimer();

    int getSuperBreakerCooldown();

    int getSuperBreakerTimer();

    List<EntityType> getBlacklistedProjectiles();

    void reloadConfig();

    boolean getStartGameOnServerStartup();

    List<String> getMapIDs();

    NamespacedKey getNamespacedItemKey();

    NamespacedKey getNamespacedMapKey();

    NamespacedKey getNamespacedPropertiesKey();

    Component getServerName();

}
