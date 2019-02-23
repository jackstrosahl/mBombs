package org.strosahl.mbombs.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;
import org.strosahl.mbombs.Main;
import org.strosahl.mbombs.data.BombData;
import org.strosahl.mbombs.data.MissileData;

import java.util.UUID;

public class EventProjectileHit implements Listener
{
    Main main;

    public EventProjectileHit(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void onEvent(ProjectileHitEvent e)
    {
        Entity entity = e.getEntity();
        UUID uuid = entity.getUniqueId();
        if(main.getMissileEntities().containsKey(uuid))
        {
            MissileData data = main.getMissileEntities().get(uuid);
            TNTPrimed tnt =main.spawnBomb(entity.getLocation(),new BombData(data.getId(),new Vector(0,1,0)));
            tnt.setFuseTicks(0);
            entity.remove();
        }
    }
}