package org.strosahl.mbombs.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.strosahl.mbombs.data.BombData;
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
        Action action = e.getAction();
        if(action.equals(Action.RIGHT_CLICK_AIR)||action.equals(Action.RIGHT_CLICK_BLOCK))
        {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            ItemStack item = e.getHand().equals(EquipmentSlot.HAND) ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
            int id = main.getMBombsId(item);
            if (id >= 0)
            {
                Bombs bomb = Bombs.getBomb(id);
                if(action.equals(Action.RIGHT_CLICK_AIR)&&item.getType().equals(Material.TNT))
                {
                    if (!bomb.equals(Bombs.TUNNELER))
                    {
                        if (lastDoneMap.containsKey(uuid))
                        {
                            Long lastDone = lastDoneMap.get(uuid);
                            Long diff = System.currentTimeMillis() - lastDone;
                            if (diff < 375 && diff > 200)
                            {
                                Location loc = player.getEyeLocation();
                                Location tntLoc = loc.getBlock().getLocation();
                                TNTPrimed tnt =  main.spawnBomb(tntLoc, new BombData(bomb.getId(), loc.getDirection()));
                                tnt.setVelocity(loc.getDirection());
                                if (!player.getGameMode().equals(GameMode.CREATIVE))
                                    item.setAmount(item.getAmount() - 1);
                            }
                        }
                        lastDoneMap.put(uuid, System.currentTimeMillis());
                    }
                }
                else if(item.getType().equals(Material.FIREWORK_ROCKET))
                {
                    if(action.equals((Action.RIGHT_CLICK_BLOCK)))
                    {
                        Location loc = player.getTargetBlock(null, 10).getRelative(BlockFace.UP).getLocation();

                        main.launch(player, id, loc);
                        if (!player.getGameMode().equals(GameMode.CREATIVE))
                            item.setAmount(item.getAmount() - 1);
                    }
                    e.setCancelled(true);
                }
            }
            else if(id==-2)
            {
                Block targetBlock = player.getTargetBlock(null,16*main.getServer().getViewDistance());
                if(targetBlock.getType().equals(Material.AIR)) player.sendMessage(Main.prefix+"Could not find a suitable target.");
                else
                {
                    Location target = targetBlock.getLocation();
                    main.getTargets().put(player.getUniqueId(),target);
                    player.sendMessage(Main.prefix+"Successfully targeted X: "+target.getX()+" Y: "+target.getY()+" Z: "+target.getZ()+".");
                }
            }
        }
    }
}