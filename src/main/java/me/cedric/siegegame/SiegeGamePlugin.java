package me.cedric.siegegame;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.cedric.siegegame.command.SpawnCommand;
import me.cedric.siegegame.command.kits.KitsCommand;
import me.cedric.siegegame.command.RallyCommand;
import me.cedric.siegegame.player.border.blockers.BlockChangePacketAdapter;
import me.cedric.siegegame.player.border.PlayerBorderListener;
import me.cedric.siegegame.command.ResourcesCommand;
import me.cedric.siegegame.command.SiegeGameCommand;
import me.cedric.siegegame.config.ConfigLoader;
import me.cedric.siegegame.config.GameConfig;
import me.cedric.siegegame.display.placeholderapi.SiegeGameExpansion;
import me.cedric.siegegame.player.PlayerListener;
import me.cedric.siegegame.model.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class SiegeGamePlugin extends JavaPlugin {
    private ConfigLoader configLoader;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("siegegame", "SiegeGame commands", List.of("sg", "siegeg"), new SiegeGameCommand(this));
            commands.register("resources", "Opens the resource menu or shop", List.of("r", "rs"), new ResourcesCommand(this));
            commands.register("rally", "Sets a waypoint at your current location. Requires lunar client", new RallyCommand(this));
            commands.register("kits", "Open kits menu", List.of("kit"), new KitsCommand(this));
            commands.register("town", List.of("t"), new SpawnCommand(this));
        });

        this.gameManager = new GameManager(this);
        this.configLoader = new ConfigLoader(this);

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerBorderListener(this), this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new BlockChangePacketAdapter(this));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new SiegeGameExpansion(this).register();

        configLoader.initializeAndLoad();

        if (getGameConfig().getStartGameOnServerStartup())
            Bukkit.getScheduler().runTaskLater(this, () -> getGameManager().startNextGame(), 1L);
    }

    @Override
    public void onDisable() {
        gameManager.endGame(true, false);
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public GameConfig getGameConfig() {
        return configLoader;
    }

    public ICombatLogX getCombatLogX() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin plugin = pluginManager.getPlugin("CombatLogX");
        return (ICombatLogX) plugin;
    }
}
