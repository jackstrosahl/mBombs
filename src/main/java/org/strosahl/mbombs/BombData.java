package org.strosahl.mbombs;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class BombData implements ConfigurationSerializable
{
    int id;
    Vector direction;

    public BombData()
    {
        this(-1,null);
    }

    public BombData(int id, Vector direction)
    {
        this.id=id;
        this.direction=direction;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Vector getDirection()
    {
        return direction;
    }

    public void setDirection(Vector direction)
    {
        this.direction = direction;
    }

    @Override
    public Map<String, Object> serialize()
    {
        Map<String,Object> out = new HashMap<>();
        out.put("id",id);
        out.put("direction",direction);
        return out;
    }

    public static BombData deserialize(Map<String,Object> map)
    {
        BombData out = new BombData();
        for(String key:map.keySet())
        {
            Object value = map.get(key);
            switch(key)
            {
                case "id":
                    out.setId((Integer)value);
                    break;
                case "direction":
                    out.setDirection((Vector)value);
                    break;
            }

        }
        return out;
    }
}
