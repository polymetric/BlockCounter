package net.nodium.mcmods.blockcounter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

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

        ArrayList<Position> leaves_orig = new ArrayList<Position>();
        ArrayList<Position> leaves_orig_rejects = new ArrayList<Position>();
        ArrayList<Position> leaves_out = new ArrayList<Position>();

        try {
            File file = new File(getDataFolder(), "leaves_orig.txt");
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                Position p = Position.loadPositionFromString(scan.nextLine());
                leaves_orig.add(p);
                System.out.println(p);
            }
        } catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "file error");
            e.printStackTrace();
            return false;
        }

        leaves_orig_rejects.addAll(leaves_orig);

//        Selection selection = worldEdit.getSelection((Player) sender);

        Position pos1 = new Position(6, 66, -4);
        Position pos2 = new Position(-10, 83, 8);

        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equalsIgnoreCase("time")) {
                int x = Integer.parseInt(args[i + 1]);
            }
        }

        for (int x = Math.min(pos1.x, pos2.x); x <= Math.max(pos1.x, pos2.x); x++) {
            for (int y = Math.min(pos1.y, pos2.y); y <= Math.max(pos1.y, pos2.y); y++) {
                for (int z = Math.min(pos1.z, pos2.z); z <= Math.max(pos1.z, pos2.z); z++) {
                    Block blockBelow = ((Player) sender).getLocation().getWorld().getBlockAt(x, y, z);
                    Block blockAbove = ((Player) sender).getLocation().getWorld().getBlockAt(x, y + 1, z);
                    if (blockBelow.getType() == Material.PURPLE_WOOL && blockAbove.getType() == Material.AIR) {
                        for (Position p : leaves_orig) {
                            if (p.x == x && p.y + Integer.parseInt(args[0]) == y && p.z == z) {
                                leaves_out.add(new Position(x, y, z));
                                leaves_orig_rejects.remove(p);
                            }
                        }
                    }
                }
            }
        }

        sender.sendMessage(args[0]);
        sender.sendMessage(String.format("%d leaves did not match the list", leaves_orig_rejects.size()));

        try {
            File file = new File(getDataFolder(), "leaves_out.txt");
            File file2 = new File(getDataFolder(), "leaves_orig_rejects.txt");
            file.createNewFile();
            file2.createNewFile();
            FileWriter writer = new FileWriter(file);
            FileWriter writer2 = new FileWriter(file2);

            for (Position p : leaves_out) {
                writer.append(String.format("%s %s %s\n", p.x, p.y, p.z));
            }
//            for (Position p : leaves_orig_rejects) {
//                writer2.append(String.format("%s %s %s\n", p.x, p.y, p.z));
//            }
            for (Position p : leaves_orig) {
                writer2.append(String.format("{ %s, %s, %s },\n", p.x, p.y, p.z));
            }

            writer.close();
            writer2.close();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "file error");
            e.printStackTrace();
            return false;
        }

        sender.sendMessage("done");

        return true;
    }
}
