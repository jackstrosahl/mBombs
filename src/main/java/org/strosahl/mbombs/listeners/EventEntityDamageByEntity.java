package org.strosahl.mbombs.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.strosahl.mbombs.data.BombData;
import org.strosahl.mbombs.Bombs;
import org.strosahl.mbombs.Main;

import java.util.UUID;

public class EventEntityDamageByEntity implements Listener
{
    Main main;

    public EventEntityDamageByEntity(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void onEvent(EntityDamageByEntityEvent e)
    {
        if(!e.isCancelled())
        {
            Entity entity = e.getEntity();
            Entity damager = e.getDamager();
            UUID uuid = damager.getUniqueId();
            if (main.getBombEntities().containsKey(uuid))
            {
                BombData data = main.getBombEntities().get(uuid);
                Bombs bomb = Bombs.getBomb(data.getId());
                switch (bomb)
                {
                    case FLOATER:
                        break;
                    default:
                        e.setCancelled(true);
                        break;
                }
            }
        }
    }
}