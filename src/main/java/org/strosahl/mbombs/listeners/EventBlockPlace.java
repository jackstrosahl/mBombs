package org.strosahl.mbombs.listeners;

import org.bukkit.util.Vector;
import org.strosahl.mbombs.BombData;
import org.strosahl.mbombs.Main;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class EventBlockPlace implements Listener
{

    Main main;
    public EventBlockPlace(Main plugin)
    {
        this.main = plugin;
    }

    @EventHandler
    public void onEvent(BlockPlaceEvent e)
    {

        if(!e.isCancelled()&&e.getItemInHand()!=null)
        {
            ItemStack item = e.getItemInHand();
            int id = main.getMBombsId(item);
            if(id!=-1)
            {
                Location loc = e.getBlockPlaced().getLocation();

                Location against = e.getBlockAgainst().getLocation();
                Vector diff = loc.clone().subtract(against).toVector();
                if(diff.equals(new Vector(0,0,0))&&!e.getBlockReplacedState().getBlock().getType().equals(Material.AIR))
                {
                    diff.setY(1);
                }

                main.getBombBlocks().put(loc,new BombData(id,diff));
            }
        }
    }
}