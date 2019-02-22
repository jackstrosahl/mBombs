package org.strosahl.mbombs.listeners;

import org.strosahl.mbombs.Bombs;
import org.strosahl.mbombs.Main;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class EventBlockBreak implements Listener
{
    Main main;

    public EventBlockBreak(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void onEvent(BlockBreakEvent e)
    {
        if(!e.isCancelled())
        {
            Location loc = e.getBlock().getLocation();

            if(main.getBombBlocks().containsKey(loc))
            {
                int id = main.getBombBlocks().remove(e.getBlock().getLocation(loc)).getId();
                if(!e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
                {
                    e.setDropItems(false);
                    loc.getWorld().dropItemNaturally(loc, Bombs.getBomb(id).getItemStack());
                }
            }
        }
    }
}