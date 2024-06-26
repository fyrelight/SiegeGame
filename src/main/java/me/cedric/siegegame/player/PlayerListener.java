package me.cedric.siegegame.player;

import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.display.shop.ShopGUI;
import me.cedric.siegegame.display.shop.ShopItem;
import me.cedric.siegegame.enums.Messages;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.util.BoundingBox;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

public class PlayerListener implements Listener {

    public SiegeGamePlugin plugin;

    public PlayerListener(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();
        if (match == null) return;

        WorldGame worldGame = match.getWorldGame();
        ShopGUI shopGUI = worldGame.getShopGUI();

        if (event.getClickedInventory() == null) return;
        if (!shopGUI.isInventory(event.getInventory())) return;

        Inventory playerInventory = player.getInventory();
        if (event.getClickedInventory().equals(playerInventory)) {
            if (event.isShiftClick()) {
                if (event.getCurrentItem() == null) return;
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                playerInventory.clear(event.getSlot());
                event.setCancelled(true);
            }
        } else if (shopGUI.isInventory(event.getClickedInventory())) {
            Inventory openInventory = event.getInventory();
            if (!shopGUI.isInventory(openInventory)) return;

            GamePlayer gamePlayer = worldGame.getPlayer(event.getWhoClicked().getUniqueId());
            if (gamePlayer != null) {
                ItemMeta meta = event.getCurrentItem().getItemMeta();

                String identifier = meta.getPersistentDataContainer().get(plugin.getGameConfig().getNamespacedItemKey(), PersistentDataType.STRING);
                ShopItem item = shopGUI.getItem(identifier);
                if (item == null) {
                    event.setCancelled(true);
                    return;
                }

                Sound sound;
                if (item.handlePurchase(gamePlayer)) {
                    int levels = player.getLevel();
                    player.setLevel(levels - item.getPrice());

                    if (item.includesItem()) {
                        if (item.includesExact()) playerInventory.addItem(event.getCurrentItem().clone());
                        else playerInventory.addItem(new ItemStack(event.getCurrentItem().getType(), event.getCurrentItem().getAmount()));
                    }

                    for (String command : item.getCommands()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                    sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
                } else sound = Sound.ENTITY_VILLAGER_NO;
                gamePlayer.getBukkitPlayer().playSound(gamePlayer.getBukkitPlayer().getLocation(), sound, 1, 1);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();
        if (match != null) {
            match.getWorldGame().addPlayer(player.getUniqueId());
            GamePlayer gamePlayer = match.getWorldGame().getPlayer(player.getUniqueId());

            match.getWorldGame().assignTeam(gamePlayer);

            if (gamePlayer.hasTeam()) {
                player.teleport(gamePlayer.getTeam().getSafeSpawn());
            }

            gamePlayer.grantNightVision();
        }

        player.getInventory().clear();
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setLevel(0);
        player.sendMessage(Messages.welcome(plugin));

        plugin.getGameManager().getKitStorage().load(player, match == null ? null : match.getWorldGame());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match != null)
            match.getWorldGame().removePlayer(player.getUniqueId());

        plugin.getGameManager().getKitStorage().unload(player.getUniqueId());

        for (PotionEffect potionEffect : player.getActivePotionEffects())
            player.removePotionEffect(potionEffect.getType());
    }

    @EventHandler
    public void onXP(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {

        Player killer = event.getPlayer().getKiller();

        if (killer == null)
            return;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        GamePlayer killerGamePlayer = match.getWorldGame().getPlayer(killer.getUniqueId());
        killer.playSound(killer.getLocation(), "entity.experience_orb.pickup", 1.0F, 0F);

        if (killerGamePlayer == null)
            return;

        if (!killerGamePlayer.hasTeam())
            return;

        Team team = killerGamePlayer.getTeam();

        for (GamePlayer player : team.getPlayers()) {
            Player bukkitPlayer = player.getBukkitPlayer();

            if (bukkitPlayer == null)
                continue;

            int levels = bukkitPlayer.getLevel();

            bukkitPlayer.setLevel(levels + plugin.getGameConfig().getLevelsPerKill());
        }

        team.addPoints(plugin.getGameConfig().getPointsPerKill());
        match.getWorldGame().updateAllScoreboards();

        GamePlayer dead = match.getWorldGame().getPlayer(event.getPlayer().getUniqueId());

        if (dead == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                GamePlayer gamePlayer = match.getWorldGame().getPlayer(player.getUniqueId());
                if (gamePlayer == null)
                    continue;
                gamePlayer.getDisplayer().displayCombatLogKill(event.getPlayer().getName());
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            GamePlayer gamePlayer = match.getWorldGame().getPlayer(player.getUniqueId());
            if (gamePlayer == null)
                continue;
            gamePlayer.getDisplayer().displayKill(dead, killerGamePlayer);
        }

        if (team.getPoints() >= plugin.getGameConfig().getPointsToEnd())
            plugin.getGameManager().startNextGame();
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager))
            return;

        if (!(event.getEntity() instanceof Player player))
            return;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null) {
            event.setCancelled(true);
            return;
        }

        GamePlayer damagerGamePlayer = match.getWorldGame().getPlayer(damager.getUniqueId());
        GamePlayer gamePlayer = match.getWorldGame().getPlayer(player.getUniqueId());

        if (damagerGamePlayer == null || gamePlayer == null)
            return;

        if (damagerGamePlayer.hasTeam() && gamePlayer.hasTeam() && damagerGamePlayer.getTeam().equals(gamePlayer.getTeam()))
            event.setCancelled(true);

        if (!gamePlayer.hasTeam() || !damagerGamePlayer.hasTeam())
            return;

        Team damagerTeam = damagerGamePlayer.getTeam();
        Team team = gamePlayer.getTeam();

        BoundingBox damagerSafeArea = damagerTeam.getSafeArea().getBoundingBox();
        BoundingBox teamSafeArea = team.getSafeArea().getBoundingBox();

        if (damagerSafeArea.isColliding(damager.getLocation()) || damagerSafeArea.isColliding(player.getLocation())
                || teamSafeArea.isColliding(damager.getLocation()) || teamSafeArea.isColliding(player.getLocation())) {
            // They are either both in the safe area or one is in there - dont care which
            // If both are tag simply allow pvp
            ICombatManager combatManager = plugin.getCombatLogX().getCombatManager();
            if (!(combatManager.isInCombat(damager) && combatManager.isInCombat(player)))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        if (event.getCause().equals(EntityPotionEffectEvent.Cause.BEACON))
            event.setCancelled(true);
    }

}
