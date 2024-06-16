package me.cedric.siegegame.display.shop;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI {

    private final List<ShopItem> shopItems = new ArrayList<>();
    private String guiName = "Shop";
    private Inventory chestGui;

    public ShopGUI() {
        createGUI();
    }

    public void addItem(ShopItem button) {
        shopItems.add(button);
        this.chestGui.setItem(button.getSlot(), button.getDisplayItem());
    }

    public void removeItem(ItemStack item) {
        shopItems.removeIf(shopItem -> shopItem.getDisplayItem().equals(item));
    }

    public List<ShopItem> getShopItems() {
        return new ArrayList<>(shopItems);
    }

    private void createGUI() {
        int rows = 3;
        this.chestGui = Bukkit.createInventory(null, rows * 9, guiName);
    }

    public void clear() {
        this.chestGui.clear();
    }

    public void setGUIName(String guiName) {
        this.guiName = guiName;
    }

    public void show(HumanEntity who) {
        who.openInventory(this.chestGui);
    }

    public ShopItem getItem(String id) {
        return shopItems.stream().filter(shopItem -> shopItem.getIdentifier().equalsIgnoreCase(id)).findAny().orElse(null);
    }

    public boolean isInventory(Inventory inventory) {
        return this.chestGui.equals(inventory);
    }

}
