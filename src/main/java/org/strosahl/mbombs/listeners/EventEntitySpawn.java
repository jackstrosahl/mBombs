package org.strosahl.mbombs.listeners;

import org.bukkit.util.Vector;
import org.strosahl.mbombs.Bombs;
import org.strosahl.mbombs.data.BombData;
import org.strosahl.mbombs.Main;
import org.bukkit.Location;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.UUID;

public class EventEntitySpawn implements Listener
{
    Main main;

    public EventEntitySpawn(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void onEvent(EntitySpawnEvent e)
    {
        if(!e.isCancelled() && e.getEntity() instanceof TNTPrimed)
        {
            TNTPrimed tnt = (TNTPrimed)e.getEntity();
            Location loc = e.getLocation().getBlock().getLocation();
            UUID uuid = e.getEntity().getUniqueId();
            BombData data = null;
            if(main.getBombBlocks().containsKey(loc))
            {
                data = main.getBombBlocks().remove(loc);
            }
            else if(main.getBombEntities().containsKey(uuid))
            {
                data = main.getBombEntities().get(uuid);
            }
            if(data!=null)
            {
                main.applyData(tnt,data);
            }
        }
    }
}