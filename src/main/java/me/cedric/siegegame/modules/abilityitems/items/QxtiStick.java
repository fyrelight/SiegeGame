package me.cedric.siegegame.modules.abilityitems.items;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.modules.abilityitems.AbilityItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class QxtiStick implements AbilityItem {

    private NamespacedKey namespacedKey;

    public QxtiStick(Plugin plugin) {
        this.namespacedKey = new NamespacedKey(plugin, "siegegame");
    }

    @Override
    public void onStartGame(SiegeGamePlugin plugin) {
        namespacedKey = new NamespacedKey(plugin, "siegegame");
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        meta.itemName(getDisplayName());
        meta.lore(List.of(
                Component.text("Use this to knock enemies").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false),
                Component.text("away from you!").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
        ));
        meta.addEnchant(Enchantment.KNOCKBACK, 5, true);
        meta.getPersistentDataContainer().set(getNamespacedKey(), PersistentDataType.STRING, getIdentifier());
        meta.setRarity(ItemRarity.RARE);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Component getDisplayName() {
        return Component.text("qxti stick").color(NamedTextColor.YELLOW);
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    @Override
    public String getIdentifier() {
        return "qxti-stick";
    }
}
