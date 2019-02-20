package io.github.jackstrosahl.mbombs.events;

import io.github.jackstrosahl.mbombs.Main;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
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
        if (e.getItem().getItemMeta().getLocalizedName().equals("MBombs Fire Bomb"))
        {
            e.setCancelled(true);
            Location loc = e.getBlock().getRelative(((Dispenser) e.getBlock().getState().getData()).getFacing()).getLocation();
            main.spawnCustomTNT(loc, "MBombs Fire Bomb", 80);
        }
    }
}