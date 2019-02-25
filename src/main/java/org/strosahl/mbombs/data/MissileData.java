package org.strosahl.mbombs.data;

import org.bukkit.Location;

public class MissileData
{
    private Location target;
    private int id;
    private int taskId;

    public MissileData(Location target, int id)
    {
        this.target =target;
        this.id=id;
        this.taskId = -1;
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
