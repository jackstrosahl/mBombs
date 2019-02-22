package org.strosahl.mbombs.listeners;

import org.bukkit.util.Vector;
import org.strosahl.mbombs.BombData;
import org.strosahl.mbombs.Main;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dispenser;

public class EventBlockDispense implements Listener
{
    Main main;

    public EventBlockDispense(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void onEvent(BlockDispenseEvent e)
    {
        if(!e.isCancelled())
        {
            ItemStack item = e.getItem();
            int id = main.getMBombsId(item);
            if(id!=-1)
            {
                Location loc = e.getBlock().getRelative(((Dispenser) e.getBlock().getState().getData()).getFacing()).getLocation();
                Location against = e.getBlock().getLocation();
                Vector diff = loc.clone().subtract(against).toVector();
                main.getBombBlocks().put(loc,new BombData(id,diff));
            }
        }
    }
}