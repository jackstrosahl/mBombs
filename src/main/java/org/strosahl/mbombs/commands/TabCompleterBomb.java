package org.strosahl.mbombs.commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.strosahl.mbombs.Main;

import java.util.ArrayList;
import java.util.List;

public class TabCompleterBomb implements TabCompleter
{
    Main main;

    public TabCompleterBomb(Main main) {this.main = main;}

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args)
    {
        List<String> out = new ArrayList<String>();
        if(args.length==2&&args[0].equalsIgnoreCase("play"))
        {
            for(Sound sound: Sound.values())
            {
                String name = sound.name();
                if(name.startsWith(args[1].toUpperCase()))
                    out.add(name);
            }
        }
        return out;
    }
}
