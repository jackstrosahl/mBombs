package org.strosahl.mbombs;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.*;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.strosahl.mbombs.commands.CommandBomb;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.plugin.java.JavaPlugin;
import org.strosahl.mbombs.data.BombData;
import org.strosahl.mbombs.data.MissileData;
import org.strosahl.mbombs.listeners.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

@Plugin(name="MBombs",version="1.0")
@ApiVersion(ApiVersion.Target.v1_13)
@Author("strojac")
public class Main extends JavaPlugin
{
    FileConfiguration bombsFileConfig;
    File bombsFile;
    String bombsFileName = "bombs.yml";

    HashMap<Location, BombData> bombBlocks;
    HashMap<UUID,BombData> bombEntities;
    HashMap<UUID, MissileData> missileEntities;

    HashMap<UUID, Location> targets;
    HashSet<UUID> allowFlying;

    public static NamespacedKey getNamespace(String s) {return NamespacedKey.minecraft("mbombs"+s);}
    public static final NamespacedKey NAMESPACE = NamespacedKey.minecraft("mbombs");

    public static ItemStack targeter;
    public static String prefix=ChatColor.AQUA+"[MBombs]: "+ChatColor.DARK_GREEN;

    static
    {
        targeter = new ItemStack(Material.CLOCK);
        ItemMeta targeterMeta = targeter.getItemMeta();
        targeterMeta.setDisplayName(ChatColor.RED+"Targeter");
        targeter.setItemMeta(targeterMeta);
    }

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

        getServer().getPluginManager().registerEvents(new EventProjectileHit(this),this);

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

        ShapelessRecipe targeterRecipe = new ShapelessRecipe(getNamespace("3"),targeter);

        targeterRecipe.addIngredient(Material.COMPASS);
        targeterRecipe.addIngredient(8,Material.DIAMOND);

        Bukkit.addRecipe(targeterRecipe);
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

    public HashMap<UUID, MissileData> getMissileEntities()
    {
        return missileEntities;
    }

    public HashSet<UUID> getAllowFlying()
    {
        return allowFlying;
    }

    public HashMap<UUID, Location> getTargets()
    {
        return targets;
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

    public TNTPrimed spawnBomb(Location loc, BombData data)
    {
        TNTPrimed out = (TNTPrimed)loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
        bombEntities.put(out.getUniqueId(),data);
        applyData(out,data);
        return out;
    }

    public void applyData(TNTPrimed tnt,BombData data)
    {
        getBombEntities().put(tnt.getUniqueId(),data);
        Bombs bomb = Bombs.getBomb(data.getId());
        tnt.setIsIncendiary(bomb.isIncendiary());
        tnt.setYield(bomb.getYield());
        getLogger().info(bomb.getYield()+"");
        tnt.setFuseTicks(bomb.getFuse());

        switch(bomb)
        {
            case FLOATER:
                tnt.setGravity(false);
                tnt.setVelocity(data.getDirection());
                break;
            case TUNNELER:
            case ANTIGRAVITY:
                tnt.setGravity(false);
                tnt.setVelocity(new Vector(0,0,0));
                break;
            case CLUSTER_BOMB:
                tnt.setFuseTicks(0);
                break;
        }
    }

    public boolean launch(Player player, int id, Location origin)
    {
        UUID uuid = player.getUniqueId();
        if(targets.containsKey(uuid))
        {
            Location target = targets.get(uuid);
            if(origin.getX()==target.getX()&&origin.getZ()==target.getZ())
            {
                player.sendMessage(prefix+"Target is too close!");
                return false;
            }
            spawnMissile(origin, new MissileData(target,id),player);
            return true;
        }
        else
        {
            player.sendMessage(prefix+"You do not have a target!  Hint: craft a targeter first.");
            return false;
        }
    }

    public Projectile spawnMissile(Location loc, MissileData data, EntityType type, ProjectileSource source)
    {
        Entity spawned = loc.getWorld().spawnEntity(loc, type);
        getMissileEntities().put(spawned.getUniqueId(),data);
        if(!(spawned instanceof Projectile)) return null;

        Projectile out = (Projectile) spawned;
        out.setShooter(source);
        out.setGravity(false);

        Vector target = data.getTarget().toVector();
        target.setY(0);
        Vector origin = loc.toVector();
        origin.setY(0);
        double radius = origin.distance(target)/2;
        Vector path = target.clone().subtract(origin).normalize().multiply(.3);
        path.setY(0);

        double magnitude = path.distance(new Vector(0,0,0))*2;

        path.setY(magnitude);

        out.setVelocity(path);

        getLogger().info("Target: "+target+" Origin: "+origin);
        getLogger().info(path+"");
        data.setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () ->
        {
            Vector cur = out.getLocation().toVector();
            cur.setY(0);
            double dist = cur.distance(origin);
            double percent = (radius-dist)/radius;
            path.setY(magnitude*percent);
            if(percent<=-1)
            {
                path.setX(0);
                path.setZ(0);
            }
            Chunk chunk = out.getLocation().getChunk();


            out.setVelocity(path);

            getLogger().info("dist: "+dist+" radius: "+radius+" = "+percent);
        },3L,3L));
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () ->
        {
            Location cur = out.getLocation();
            Chunk next = cur.clone().add(path.clone().multiply(20)).getChunk();
            if(!next.isLoaded()) next.load();
        },0L,20L);
        return out;
    }

    public Projectile spawnMissile(Location loc, MissileData data, ProjectileSource source)
    {
        return spawnMissile(loc,data,EntityType.TRIDENT,source);
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
        missileEntities = new HashMap<>();
        targets = new HashMap<>();

        allowFlying = new HashSet<>();

        ConfigurationSerialization.registerClass(BombData.class);

        loadBombsFile(false);

        for(String i:bombsFileConfig.getKeys(false))
        {
            ConfigurationSection cur = bombsFileConfig.getConfigurationSection(i);
            Location loc = cur.getSerializable("location",Location.class);
            BombData data = cur.getSerializable("data", BombData.class);
            bombBlocks.put(loc,data);
        }
    }

    public void saveBombs()
    {
        loadBombsFile(true);
        int i =0;
        for(Location loc: bombBlocks.keySet())
        {
            String sectionName = i+"";
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
