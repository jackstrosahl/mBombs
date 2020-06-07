package org.strosahl.mbombs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static org.strosahl.mbombs.Main.setVal;

public enum Bombs
{
    FIRE_BOMB(0,ChatColor.GOLD+"Incendiary",20,4,true),
    NUKE(1,ChatColor.DARK_GREEN+"Nuclear",200,15,false),
    TUNNELER(2,ChatColor.GRAY+"Tunnel",80,40,false),
    FLOATER(3, ChatColor.BLUE+"Floater",80,4,false),
    ANTIGRAVITY(4,ChatColor.YELLOW+"Anti-Gravity",80,15,false),
    CLUSTER_BOMB(5,ChatColor.DARK_GRAY+"Cluster",80,15,false),
    RELOCATOR(6, ChatColor.AQUA+"Relocator",0,0,false);

    private final int id;
    private final ItemStack is;
    private final int fuse;
    private final float yield;
    private final boolean incendiary;
    private final ItemStack missile;
    Bombs(int id,String name,int fuse,float yield,boolean incendiary)
    {
        this.id = id;
        this.fuse = fuse;
        this.yield=yield;
        this.incendiary=incendiary;
        switch(id)
        {
            case 6:
                is = new ItemStack(Material.AIR);
                break;
            default:
                is = new ItemStack(Material.TNT);
                ItemMeta im = is.getItemMeta();
                setVal(im, id);
                im.setDisplayName(name +" Bomb");
                is.setItemMeta(im);
                break;
        }

        missile = new ItemStack(Material.FIREWORK);
        ItemMeta missileMeta = missile.getItemMeta();
        setVal(missileMeta, id);
        missileMeta.setDisplayName(name+" Missile");
        missile.setItemMeta(missileMeta);
    }

    public int getId()
    {
        return id;
    }

    public ItemStack getItemStack()
    {
        return is;
    }

    public ItemStack getMissile()
    {
        return missile;
    }

    public static Bombs getBomb(int id)
    {
        for(Bombs b: Bombs.values())
        {
            if(b.getId()==id) return b;
        }
        return null;
    }

    public int getFuse()
    {
        return fuse;
    }

    public boolean isIncendiary()
    {
        return incendiary;
    }

    public float getYield()
    {
        return yield;
    }
}
