package io.github.jackstrosahl.mbombs;

import io.github.jackstrosahl.mbombs.commands.CommandBomb;
import io.github.jackstrosahl.mbombs.events.*;
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Main extends JavaPlugin
{
    FileConfiguration bombsFileConfig;
    File bombsFile;
    String bombsFileName = "bombs.yml";

    HashMap<Location,Integer> bombs;

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
        getServer().getPluginManager().registerEvents(new EventExplosionPrime(this), this);
        getServer().getPluginManager().registerEvents(new EventBlockBreak(this),this);

        getServer().getPluginManager().registerEvents(new EventEntitySpawn(this), this);

        getLogger().info("Enabled");
    }

    public void makeRecipes()
    {
        ShapedRecipe fireRecipe = new ShapedRecipe(NAMESPACE,Bombs.FIRE_BOMB.getItemStack());
        fireRecipe.shape("TFT",
                "FTF",
                "TFT");

        fireRecipe.setIngredient('T',Material.TNT);
        fireRecipe.setIngredient('F',Material.FLINT_AND_STEEL);

        Bukkit.addRecipe(fireRecipe);
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
        CustomItemTagContainer container = is.getItemMeta().getCustomTagContainer();
        if(!container.hasCustomTag(Main.NAMESPACE,ItemTagType.INTEGER)) return -1;
        return container.getCustomTag(NAMESPACE, ItemTagType.INTEGER);
    }

    public HashMap<Location,Integer> getBombs()
    {
        return bombs;
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
        bombs = new HashMap<>();
        loadBombsFile(false);
        for(String s: bombsFileConfig.getKeys(false))
        {
            if(bombsFileConfig.isConfigurationSection(s))
            {
                ConfigurationSection worldSection = bombsFileConfig.getConfigurationSection(s);
                for(String coordsString:worldSection.getKeys(false))
                {
                    double x,y,z;
                    String[] coords = coordsString.split(",");
                    try
                    {
                        x=Double.parseDouble(coords[0]);
                        y=Double.parseDouble(coords[1]);
                        z=Double.parseDouble(coords[2]);

                        bombs.put(new Location(getServer().getWorld(worldSection.getName()),x,y,z),worldSection.getInt(coordsString));
                    }
                    catch(Exception e)
                    {
                        getLogger().warning("Could not load bomb at coordinates "+ coordsString+","+worldSection);
                    }
                }
            }
        }
    }

    public void saveBombs()
    {
        loadBombsFile(true);
        for(Location loc: bombs.keySet())
        {
            String worldName = loc.getWorld().getName();
            if(!bombsFileConfig.isConfigurationSection(worldName))
            {
                bombsFileConfig.createSection(worldName);
            }
            ConfigurationSection section = bombsFileConfig.getConfigurationSection(worldName);
            section.set(loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ(),bombs.get(loc));
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
