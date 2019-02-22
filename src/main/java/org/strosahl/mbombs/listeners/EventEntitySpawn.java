package org.strosahl.mbombs.listeners;

import org.bukkit.util.Vector;
import org.strosahl.mbombs.Bombs;
import org.strosahl.mbombs.BombData;
import org.strosahl.mbombs.Main;
import org.bukkit.Location;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

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
            if(main.getBombBlocks().containsKey(loc))
            {
                BombData data = main.getBombBlocks().remove(loc);
                main.getBombEntities().put(e.getEntity().getUniqueId(),data);
                Bombs bomb = Bombs.getBomb(data.getId());
                tnt.setIsIncendiary(bomb.isIncendiary());
                tnt.setYield(bomb.getYield());
                tnt.setFuseTicks(bomb.getFuse());

                switch(bomb)
                {
                    case FLOATER:
                        tnt.setGravity(false);
                        tnt.setVelocity(data.getDirection());
                        break;
                    case TUNNELER:
                    case ANTIGRAVITY:
                        tnt.setGravity(false);
                        tnt.setVelocity(new Vector(0,0,0));
                        break;
                    case CLUSTER_BOMB:
                        tnt.setFuseTicks(0);
                        break;
                }
            }
        }
    }
}