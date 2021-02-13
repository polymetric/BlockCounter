package net.nodium.mcmods.blockcounter;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.internal.annotation.Selection;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;

public final class BlockCounter extends JavaPlugin {
    public WorldEditPlugin worldEdit;

    @Override
    public void onEnable() {
        getDataFolder().mkdir();

        // load worldedit
        worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // TODO proper command handling lol
        if (!(sender instanceof Player)) {
            sender.sendMessage("console not supported yet");
        }

        Player player = (Player) sender;
        BukkitPlayer wePlayer = worldEdit.wrapPlayer(player);
        Region region = null;

        try {
            region = worldEdit.getSession(player).getSelection(wePlayer.getWorld());
        } catch (Exception e) {
            // TODO error handling
            e.printStackTrace();
        }

        BlockVector3 pos1_we = region.getMinimumPoint();
        BlockVector3 pos2_we = region.getMaximumPoint();

        Position pos1 = new Position(pos1_we.getBlockX(), pos1_we.getBlockY(), pos1_we.getBlockZ());
        Position pos2 = new Position(pos2_we.getBlockX(), pos2_we.getBlockY(), pos2_we.getBlockZ());

        sender.sendMessage("dumping blocks");

        StringBuilder blocks_out = new StringBuilder();

        for (int x = Math.min(pos1.x, pos2.x); x <= Math.max(pos1.x, pos2.x); x++) {
                for (int z = Math.min(pos1.z, pos2.z); z <= Math.max(pos1.z, pos2.z); z++) {
                    for (int y = 255; y > 0; y--) {
                    Block block = ((Player) sender).getLocation().getWorld().getBlockAt(x, y, z);

                    if (!block.getType().isAir()) {
                        blocks_out.append(String.format("%6d %6d %s", x, z, block.getType().toString()));
                        break;
                    }
                }
            }
        }

        try {
            File file = new File(getDataFolder(), "blocks.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);

//            for (Position p : blocks_out) {
//                writer.append(String.format("%s %s %s\n", p.x, p.y, p.z));
//            }

            writer.append(blocks_out.toString());

            writer.close();
        } catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "file error");
            e.printStackTrace();
            return false;
        }

        sender.sendMessage("done");

        return true;
    }

    public boolean isLeaf(Material m) {
        return m.toString().toUpperCase().contains("LEAVES");
    }

    public boolean isLog(Material m) {
        return m.toString().toUpperCase().contains("LOG");
    }
}
