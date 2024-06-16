package me.cedric.siegegame.player.kits;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.config.GameConfig;
import me.cedric.siegegame.display.shop.ShopGUI;
import me.cedric.siegegame.display.shop.ShopItem;
import me.cedric.siegegame.model.game.WorldGame;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Kit {

    private final SiegeGamePlugin plugin;
    private final String mapIdentifier;
    private final String rawString;
    private final UUID uuid;
    private List<ShopItem> contents = new ArrayList<>();

    public Kit(SiegeGamePlugin plugin, String mapIdentifier, String rawString, UUID kitUUID) {
        this.plugin = plugin;
        this.mapIdentifier = mapIdentifier;
        this.uuid = kitUUID;
        this.rawString = rawString;
    }

    public void setContents(ItemStack[] contents, WorldGame worldGame) {
        this.contents = contents(this.plugin.getGameConfig(), contents, worldGame);
    }

    public void populateFromRawString(WorldGame worldGame) {
        if (!contents.isEmpty())
            return;

        String[] items = rawString.split(",");
        ShopGUI shopGUI = worldGame.getShopGUI();
        List<ShopItem> shopItems = new ArrayList<>();

        for (String item : items) {
            String[] s = item.split("-");

            String itemID = s[0];
            int slot = Integer.parseInt(s[1]);

            if (itemID.equalsIgnoreCase("empty")) {
                shopItems.add(slot, null);
                continue;
            }

            ShopItem shopItem = shopGUI.getItem(itemID);

            if (shopItem == null || shopItem.getPrice() > 0)
                continue;

            shopItems.add(slot, shopGUI.getItem(itemID));
        }

        this.contents = shopItems;
    }

    public void setContents(List<ShopItem> contents) {
        this.contents = contents;
    }

    public List<ShopItem> getContents() {
        return new ArrayList<>(contents);
    }

    public ItemStack[] getInventoryContents() {
        List<ItemStack> items = new ArrayList<>();
        for (ShopItem shopItem : getContents()) {
            if (shopItem == null) {
                items.add(null);
                continue;
            }

            if (shopItem.includesExact())
                items.add(shopItem.getDisplayItem());
            else
                items.add(new ItemStack(shopItem.getDisplayItem().getType(), shopItem.getDisplayItem().getAmount()));
        }

        return items.toArray(ItemStack[]::new);
    }

    public String getMapIdentifier() {
        return mapIdentifier;
    }

    public String getRawString() {
        return rawString;
    }

    private static List<ShopItem> contents(GameConfig config, ItemStack[] items, WorldGame worldGame) {
        List<ShopItem> newList = new ArrayList<>();
        for (ItemStack item : items.clone()) {
            if (item == null || item.getType().equals(Material.AIR)) {
                newList.add(null);
                continue;
            }

            ShopItem shopItem = getShopItem(config, item, worldGame);
            if (shopItem == null || shopItem.getPrice() > 0) {
                newList.add(null);
                continue;
            }

            newList.add(shopItem);
        }

        return newList;
    }

    private static ShopItem getShopItem(GameConfig config, ItemStack item, WorldGame worldGame) {
        for (ShopItem shopItem : worldGame.getShopGUI().getShopItems()) {
            if (!shopItem.includesExact() && shopItem.getDisplayItem().getType().equals(item.getType()))
                return shopItem;

            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            NamespacedKey identifierKey = config.getNamespacedItemKey();

            if (!pdc.has(identifierKey, PersistentDataType.STRING)) continue;

            String identifier = pdc.get(identifierKey, PersistentDataType.STRING);
            String shopItemID = shopItem.getIdentifier();

            if (identifier.equalsIgnoreCase(shopItemID))
                return shopItem;
        }

        return null;
    }

    public UUID getKitUUID() {
        return uuid;
    }

    public static Kit fromInventory(SiegeGamePlugin plugin, ItemStack[] contents, WorldGame worldGame, String mapIdentifier) {
        List<ShopItem> shopItemContents = contents(plugin.getGameConfig(), contents, worldGame);
        StringBuilder items = new StringBuilder();

        int i = 0;
        for (ShopItem shopItem : shopItemContents) {
            if (shopItem == null)
                items.append("empty-").append(i);
            else
                items.append(shopItem.getIdentifier()).append("-").append(i);

            if (i < (contents.length - 1))
                items.append(",");

            i++;
        }

        return new Kit(plugin, mapIdentifier, items.toString(), UUID.randomUUID());
    }

    @Override
    public boolean equals(Object that) {
        if (that == null)
            return false;

        if (!(that instanceof Kit other))
            return false;

        return other.getMapIdentifier().equalsIgnoreCase(getMapIdentifier());
    }
}
