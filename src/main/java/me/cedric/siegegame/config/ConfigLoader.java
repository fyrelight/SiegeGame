package me.cedric.siegegame.config;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.display.NamedTeamColor;
import me.cedric.siegegame.display.TeamColor;
import me.cedric.siegegame.player.border.Border;
import me.cedric.siegegame.player.border.TeamBorder;
import me.cedric.siegegame.util.BoundingBox;
import me.cedric.siegegame.display.shop.ShopItem;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.model.teams.territory.Polygon;
import me.cedric.siegegame.model.teams.territory.Territory;
import me.cedric.siegegame.model.teams.territory.Vector2D;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.map.GameMap;
import me.cedric.siegegame.model.map.FileMapLoader;
import me.cedric.siegegame.model.teams.TeamFactory;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;

import java.awt.Color;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class ConfigLoader implements GameConfig {

    private final SiegeGamePlugin plugin;

    private FileConfiguration mapsYml;
    private static final String MAPS_SECTION_KEY = "maps";
    private static final String MAPS_SECTION_WORLD_NAME_KEY = "worldname";
    private static final String MAPS_SECTION_WORLD_DISPLAY_NAME_KEY = "world-display-name";
    private static final String MAPS_SECTION_DEFAULT_SPAWN_KEY = "defaultspawn";
    private static final String MAPS_SECTION_DEFAULT_SPAWN_X = "x";
    private static final String MAPS_SECTION_DEFAULT_SPAWN_Y = "y";
    private static final String MAPS_SECTION_DEFAULT_SPAWN_Z = "z";
    private static final String MAPS_SECTION_MAP_MAPBORDER_KEY = "worldborder";
    private static final String MAPS_SECTION_MAP_MAPBORDER_MATERIAL_KEY = "material";
    private static final String MAPS_SECTION_MAP_MAPBORDER_X1_KEY = "x1";
    private static final String MAPS_SECTION_MAP_MAPBORDER_Y1_KEY = "y1";
    private static final String MAPS_SECTION_MAP_MAPBORDER_Z1_KEY = "z1";
    private static final String MAPS_SECTION_MAP_MAPBORDER_X2_KEY = "x2";
    private static final String MAPS_SECTION_MAP_MAPBORDER_Y2_KEY = "y2";
    private static final String MAPS_SECTION_MAP_MAPBORDER_Z2_KEY = "z2";
    private static final String MAPS_SECTION_WORLD_TEAMS_KEY = "teams";
    private static final String MAPS_SECTION_WORLD_TEAMS_NAME = "name";
    private static final String MAPS_SECTION_WORLD_TEAMS_COLOR = "color";
    private static final String MAPS_SECTION_WORLD_TEAMS_BOSSBAR_COLOR = "bossbar_color";
    private static final String MAPS_SECTION_WORLD_TEAMS_SOLID = "solid_block";
    private static final String MAPS_SECTION_WORLD_TEAMS_SOFT = "soft_block";
    private static final String MAPS_SECTION_WORLD_TEAMS_TRANSPARENT = "transparent_block";
    private static final String MAPS_SECTION_TEAMS_SPAWN = "spawn-area";
    private static final String MAPS_SECTION_TEAMS_SPAWN_X1 = "x1";
    private static final String MAPS_SECTION_TEAMS_SPAWN_Y1 = "y1";
    private static final String MAPS_SECTION_TEAMS_SPAWN_Z1 = "z1";
    private static final String MAPS_SECTION_TEAMS_SPAWN_X2 = "x2";
    private static final String MAPS_SECTION_TEAMS_SPAWN_Y2 = "y2";
    private static final String MAPS_SECTION_TEAMS_SPAWN_Z2 = "z2";
    private static final String MAPS_SECTION_TEAMS_SAFE_SPAWN = "safe-spawn";
    private static final String MAPS_SECTION_TEAMS_SAFE_SPAWN_X1 = "x";
    private static final String MAPS_SECTION_TEAMS_SAFE_SPAWN_Y1 = "y";
    private static final String MAPS_SECTION_TEAMS_SAFE_SPAWN_Z1 = "z";
    private static final String MAPS_SECTION_TEAMS_SAFE_SPAWN_YAW = "yaw";
    private static final String MAPS_SECTION_TEAMS_SAFE_SPAWN_PITCH = "pitch";
    private static final String MAPS_SECTION_TERRITORY_KEY = "territory";

    private FileConfiguration shopYml;
    private static final String SHOP_SECTION_KEY = "shop";
    private static final String SHOP_SECTION_SHOP_NAME_KEY = "shop-name";
    private static final String SHOP_SECTION_MATERIAL_KEY = "material";
    private static final String SHOP_SECTION_SLOT_KEY = "slot";
    private static final String SHOP_SECTION_PRICE_KEY = "price";
    private static final String SHOP_SECTION_DISPLAY_NAME_KEY = "display-name";
    private static final String SHOP_SECTION_LORE_KEY = "lore";
    private static final String SHOP_SECTION_ENCHANTMENTS_KEY = "enchantments";
    private static final String SHOP_SECTION_HIDE_ITEM_FLAGS_KEY = "item-flags";
    private static final String SHOP_SECTION_COMMAND_LIST_KEY = "commands";
    private static final String SHOP_SECTION_CUSTOM_NBT = "custom-nbt";
    private static final String SHOP_SECTION_INCLUDES_ITEM = "includes-item";
    private static final String SHOP_SECTION_INCLUDES_ITEM_EXACT = "includes-item-exact";
    private static final String SHOP_SECTION_POTION_EFFECTS_KEY = "potion-effects";
    private static final String SHOP_SECTION_POTION_COLOR_KEY = "potion-color";
    private static final String SHOP_SECTION_AMOUNT_KEY = "amount";
    private static final String SHOP_SECTION_SELECTED_MAPS = "maps";

    private FileConfiguration configYml;
    private static final String CONFIG_POINTS_PER_KILL_KEY = "points-per-kill";
    private static final String CONFIG_POINTS_TO_END_KEY = "points-to-end";
    private static final String CONFIG_LEVELS_PER_KILL_KEY = "levels-per-kill";
    private static final String RESPAWN_TIMER_KEY = "respawn-timer";
    private static final String DEATH_COMMANDS_KEY = "death-commands";
    private static final String RESPAWN_COMMANDS_KEY = "respawn-commands";
    private static final String START_COMMANDS_KEY = "start-game-commands";
    private static final String END_COMMANDS_KEY = "end-game-commands";
    private static final String BLACKLISTED_PROJECTILES_KEY = "blacklisted-projectiles";
    private static final String SUPER_BREAKER_TIMER = "super-breaker-timer";
    private static final String START_GAME_ON_STARTUP = "start-game-on-server-start";
    private static final String SUPER_BREAKER_COOLDOWN = "super-breaker-cooldown";

    private final NamespacedKey namespacedItemKey;
    private final NamespacedKey namespacedMapKey;
    private final NamespacedKey namespacedPropertiesKey;

    public ConfigLoader(SiegeGamePlugin plugin) {
        this.plugin = plugin;
        this.namespacedItemKey = new NamespacedKey(plugin, "siegegame-item");
        this.namespacedMapKey = new NamespacedKey(plugin, "siegegame-map");
        this.namespacedPropertiesKey = new NamespacedKey(plugin, "siegegame-properties");
    }

    public void initializeAndLoad() {

        try {
            setupConfig();
        } catch (Exception x) {
            x.printStackTrace();
            return;
        }

        if (!mapsYml.isConfigurationSection(MAPS_SECTION_KEY) || !shopYml.isConfigurationSection(SHOP_SECTION_KEY)) {
            cryAndDisable();
            return;
        }

        loadMaps();

        plugin.getLogger().info("Config loaded.");
    }

    private void loadMaps() {
        ConfigurationSection section = mapsYml.getConfigurationSection(MAPS_SECTION_KEY);

        for (String mapKey : mapsYml.getConfigurationSection(MAPS_SECTION_KEY).getKeys(false)) {

            ConfigurationSection mapSection = section.getConfigurationSection(mapKey);

            String worldName = mapSection.getString(MAPS_SECTION_WORLD_NAME_KEY);

            if (worldName == null) {
                plugin.getLogger().severe("Could not retrieve world name for map key " + mapKey + " - Skipping!");
                continue;
            }

            loadWorld(worldName, mapSection, mapKey);
        }

        plugin.getGameManager().shuffleQueue();
        plugin.getLogger().info("Maps loaded.");
    }

    public void loadWorld(String worldName, ConfigurationSection section, String mapID) {
        ConfigurationSection defaultSpawnSection = section.getConfigurationSection(MAPS_SECTION_DEFAULT_SPAWN_KEY);
        int x = defaultSpawnSection.getInt(MAPS_SECTION_DEFAULT_SPAWN_X);
        int y = defaultSpawnSection.getInt(MAPS_SECTION_DEFAULT_SPAWN_Y);
        int z = defaultSpawnSection.getInt(MAPS_SECTION_DEFAULT_SPAWN_Z);
        Location defaultSpawn = new Location(null, x, y, z);

        ConfigurationSection worldBorderSection = section.getConfigurationSection(MAPS_SECTION_MAP_MAPBORDER_KEY);
        String materialName = worldBorderSection.getString(MAPS_SECTION_MAP_MAPBORDER_MATERIAL_KEY);
        int x1 = worldBorderSection.getInt(MAPS_SECTION_MAP_MAPBORDER_X1_KEY);
        int y1 = worldBorderSection.getInt(MAPS_SECTION_MAP_MAPBORDER_Y1_KEY);
        int z1 = worldBorderSection.getInt(MAPS_SECTION_MAP_MAPBORDER_Z1_KEY);

        int x2 = worldBorderSection.getInt(MAPS_SECTION_MAP_MAPBORDER_X2_KEY);
        int y2 = worldBorderSection.getInt(MAPS_SECTION_MAP_MAPBORDER_Y2_KEY);
        int z2 = worldBorderSection.getInt(MAPS_SECTION_MAP_MAPBORDER_Z2_KEY);

        String displayName = section.getString(MAPS_SECTION_WORLD_DISPLAY_NAME_KEY);

        Vector corner1Vector = new Vector(x1, y1, z1);
        Vector corner2Vector = new Vector(x2, y2, z2);

        FileMapLoader fileMapLoader = new FileMapLoader(new File(Bukkit.getWorldContainer().getParentFile(), worldName));
        Border border = new Border(new BoundingBox(fileMapLoader.getWorld(), corner1Vector, corner2Vector));
        border.setCanLeave(false);
        border.setAllowBlockChanges(false);
        border.setInverse(false);

        Material material = null;
        if (materialName != null) material = Material.matchMaterial(materialName);
        if (material == null || !material.isBlock()) material = Material.RED_STAINED_GLASS;

        GameMap gameMap = new GameMap(fileMapLoader, displayName, new HashSet<>(), border, defaultSpawn, material);
        WorldGame worldGame = new WorldGame(plugin, mapID);

        ConfigurationSection teamsSection = section.getConfigurationSection(MAPS_SECTION_WORLD_TEAMS_KEY);

        List<ShopItem> shopItems = loadShop(shopYml.getConfigurationSection(SHOP_SECTION_KEY), worldName);
        String shopName = shopYml.getString(SHOP_SECTION_SHOP_NAME_KEY);
        worldGame.getShopGUI().setGUIName(ChatColor.translateAlternateColorCodes('&', shopName));
        shopItems.stream().forEach(shopItem -> worldGame.getShopGUI().addItem(shopItem));

        List<TeamFactory> factories = loadTeams(worldGame, gameMap, teamsSection);
        factories.forEach(gameMap::addTeam);
        factories.forEach(teamFactory -> worldGame.addTeam(new Team(worldGame, teamFactory)));

        plugin.getGameManager().addGame(new SiegeGameMatch(plugin, worldGame, gameMap));
    }

    private List<ShopItem> loadShop(ConfigurationSection section, String worldName) {
        List<ShopItem> shopItems = new ArrayList<>();

        for (String key : section.getKeys(false)) {
            ConfigurationSection configSection = section.getConfigurationSection(key);
            List<String> maps = configSection.getStringList(SHOP_SECTION_SELECTED_MAPS);

            if (!maps.contains("all") && !maps.contains(worldName))
                continue;

            String material = configSection.getString(SHOP_SECTION_MATERIAL_KEY);
            int slot = configSection.getInt(SHOP_SECTION_SLOT_KEY);
            int price = configSection.getInt(SHOP_SECTION_PRICE_KEY);
            int amount = configSection.getInt(SHOP_SECTION_AMOUNT_KEY);
            String displayName = configSection.getString(SHOP_SECTION_DISPLAY_NAME_KEY);
            List<String> listOfLore = configSection.getStringList(SHOP_SECTION_LORE_KEY);
            List<String> itemFlags = configSection.getStringList(SHOP_SECTION_HIDE_ITEM_FLAGS_KEY);
            List<String> enchantments = configSection.getStringList(SHOP_SECTION_ENCHANTMENTS_KEY);
            List<String> commands = configSection.getStringList(SHOP_SECTION_COMMAND_LIST_KEY);
            List<String> customProperties = configSection.getStringList(SHOP_SECTION_CUSTOM_NBT);
            boolean includesItem = Boolean.parseBoolean(configSection.getString(SHOP_SECTION_INCLUDES_ITEM));
            boolean includesItemExact = Boolean.parseBoolean(configSection.getString(SHOP_SECTION_INCLUDES_ITEM));

            List<Component> lore = new ArrayList<>();
            for (String s : listOfLore)
                lore.add(MiniMessage.miniMessage().deserialize(s).decoration(TextDecoration.ITALIC, false));

            if (material == null) continue;
            Material material1 = Material.matchMaterial(material);
            if (material1 == null) continue;



            ItemStack item = new ItemStack(material1, amount);
            ItemMeta meta = item.getItemMeta();
            if (displayName != null) meta.displayName(MiniMessage.miniMessage().deserialize(displayName).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);

            for (String flag : itemFlags) {
                meta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
            }

            if (meta instanceof PotionMeta potionMeta) {
                for (String s : configSection.getStringList(SHOP_SECTION_POTION_EFFECTS_KEY)) {
                    String[] args = s.split(",");

                    PotionEffectType type = Registry.POTION_EFFECT_TYPE.get(NamespacedKey.minecraft(args[0]));
                    int duration = Integer.parseInt(args[1]) * 20;
                    int amplifier = Integer.parseInt(args[2]);
                    boolean ambient = Boolean.parseBoolean(args[3]);
                    boolean particles = Boolean.parseBoolean(args[4]);
                    boolean icon = Boolean.parseBoolean(args[5]);

                    PotionEffect effect = new PotionEffect(type, duration, amplifier, ambient, particles, icon);
                    potionMeta.addCustomEffect(effect, true);
                }

                potionMeta.setColor(bukkitColor(configSection.getString(SHOP_SECTION_POTION_COLOR_KEY)));
            }

            for (String enchantment : enchantments) {
                String[] s = enchantment.split(";");
                Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(s[0]));
                int level = Integer.parseInt(s[1]);
                meta.addEnchant(ench, level, true);
            }

            meta.getPersistentDataContainer().set(getNamespacedItemKey(), PersistentDataType.STRING, key);
            meta.getPersistentDataContainer().set(getNamespacedMapKey(), PersistentDataType.STRING, worldName);
            meta.getPersistentDataContainer().set(getNamespacedPropertiesKey(), PersistentDataType.LIST.strings(), customProperties);

            item.setItemMeta(meta);

            ShopItem button = new ShopItem(key, item.clone(), price, slot, includesItem, includesItemExact, commands);

            shopItems.add(button);
        }

        return shopItems;
    }

    private List<TeamFactory> loadTeams(WorldGame worldGame, GameMap gameMap, ConfigurationSection section) {
        List<TeamFactory> factories = new ArrayList<>();
        for (String key : section.getKeys(false)) {

            ConfigurationSection currentTeamSection = section.getConfigurationSection(key);
            String name = currentTeamSection.getString(MAPS_SECTION_WORLD_TEAMS_NAME);
            String colorName = currentTeamSection.getString(MAPS_SECTION_WORLD_TEAMS_COLOR);
            String bossbarColorName = currentTeamSection.getString(MAPS_SECTION_WORLD_TEAMS_BOSSBAR_COLOR);
            String solidName = currentTeamSection.getString(MAPS_SECTION_WORLD_TEAMS_SOLID);
            String softName = currentTeamSection.getString(MAPS_SECTION_WORLD_TEAMS_SOFT);
            String transparentName = currentTeamSection.getString(MAPS_SECTION_WORLD_TEAMS_TRANSPARENT);

            ConfigurationSection spawnAreaSection = currentTeamSection.getConfigurationSection(MAPS_SECTION_TEAMS_SPAWN);
            int x1 = spawnAreaSection.getInt(MAPS_SECTION_TEAMS_SPAWN_X1);
            int y1 = spawnAreaSection.getInt(MAPS_SECTION_TEAMS_SPAWN_Y1);
            int z1 = spawnAreaSection.getInt(MAPS_SECTION_TEAMS_SPAWN_Z1);
            int x2 = spawnAreaSection.getInt(MAPS_SECTION_TEAMS_SPAWN_X2);
            int y2 = spawnAreaSection.getInt(MAPS_SECTION_TEAMS_SPAWN_Y2);
            int z2 = spawnAreaSection.getInt(MAPS_SECTION_TEAMS_SPAWN_Z2);
            TeamBorder safeArea = new TeamBorder(new BoundingBox(null, x1, y1, z1, x2, y2, z2));
            safeArea.setAllowBlockChanges(false);
            safeArea.setInverse(true);

            ConfigurationSection safeSpawnSection = currentTeamSection.getConfigurationSection(MAPS_SECTION_TEAMS_SAFE_SPAWN);
            int safeSpawnX = safeSpawnSection.getInt(MAPS_SECTION_TEAMS_SAFE_SPAWN_X1);
            int safeSpawnY = safeSpawnSection.getInt(MAPS_SECTION_TEAMS_SAFE_SPAWN_Y1);
            int safeSpawnZ = safeSpawnSection.getInt(MAPS_SECTION_TEAMS_SAFE_SPAWN_Z1);
            float yaw = safeSpawnSection.getInt(MAPS_SECTION_TEAMS_SAFE_SPAWN_YAW);
            float pitch = safeSpawnSection.getInt(MAPS_SECTION_TEAMS_SAFE_SPAWN_PITCH);
            Location safeSpawn = new Location(null, safeSpawnX, safeSpawnY, safeSpawnZ, yaw, pitch);

            // TERRITORY IS A LIST OF STRINGS
            // x1,z1,x2,z2 ON EACH ELEMENT
            // territory:
            //   - '69420, 420, 69, 420'
            List<String> stringCoords = currentTeamSection.getStringList(MAPS_SECTION_TERRITORY_KEY);
            Polygon polygon = null;
            for (String rawCoords : stringCoords) {
                String[] coords = rawCoords.split(",");
                try {
                    int coordX1 = Integer.parseInt(coords[0].trim());
                    int coordZ1 = Integer.parseInt(coords[1].trim());
                    int coordX2 = Integer.parseInt(coords[2].trim());
                    int coordZ2 = Integer.parseInt(coords[3].trim());

                    if (polygon == null)
                        polygon = new Polygon(gameMap, new Vector2D(coordX1, coordZ1), new Vector2D(coordX2, coordZ2));
                    else
                        polygon.addSquare(new Vector2D(coordX1, coordZ1), new Vector2D(coordX2, coordZ2));
                } catch (Exception x) {
                    plugin.getLogger().severe("Could not parse territory coordinates for key -- Skipping! Team will likely have no claims " + key);
                }
            }

            BossBar.Color bossbarColor = null;
            if (bossbarColorName != null) bossbarColor = BossBar.Color.valueOf(bossbarColorName.toUpperCase(Locale.ROOT));
            TextColor text = null;
            if (colorName != null) text = TextColor.fromHexString(colorName);
            Material solid = null;
            if (solidName != null) solid = Material.matchMaterial(solidName);
            Material soft = null;
            if (softName != null) soft = Material.matchMaterial(softName);
            Material transparent = null;
            if (transparentName != null) transparent = Material.matchMaterial(transparentName);

            TeamColor color = null;
            if (text == null) color = NamedTeamColor.matchNamedTextColor(colorName);
            if (color == null) color = TeamColor.of(bossbarColor, text, solid, soft, transparent);

            TeamFactory factory = new TeamFactory(safeArea, safeSpawn, name, key, color);
            factory.setTerritory(new Territory(plugin, polygon, factory));
            factories.add(factory);
        }

        return factories;
    }

    private Color color(String hexColor) {
        return Color.decode(hexColor);
    }

    private org.bukkit.Color bukkitColor(String hexColor) {
        Color color = color(hexColor);
        return org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    private void cryAndDisable() {
        plugin.getLogger().severe("There was a problem loading the config. Disabling...");
        Bukkit.getPluginManager().disablePlugin(plugin);
    }

    private void setupConfig() throws IOException, InvalidConfigurationException {
        File mapFile = new File(plugin.getDataFolder(), "maps.yml");
        File shopFile = new File(plugin.getDataFolder(), "shop.yml");
        File configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!mapFile.exists())
            plugin.saveResource("maps.yml", false);
        if (!shopFile.exists())
            plugin.saveResource("shop.yml", false);
        if (!configFile.exists())
            plugin.saveResource("config.yml", false);

        mapsYml = new YamlConfiguration();
        shopYml = new YamlConfiguration();
        configYml = new YamlConfiguration();
        mapsYml.load(new File(plugin.getDataFolder(), "maps.yml"));
        shopYml.load(new File(plugin.getDataFolder(), "shop.yml"));
        configYml.load(new File(plugin.getDataFolder(), "config.yml"));
    }

    @Override
    public int getPointsPerKill() {
        return configYml.getInt(CONFIG_POINTS_PER_KILL_KEY);
    }

    @Override
    public int getLevelsPerKill() {
        return configYml.getInt(CONFIG_LEVELS_PER_KILL_KEY);
    }

    @Override
    public int getPointsToEnd() {
        return configYml.getInt(CONFIG_POINTS_TO_END_KEY);
    }

    @Override
    public int getRespawnTimer() {
        return configYml.getInt(RESPAWN_TIMER_KEY);
    }

    @Override
    public int getSuperBreakerCooldown() {
        return configYml.getInt(SUPER_BREAKER_COOLDOWN);
    }

    @Override
    public int getSuperBreakerTimer() {
        return configYml.getInt(SUPER_BREAKER_TIMER);
    }

    @Override
    public boolean getStartGameOnServerStartup() {
        return configYml.getBoolean(START_GAME_ON_STARTUP);
    }

    @Override
    public List<EntityType> getBlacklistedProjectiles() {
        List<EntityType> types = new ArrayList<>();
        for (String s : configYml.getStringList(BLACKLISTED_PROJECTILES_KEY)) {
            EntityType entityType = EntityType.valueOf(s.toUpperCase());
            types.add(entityType);
        }

        return types;
    }

    @Override
    public void reloadConfig() {
        try {
            configYml.load(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getMapIDs() {
        return mapsYml.getConfigurationSection(MAPS_SECTION_KEY).getKeys(false).stream().toList();
    }

    @Override
    public NamespacedKey getNamespacedItemKey() {
        return this.namespacedItemKey;
    }

    @Override
    public NamespacedKey getNamespacedMapKey() {
        return this.namespacedMapKey;
    }

    @Override
    public NamespacedKey getNamespacedPropertiesKey() {
        return this.namespacedPropertiesKey;
    }
}














