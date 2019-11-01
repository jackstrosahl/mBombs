package org.strosahl.mbombs.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;
import org.strosahl.mbombs.Main;
import org.strosahl.mbombs.data.MissileData;

import java.util.UUID;

public class EventProjectileLaunch implements Listener
{
    Main main;

    public EventProjectileLaunch(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void onEvent(ProjectileLaunchEvent e)
    {
        Projectile projectile = e.getEntity();
        UUID uuid = projectile.getUniqueId();

        if(main.getMissileEntities().containsKey(uuid))
        {
            MissileData data = main.getMissileEntities().get(uuid);
            Location loc = projectile.getLocation();
            Location target = data.getTarget();
            Vector path = target.toVector().subtract(loc.toVector());
            projectile.setVelocity(path);
            projectile.setGravity(false);
        }
    }
}