package org.strosahl.mbombs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;

public enum Bombs
{
    FIRE_BOMB(0,ChatColor.GOLD+"Fire Bomb",20,4,true),
    NUKE(1,ChatColor.DARK_GREEN+"Nuke",200,15,false),
    TUNNELER(2,ChatColor.GRAY+"Tunneler",80,100,false),
    FLOATER(3, ChatColor.BLUE+"Floater",80,10,false),
    ANTIGRAVITY(4,ChatColor.YELLOW+"Anti-Gravity",80,15,false),
    CLUSTER_BOMB(5,ChatColor.BLACK+"Cluster Bomb",80,15,false);

    private final int id;
    private final ItemStack is;
    private final int fuse;
    private final float yield;
    private final boolean incendiary;
    Bombs(int id,String name,int fuse,float yield,boolean incendiary)
    {
        this.id = id;
        this.fuse = fuse;
        this.yield=yield;
        this.incendiary=incendiary;
        is = new ItemStack(Material.TNT);
        ItemMeta im = is.getItemMeta();
        im.getCustomTagContainer().setCustomTag(Main.NAMESPACE, ItemTagType.INTEGER,id);
        im.setDisplayName(name);
        is.setItemMeta(im);
    }

    public int getId()
    {
        return id;
    }

    public ItemStack getItemStack()
    {
        return is;
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
