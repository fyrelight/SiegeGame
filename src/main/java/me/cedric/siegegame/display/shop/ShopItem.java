package me.cedric.siegegame.display.shop;

import com.google.common.collect.ImmutableList;
import me.cedric.siegegame.model.game.WorldGame;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopItem implements Buyable {

    private final int price;
    private final int slot;
    private final ItemStack displayItem;
    private final boolean includesItem;
    private final boolean includesExact;
    private final List<String> commands;
    private String itemID;

    public ShopItem(String itemID, ItemStack displayItem, int price, int slot, boolean includesItem, boolean includesExact, List<String> commands) {
        this.price = price;
        this.slot = slot;
        this.includesItem = includesItem;
        this.includesExact = includesExact;
        this.displayItem = displayItem;
        this.itemID = itemID;
        this.commands = commands;
    }

    @Override
    public ItemStack getDisplayItem() {
        return displayItem.clone();
    }

    @Override
    public boolean includesItem() {
        return includesItem;
    }

    public int getPrice() {
        return price;
    }

    public int getSlot() {
        return slot;
    }

    public boolean includesExact() {
        return includesExact;
    }

    public String getIdentifier() {
        return itemID;
    }

    public List<String> getCommands() {
        return ImmutableList.copyOf(this.commands);
    }

}
