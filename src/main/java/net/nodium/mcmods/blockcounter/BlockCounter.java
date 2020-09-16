package net.nodium.mcmods.blockcounter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public final class BlockCounter extends JavaPlugin {

    @Override
    public void onEnable() {
        getDataFolder().mkdir();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("console not supported yet");
        }

        ArrayList<Position> flowers = new ArrayList<Position>();
        ArrayList<Position> potentialFlowers = new ArrayList<Position>();

//        Selection selection = worldEdit.getSelection((Player) sender);

        Position pos1 = new Position(87, 85, -114);
        Position pos2 = new Position(110, 64, -89);

        for (int x = pos1.x; x <= pos2.x; x++) {
            for (int y = pos1.y; y >= pos2.y; y--) {
                for (int z = pos1.z; z <= pos2.z; z++) {
                    Block block = ((Player) sender).getLocation().getWorld().getBlockAt(x, y, z);
                    if (block.getType() == Material.DANDELION) {
                        flowers.add(new Position(x, y, z));
//                        sender.sendMessage(String.format("found block %s at %s", block.getType().toString(), block.getX(), block.getY(), block.getZ()));
                    }
                }
            }
        }

        for (Position p : flowers) {
            for (int x = p.x - 7; x <= p.x + 7; x++) {
                for (int y = p.y - 3; y <= p.y + 3; y++) {
                    for (int z = p.z - 7; z <= p.z + 7; z++) {
                        Block blockBelow = ((Player) sender).getLocation().getWorld().getBlockAt(x, y - 1, z);
                        Block blockAbove = ((Player) sender).getLocation().getWorld().getBlockAt(x, y, z);
                        if (blockBelow.getType() == Material.GRASS_BLOCK && blockAbove.getType() == Material.AIR) {
                            potentialFlowers.add(new Position(x, y, z));
                            sender.sendMessage(String.format("found block %s at %s %s %s", blockAbove.getType().toString(), blockAbove.getX(), blockAbove.getY(), blockAbove.getZ()));
                        }
                    }
                }
            }
        }

        try {
            File file = new File(getDataFolder(), "potentialFlowers.txt");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);

            for (Position p : potentialFlowers) {
                writer.append(String.format("%s %s %s\n", p.x, p.y, p.z));
            }

            writer.close();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "file error");
            e.printStackTrace();
        }

        sender.sendMessage("done");

        return true;
    }
}
