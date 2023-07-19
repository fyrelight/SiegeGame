package me.cedric.siegegame.display.shop;

import org.bukkit.inventory.ItemStack;

public class ShopItem implements Buyable {

    private Purchase purchase;
    private final int price;
    private final int slot;
    private final ItemStack displayItem;
    private final boolean includesItem;
    private final boolean includesNBT;

    public ShopItem(Purchase purchase, ItemStack displayItem, int price, int slot, boolean includesItem, boolean includesNBT) {
        this.purchase = purchase;
        this.price = price;
        this.slot = slot;
        this.includesItem = includesItem;
        this.includesNBT = includesNBT;
        this.displayItem = displayItem;
    }

    @Override
    public ItemStack getDisplayItem() {
        return displayItem.clone();
    }

    @Override
    public Purchase getPurchase() {
        return purchase;
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

    public boolean includesNBT() {
        return includesNBT;
    }
}
