package org.strosahl.mbombs.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;
import org.strosahl.mbombs.data.BombData;
import org.strosahl.mbombs.Bombs;
import org.strosahl.mbombs.Main;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EventEntityExplode implements Listener
{
    Main main;

    public EventEntityExplode(Main main)
    {
        this.main = main;
    }


    @EventHandler
    public void onEvent(EntityExplodeEvent e)
    {
        if(!e.isCancelled()&&main.getBombEntities().containsKey(e.getEntity().getUniqueId()))
        {
            BombData data = main.getBombEntities().remove(e.getEntity().getUniqueId());
            int id = data.getId();
            Bombs bomb = Bombs.getBomb(id);
            Location origin = e.getEntity().getLocation().getBlock().getLocation();
            double yield = bomb.getYield();
            switch(bomb)
            {
                case FIRE_BOMB:
                    e.setCancelled(true);

                    for(Block b: e.blockList())
                    {
                        b.setType(Material.FIRE);
                    }
                    break;
                case NUKE:
                    e.setCancelled(true);


                    double d = 10;
                    yield = yield-(d/2);
                    for(double x=-yield;x<=yield;x+=d)
                    {
                        for(double y=-yield;y<=yield;y+=d)
                        {
                            for(double z=-yield;z<=yield;z+=d)
                            {
                                Location loc = origin.clone().add(x,y,z);
                                if(origin.distance(loc)<=yield)
                                {
                                    createExplosion(loc,50,bomb.getId());
                                }
                            }
                        }
                    }
                    break;
                case TUNNELER:
                    e.setCancelled(true);

                    for(int x=0;x<yield;x++)
                    {
                        createExplosion(origin.subtract(data.getDirection()),4,id);
                    }
                    break;
                case ANTIGRAVITY:
                    List<Entity> hitList = e.getEntity().getNearbyEntities(yield,yield,yield);
                    for(Entity hit:hitList)
                    {
                        hit.setGravity(false);
                        hit.setVelocity(data.getDirection());
                        if(hit instanceof Player)
                        {
                            main.getAllowFlying().add(hit.getUniqueId());
                        }
                    }

                    List<FallingBlock> floatingList = new ArrayList<>();

                    Iterator<Block> iterator = e.blockList().iterator();
                    while(iterator.hasNext())
                    {
                        Block b = iterator.next();
                        PistonMoveReaction reaction = b.getPistonMoveReaction();
                        if(reaction.equals(PistonMoveReaction.MOVE)||reaction.equals(reaction.equals(PistonMoveReaction.PUSH_ONLY)))
                        {
                            FallingBlock entity = origin.getWorld().spawnFallingBlock(b.getLocation(),b.getBlockData());
                            entity.setGravity(false);
                            entity.setVelocity(data.getDirection());
                            entity.setDropItem(false);
                            floatingList.add(entity);
                            b.setType(Material.AIR);
                            iterator.remove();
                        }
                    }

                    Bukkit.getScheduler().runTaskLater(main, () ->
                    {
                        for(FallingBlock floating:floatingList)
                        {
                            Location loc = floating.getLocation();
                            if (loc.getBlock().getType().equals(Material.AIR))
                            {
                                loc.getBlock().setBlockData(floating.getBlockData());
                            }
                            floating.remove();
                        }

                        for(Entity hit: hitList)
                        {
                            hit.setGravity(true);
                        }
                    }, 80l);
                    Bukkit.getScheduler().runTaskLater(main, () ->
                    {
                        for(Entity hit: hitList)
                        {
                            if (hit instanceof Player)
                            {
                                main.getAllowFlying().remove(hit.getUniqueId());
                            }
                        }
                    },80l);
                    break;
                case CLUSTER_BOMB:
                    e.setCancelled(true);
                    for(int x=0;x<yield;x++)
                    {
                        Vector direction = new Vector(randomDouble(-.5,.5),randomDouble(1.2,1.5),randomDouble(-.5,.5));
                        createExplosion(origin, bomb.getFuse(),4, direction, id);
                    }
                    break;
            }
            if(e.isCancelled()) e.blockList().clear();
        }
        System.out.println(((TNTPrimed)e.getEntity()).getSource());
    }

    private void createExplosion(Location loc, int fuse,float yield, Vector velocity, int id)
    {
        TNTPrimed entity = (TNTPrimed)loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
        entity.setYield(yield);
        entity.setVelocity(velocity);
        entity.setFuseTicks(fuse);
        entity.addScoreboardTag("strosahl.mbombs.deathmessage."+id);
    }

    private void createExplosion(Location loc, float yield,int id)
    {
        createExplosion(loc,0,yield,new Vector(0,0,0),id);
    }

    private double randomDouble(double min,double max)
    {
        Random rnd = new Random();
        if(min>max) throw new IllegalArgumentException("Min was larger than max.");
        double range = max-min;
        return max-(rnd.nextDouble()*range);
    }
}