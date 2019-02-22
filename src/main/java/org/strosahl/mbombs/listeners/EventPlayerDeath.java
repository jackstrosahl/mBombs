package org.strosahl.mbombs.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.entity.*;
import org.strosahl.mbombs.Bombs;
import org.strosahl.mbombs.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EventPlayerDeath implements Listener
{
    Main main;

    public EventPlayerDeath(Main main)
    {
        this.main = main;
    }

    @EventHandler
    public void onEvent(PlayerDeathEvent e)
    {
        EntityDamageEvent lastDamageGeneral = e.getEntity().getLastDamageCause();
        if (lastDamageGeneral instanceof EntityDamageByEntityEvent)
        {
            EntityDamageByEntityEvent lastDamage = (EntityDamageByEntityEvent) lastDamageGeneral;
            String prefix = "strosahl.mbombs.";
            String dmPrefix = prefix + "deathmessage.";
            for (String tag : lastDamage.getDamager().getScoreboardTags())
            {
                if (tag.startsWith(prefix))
                {
                    String input;
                    if (tag.startsWith(dmPrefix))
                        input = tag.substring(dmPrefix.length());
                    else
                        input = tag.substring(prefix.length());
                    int id = Integer.parseInt(input);
                    Bombs bomb = Bombs.getBomb(id);
                    String message = null;
                    switch (bomb)
                    {
                        case NUKE:
                            message = "got " + ChatColor.DARK_GREEN + "nuked";
                    }
                    if (message != null)
                        e.setDeathMessage(e.getEntity().getDisplayName() + " " + message);
                }
            }
        }
    }
}