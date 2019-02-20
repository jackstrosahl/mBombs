package io.github.jackstrosahl.mbombs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;

public enum Bombs
{
    FIRE_BOMB(0);

    private final int id;
    private final ItemStack is;
    Bombs(int id)
    {
        this.id = id;
        switch(id)
        {
            case 0:
                ItemStack fireBomb = new ItemStack(Material.TNT);
                ItemMeta fireMeta = fireBomb.getItemMeta();
                fireMeta.getCustomTagContainer().setCustomTag(NamespacedKey.minecraft("mbombs"), ItemTagType.INTEGER,0);
                fireMeta.setDisplayName(ChatColor.GOLD+"Fire Bomb");
                fireBomb.setItemMeta(fireMeta);
                is = fireBomb;
                break;
            default:
                is=null;
                break;
        }
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
}
