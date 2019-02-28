package org.strosahl.mbombs.data;

import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.HashSet;

public class MissileData
{
    private Location target;
    private int id;
    private int taskId;
    private HashSet<Chunk> forced;

    public MissileData(Location target, int id)
    {
        this.target =target;
        this.id=id;
        this.taskId = -1;

        forced = new HashSet<>();
    }

    public HashSet<Chunk> getForced()
    {
        return forced;
    }

    public int getTaskId()
    {
        return taskId;
    }

    public void setTaskId(int taskId)
    {
        this.taskId = taskId;
    }

    public Location getTarget()
    {
        return target;
    }

    public void setTarget(Location target)
    {
        this.target = target;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}
