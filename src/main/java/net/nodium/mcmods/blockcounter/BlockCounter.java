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

        BlockVector3 pos1 = region.getMinimumPoint();
        BlockVector3 pos2 = region.getMaximumPoint();

//        Position pos1 = new Position(-89, 90, -329);
//        Position pos2 = new Position(-104, 59, -344);

        for (int i = 0; i < args.length; i += 2) {

        }

        sender.sendMessage("getting tree leaves");

        StringBuilder leaves_out = new StringBuilder();

//        for (int x = Math.min(pos1.x, pos2.x); x <= Math.max(pos1.x, pos2.x); x++) {
//            for (int y = Math.min(pos1.y, pos2.y); y <= Math.max(pos1.y, pos2.y); y++) {
//                for (int z = Math.min(pos1.z, pos2.z); z <= Math.max(pos1.z, pos2.z); z++) {
//                    Block block = ((Player) sender).getLocation().getWorld().getBlockAt(x, y, z);
//                    if (block.getType() == Material.GRASS) {
//                        leaves_out.add(new Position(x, y, z));
//                    }
//                }
//            }
//        }

        Position treeBase = new Position(pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ());
        sender.sendMessage(String.format("tree base = %3d %3d %3d", treeBase.x, treeBase.y, treeBase.z));

        int trunkHeight = -1;

        for (int y = treeBase.y; y < 256; y++) {
            Block block = player.getLocation().getWorld().getBlockAt(treeBase.x, y, treeBase.z);
            if (!isLog(block.getType())) {
                trunkHeight = y - treeBase.y;
                break;
            }
        }
        sender.sendMessage(String.format("trunk height = %d\n", trunkHeight));

        // this is the tree generation function from b1.8, the for loops are copied mostly
        // verbatim except we subtract one from the y max because we don't need the
        // top four corner leaves, they're always air
        for (int y = treeBase.y + trunkHeight - 3; y <= treeBase.y + trunkHeight - 1; y++) {
            int distFromTop = y - (treeBase.y + trunkHeight);
            int radius = 1 - distFromTop / 2;
            for (int x = treeBase.x - radius; x <= treeBase.x + radius; x++) {
                int relX = x - treeBase.x;
                for (int z = treeBase.z - radius; z <= treeBase.z + radius; z++) {
                    int relZ = z - treeBase.z;
                    if (Math.abs(relX) == radius && Math.abs(relZ) == radius) {
                        Position leafPos = new Position(x, y, z);

                        Block block = player.getLocation().getWorld().getBlockAt(leafPos.x, leafPos.y, leafPos.z);
//                        block.setType(Material.STONE);
                        if (isLeaf(block.getType())) {
                            leaves_out.append("l");
                        } else if (block.getType().isAir()) {
                            leaves_out.append("n");
                        } else {
                            leaves_out.append("u");
                        }

//                        sender.sendMessage(String.format("%3d %3d %3d", x, y, z));
//                    sender.sendMessage(String.format("y = %d", y));
//                    sender.sendMessage(String.format("radius = %d", radius));
//                    sender.sendMessage(String.format("dist from top = %d", distFromTop));
                    }
                }
            }
        }

        sender.sendMessage(leaves_out.toString());

        try {
            File file = new File(getDataFolder(), "treeleaves.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);

//            for (Position p : leaves_out) {
//                writer.append(String.format("%s %s %s\n", p.x, p.y, p.z));
//            }

            writer.append(leaves_out.toString());

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
