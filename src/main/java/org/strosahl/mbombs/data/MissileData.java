package org.strosahl.mbombs.data;

import org.bukkit.Location;

public class MissileData
{
    private Location target;
    private int id;

    public MissileData(Location target, int id)
    {
        this.target =target;
        this.id=id;
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
