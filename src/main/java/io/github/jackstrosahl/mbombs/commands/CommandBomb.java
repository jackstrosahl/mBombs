package io.github.jackstrosahl.mbombs.commands;

import io.github.jackstrosahl.mbombs.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandBomb implements CommandExecutor
{

    Main plugin;
    public CommandBomb(Main plugin)
    {
        this.plugin = plugin;
    }
    Player player;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args)
    {
        sender.sendMessage(plugin.getBombs().keySet().toString());
        if(sender instanceof Player)
        {
            player = (Player) sender;
            ItemStack fireBomb = new ItemStack(Material.TNT);
            ItemMeta fireMeta = fireBomb.getItemMeta();
            fireMeta.setLocalizedName("MBombs Fire Bomb");
            fireBomb.setItemMeta(fireMeta);
            ItemStack[] inv = player.getInventory().getStorageContents();
            for(int x=0;x<inv.length;x++)
            {
                if(inv[x] == null)
                {
                    inv[x] = fireBomb;
                    break;
                }
                else if(inv[x].getItemMeta().getLocalizedName().equals("MBombs Fire Bomb"))
                {
                    inv[x].setAmount(inv[x].getAmount()+1);
                    break;
                }
            }
            player.getInventory().setStorageContents(inv);
            /*
            try
            {
                int radius = Integer.parseInt(args[0]);

                Location origin = player.getLocation();
                //origin.getWorld().createExplosion(origin,10);
                setSphere(origin,Material.FIRE,false,radius,radius,radius);
                return true;
            }
            catch(NumberFormatException e)
            {
                e.printStackTrace();
            }*/
            return true;
        }
        return false;
    }


}