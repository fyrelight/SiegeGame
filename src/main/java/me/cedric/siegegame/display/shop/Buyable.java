package me.cedric.siegegame.display.shop;

import me.cedric.siegegame.enums.Messages;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface Buyable {

    ItemStack getDisplayItem();

    int getPrice();

    boolean includesItem();

    default boolean handlePurchase(GamePlayer gamePlayer) {
        Player player = gamePlayer.getBukkitPlayer();


        if (!gamePlayer.hasTeam()) {
            player.sendMessage(Messages.ERROR_REQUIRES_HAVING_TEAM_TO_BUY);
            return false;
        }

        Team team = gamePlayer.getTeam();

        if (!team.getTerritory().isInside(player.getLocation())) {
            player.sendMessage(Messages.ERROR_REQUIRES_BEING_IN_CLAIMS);
            return false;
        }

        if (gamePlayer.isDead()) {
            player.sendMessage(Messages.ERROR_REQUIRES_BEING_ALIVE);
            return false;
        }

        if (player.getLevel() < getPrice()) {
            player.sendMessage(Messages.ERROR_REQUIRES_MORE_LEVELS);
            return false;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Messages.ERROR_REQUIRES_INVENTORY_SLOTS);
            return false;
        }

        return true;
    }

}
