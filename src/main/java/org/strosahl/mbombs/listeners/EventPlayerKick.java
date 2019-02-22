package org.strosahl.mbombs.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.strosahl.mbombs.Main;

public class EventPlayerKick implements Listener
{
    Main main;

    public EventPlayerKick(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void onEvent(PlayerKickEvent e)
    {
        if(!e.isCancelled())
        {
            if(e.getReason().equals("Flying is not enabled on this server")&&main.getAllowFlying().contains(e.getPlayer().getUniqueId()))
            {
                e.setCancelled(true);
            }
        }
    }
}