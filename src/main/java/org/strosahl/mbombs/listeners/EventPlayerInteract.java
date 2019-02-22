package org.strosahl.mbombs.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.strosahl.mbombs.BombData;
import org.strosahl.mbombs.Bombs;
import org.strosahl.mbombs.Main;

import java.util.HashMap;
import java.util.UUID;

public class EventPlayerInteract implements Listener
{
    Main main;
    HashMap<UUID,Long> lastDoneMap;

    public EventPlayerInteract(Main main)
    {
        this.main = main;
        lastDoneMap = new HashMap<>();
    }

    @EventHandler
    public void onEvent(PlayerInteractEvent e)
    {
        if(e.getAction().equals(Action.RIGHT_CLICK_AIR))
        {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            ItemStack item = player.getInventory().getItemInMainHand();
            int id = main.getMBombsId(item);
            if(id==-1)
            {
                item = player.getInventory().getItemInOffHand();
                id = main.getMBombsId(item);
            }
            if(id!=-1)
            {
                Bombs bomb = Bombs.getBomb(id);
                if(!bomb.equals(Bombs.TUNNELER))
                {
                    if (lastDoneMap.containsKey(uuid))
                    {
                        Long lastDone = lastDoneMap.get(uuid);
                        Long diff = System.currentTimeMillis() - lastDone;
                        if (diff < 375&&diff>200)
                        {
                            Location loc = player.getEyeLocation();
                            Location tntLoc = loc.getBlock().getLocation();
                            main.getBombBlocks().put(tntLoc, new BombData(bomb.getId(), loc.getDirection()));
                            TNTPrimed tnt = (TNTPrimed)player.getWorld().spawnEntity(tntLoc, EntityType.PRIMED_TNT);
                            tnt.setVelocity(loc.getDirection());
                            if(!player.getGameMode().equals(GameMode.CREATIVE))item.setAmount(item.getAmount()-1);
                        }
                    }
                    lastDoneMap.put(uuid, System.currentTimeMillis());
                }
            }
        }
    }
}