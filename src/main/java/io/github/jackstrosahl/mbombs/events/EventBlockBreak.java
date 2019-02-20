package io.github.jackstrosahl.mbombs.events;

import io.github.jackstrosahl.mbombs.Bombs;
import io.github.jackstrosahl.mbombs.Main;
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

            if(main.getBombs().containsKey(loc))
            {
                int id = main.getBombs().remove(e.getBlock().getLocation(loc));
                main.getLogger().info(id+"");
                if(!e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
                {
                    e.setDropItems(false);
                    loc.getWorld().dropItemNaturally(loc, Bombs.getBomb(id).getItemStack());
                }
            }
        }
    }
}