package org.strosahl.mbombs.commands;

import org.bukkit.entity.EntityType;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.strosahl.mbombs.Bombs;
import org.strosahl.mbombs.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@org.bukkit.plugin.java.annotation.command.Command(name="bomb",desc="Blows stuff up.  Used for testing.", usage="/bomb [yield]",permission = "mbombs.bomb")
@Permission(name ="mbombs.bomb")
public class CommandBomb implements CommandExecutor
{

    Main plugin;
    public CommandBomb(Main plugin)
    {
        this.plugin = plugin;
    }
    Player player;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args)
    {
        if(args.length==0)
        {
            sender.sendMessage(plugin.getBombBlocks().keySet().toString());
            return true;
        }
        if(args.length>=1 &&sender instanceof Player)
        {
            Player player = (Player) sender;
            if(args[0].equals("give"))
            {
                for(Bombs bomb: Bombs.values())
                {
                    player.getInventory().addItem(bomb.getItemStack());
                    player.getInventory().addItem(bomb.getMissile());
                }
                player.getInventory().addItem(Main.targeter);
            }
            else if(args[0].equals("spawn"))
            {
                player.getWorld().spawnEntity(player.getLocation(), EntityType.valueOf(args[1]));
            }
            else
            {
                float yield = Float.parseFloat(args[0]);
                player.getWorld().createExplosion(player.getLocation(), yield);
            }
            return true;
        }
        return false;
    }


}