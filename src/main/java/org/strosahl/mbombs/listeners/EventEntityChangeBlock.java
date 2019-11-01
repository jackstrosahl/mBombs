package org.strosahl.mbombs.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.strosahl.mbombs.Main;

public class EventEntityChangeBlock implements Listener
{
    Main main;

    public EventEntityChangeBlock(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void onEvent(EntityChangeBlockEvent e)
    {

    }
}