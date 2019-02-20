package io.github.jackstrosahl.mbombs.events;

import io.github.jackstrosahl.mbombs.Main;
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
                main.getBombs().put(loc,id);
            }
        }
    }
}