package io.github.jackstrosahl.mbombs.events;

import io.github.jackstrosahl.mbombs.Main;
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
        String name = e.getEntity().getName();
        if (name.equals("Primed TNT")) main.getLogger().info("TNT Spawned");
    }
}