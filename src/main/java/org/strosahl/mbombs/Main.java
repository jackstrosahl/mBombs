package org.strosahl.mbombs;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.strosahl.mbombs.commands.CommandBomb;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.plugin.java.JavaPlugin;
import org.strosahl.mbombs.listeners.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

@Plugin(name="MBombs",version="1.0")
public class Main extends JavaPlugin
{
    FileConfiguration bombsFileConfig;
    File bombsFile;
    String bombsFileName = "bombs.yml";

    HashMap<Location, BombData> bombBlocks;
    HashMap<UUID,BombData> bombEntities;
    HashSet<UUID> allowFlying;

    public static NamespacedKey getNamespace(String s) {return NamespacedKey.minecraft("mbombs"+s);}
    public static final NamespacedKey NAMESPACE = NamespacedKey.minecraft("mbombs");
    public static final int fireBomb = 0;

    @Override
    public void onEnable()
    {
        loadBombs();

        makeRecipes();

        getCommand("bomb").setExecutor(new CommandBomb(this));

        getServer().getPluginManager().registerEvents(new EventBlockPlace(this),this);
        getServer().getPluginManager().registerEvents(new EventBlockDispense(this), this);
        getServer().getPluginManager().registerEvents(new EventEntityExplode(this), this);
        getServer().getPluginManager().registerEvents(new EventBlockBreak(this),this);

        getServer().getPluginManager().registerEvents(new EventEntitySpawn(this), this);
        getServer().getPluginManager().registerEvents(new EventBlockPiston(this), this);

        getServer().getPluginManager().registerEvents(new EventPlayerDeath(this), this);
        getServer().getPluginManager().registerEvents(new EventPlayerInteract(this), this);

        getServer().getPluginManager().registerEvents(new EventEntityChangeBlock(this),this);
        getServer().getPluginManager().registerEvents(new EventEntityDamageByEntity(this), this);
        getServer().getPluginManager().registerEvents(new EventPlayerKick(this), this);

        getLogger().info("Enabled");
    }

    public void makeRecipes()
    {
        ShapedRecipe fireRecipe = new ShapedRecipe(getNamespace("0"),Bombs.FIRE_BOMB.getItemStack());
        fireRecipe.shape("TFT",
                "FTF",
                "TFT");

        fireRecipe.setIngredient('T',Material.TNT);
        fireRecipe.setIngredient('F',Material.FLINT_AND_STEEL);

        Bukkit.addRecipe(fireRecipe);

        ShapedRecipe nukeRecipe = new ShapedRecipe(getNamespace("1"), Bombs.NUKE.getItemStack());
        nukeRecipe.shape("TTT",
                "TNT",
                "TTT");

        nukeRecipe.setIngredient('T',Material.TNT);
        nukeRecipe.setIngredient('N',Material.NETHER_STAR);

        Bukkit.addRecipe(nukeRecipe);

        ShapedRecipe tunnelerRecipe = new ShapedRecipe(getNamespace("2"), Bombs.TUNNELER.getItemStack());
        tunnelerRecipe.shape("OTO",
                "TTT",
                "OTO");

        tunnelerRecipe.setIngredient('T',Material.TNT);
        tunnelerRecipe.setIngredient('O',Material.OBSIDIAN);

        Bukkit.addRecipe(tunnelerRecipe);
    }

    @Override
    public void onDisable()
    {
        saveBombs();
        getLogger().info("Disabled.");
    }


    //General Methods
    public void playSound(Location loc,Sound sound)
    {
        for(Player p:getServer().getOnlinePlayers())
        {
            p.playSound(loc,sound,1,1);
        }
    }

    public int getMBombsId(ItemStack is)
    {
        try
        {
            CustomItemTagContainer container = is.getItemMeta().getCustomTagContainer();
            if (!container.hasCustomTag(Main.NAMESPACE, ItemTagType.INTEGER))
                return -1;
            return container.getCustomTag(NAMESPACE, ItemTagType.INTEGER);
        }
        catch(NullPointerException e)
        {
            return -1;
        }
    }

    public HashMap<Location, BombData> getBombBlocks()
    {
        return bombBlocks;
    }

    public HashMap<UUID, BombData> getBombEntities()
    {
        return bombEntities;
    }

    public HashSet<UUID> getAllowFlying()
    {
        return allowFlying;
    }

    public boolean setSphere(Location origin, Material mat, Boolean replace, int rX, int rY, int rZ)
    {
        int oX = (int)origin.getX();
        int oY = (int)origin.getY();
        int oZ = (int)origin.getZ();

        for(int x=oX-rX;x<=oX+rX;x++)
        {
            for (int y = oY - rY; y <= oY + rY; y++)
            {
                for (int z = oZ - rZ; z <= oZ + rZ; z++)
                {
                    Location location = new Location(origin.getWorld(), x, y, z);
                    Block block = origin.getWorld().getBlockAt(location);
                    if (location.distance(origin) < rX && (replace || block.getType().equals(Material.AIR)))
                    {
                        block.setType(mat);
                    }
                }
            }
        }
        return true;
    }

    public void spawnCustomTNT(Location loc, String name,int fuse)
    {
        TNTPrimed tnt = (TNTPrimed)loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
        tnt.setCustomName(name);
        tnt.setFuseTicks(fuse);
    }


    //Saving/Loading bomb locations/ids
    public void loadBombsFile(boolean replace)
    {

        bombsFile = new File(getDataFolder(),bombsFileName);
        if (!bombsFile.exists())
        {
            bombsFile.getParentFile().mkdirs();
            saveResource(bombsFileName, replace);
        }
        bombsFileConfig = YamlConfiguration.loadConfiguration(bombsFile);
    }

    public void loadBombs()
    {
        bombBlocks = new HashMap<>();
        bombEntities = new HashMap<>();
        allowFlying = new HashSet<>();

        ConfigurationSerialization.registerClass(BombData.class);

        loadBombsFile(false);
        for(String worldName: bombsFileConfig.getKeys(false))
        {
            ConfigurationSection worldSection = bombsFileConfig.getConfigurationSection(worldName);
            for(String i:worldSection.getKeys(false))
            {
                ConfigurationSection cur = worldSection.getConfigurationSection(i);
                Location loc = cur.getSerializable("location",Location.class);
                BombData data = cur.getSerializable("data", BombData.class);
                bombBlocks.put(loc,data);
            }
        }
    }

    public void saveBombs()
    {
        loadBombsFile(true);
        int i =0;
        for(Location loc: bombBlocks.keySet())
        {
            String sectionName = loc.getWorld().getName()+"."+i;
            if(!bombsFileConfig.isConfigurationSection(sectionName))
            {
                bombsFileConfig.createSection(sectionName);
            }
            ConfigurationSection section = bombsFileConfig.getConfigurationSection(sectionName);
            section.set("location",loc);
            section.set("data", bombBlocks.get(loc));
            i++;
        }
        saveBombsFile();
    }

    public void saveBombsFile()
    {
        try
        {
            bombsFileConfig.save(bombsFile);
        }
        catch(IOException e)
        {
            getLogger().warning("Failed to save bomb locations");
        }
    }

    public FileConfiguration getBombsFileConfig()
    {
        return bombsFileConfig;
    }
}
