package me.cedric.siegegame.display.shop;

import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Buyable {

    ItemStack getDisplayItem();

    Purchase getPurchase();

    int getPrice();

    boolean includesItem();

    default boolean handlePurchase(GamePlayer gamePlayer) {
        Player player = gamePlayer.getBukkitPlayer();

        if (!gamePlayer.hasTeam()) {
            player.sendMessage(ChatColor.RED + "You need to be in a team to buy items");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return false;
        }

        Team team = gamePlayer.getTeam();

        if (!team.getTerritory().isInside(player.getLocation())) {
            player.sendMessage(ChatColor.RED + "You need to be inside claims to do this");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return false;
        }

        if (gamePlayer.isDead()) {
            player.sendMessage(ChatColor.RED + "You cannot do this while dead");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return false;
        }

        if (player.getLevel() < getPrice()) {
            player.sendMessage(ChatColor.RED + "You do not have enough levels to buy this");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return false;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "You do not have any empty inventory slots");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return false;
        }

        int levels = player.getLevel();
        player.setLevel(levels - getPrice());
        getPurchase().accept(gamePlayer);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        return true;
    }

}
