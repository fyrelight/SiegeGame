package me.cedric.siegegame.modules.abilityitems.items;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.modules.abilityitems.AbilityItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class MortyAntiQuickie implements AbilityItem {

    private NamespacedKey namespacedKey;

    public MortyAntiQuickie(Plugin plugin) {
        this.namespacedKey = new NamespacedKey(plugin, "siegegame");
    }

    @Override
    public void onStartGame(SiegeGamePlugin plugin) {
        namespacedKey = new NamespacedKey(plugin, "siegegame");
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta meta = item.getItemMeta();
        meta.itemName(getDisplayName());
        meta.lore(List.of(
                Component.text("Functions as a regular Totem of Undying.").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("Hold this to revive yourself and ").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false),
                Component.text("add absorption hearts").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
        ));
        meta.getPersistentDataContainer().set(getNamespacedKey(), PersistentDataType.STRING, getIdentifier());
        meta.setRarity(ItemRarity.EPIC);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Component getDisplayName() {
        return Component.text("Morty's Anti Quickdrop").color(NamedTextColor.LIGHT_PURPLE);
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    @Override
    public String getIdentifier() {
        return "morty-antiquickie";
    }
}
