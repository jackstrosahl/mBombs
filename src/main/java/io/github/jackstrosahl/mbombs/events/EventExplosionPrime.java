package io.github.jackstrosahl.mbombs.events;

import io.github.jackstrosahl.mbombs.Main;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class EventExplosionPrime implements Listener
{
    Main main;

    public EventExplosionPrime(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void onEvent(ExplosionPrimeEvent e)
    {
        if(e.getEntity().getCustomName()!=null&&e.getEntity().getCustomName().equals("MBombs Fire Bomb"))
        {
            e.setCancelled(true);
            main.playSound(e.getEntity().getLocation(), Sound.ENTITY_GENERIC_EXPLODE);
            main.setSphere(e.getEntity().getLocation(), Material.FIRE,false,5,5,5);
        }
    }
}