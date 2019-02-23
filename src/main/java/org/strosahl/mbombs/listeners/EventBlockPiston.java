package org.strosahl.mbombs.listeners;

import org.strosahl.mbombs.data.BombData;
import org.strosahl.mbombs.Main;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.List;

public class EventBlockPiston implements Listener
{
    Main main;

    public EventBlockPiston(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void onExtend(BlockPistonRetractEvent e)
    {
        if(!e.isCancelled()) updateMBombs(e.getBlocks(),e.getDirection());
    }

    @EventHandler
    public void onExtend(BlockPistonExtendEvent e)
    {
        if(!e.isCancelled()) updateMBombs(e.getBlocks(),e.getDirection());
    }

    public void updateMBombs(List<Block> blocks, BlockFace direction)
    {
        for(Block b: blocks)
        {
            Location loc = b.getLocation();
            if(main.getBombBlocks().containsKey(loc))
            {
                BombData temp = main.getBombBlocks().remove(loc);
                main.getBombBlocks().put(b.getRelative(direction).getLocation(),temp);
            }
        }
    }
}