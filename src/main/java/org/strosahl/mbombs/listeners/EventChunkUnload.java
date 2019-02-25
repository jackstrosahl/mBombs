package org.strosahl.mbombs.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.strosahl.mbombs.Main;

import java.util.UUID;

public class EventChunkUnload implements Listener
{
    Main main;

    public EventChunkUnload(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void onEvent(ChunkUnloadEvent e)
    {
        if(!e.isCancelled())
        {
            for(UUID uuid:main.getMissileEntities().keySet())
            {
                Entity entity = main.getServer().getEntity(uuid);
                if(entity.getLocation().getChunk().equals(e.getChunk()))
                    e.setCancelled(true);
            }
        }
    }
}