package com.sk89q.craftbook.mech;

import java.util.HashSet;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Tree;

import com.sk89q.craftbook.AbstractMechanic;
import com.sk89q.craftbook.AbstractMechanicFactory;
import com.sk89q.craftbook.LocalPlayer;
import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.craftbook.bukkit.MechanicListenerAdapter;
import com.sk89q.craftbook.util.ItemUtil;
import com.sk89q.craftbook.util.LocationUtil;
import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.bukkit.BukkitUtil;

public class TreeLopper extends AbstractMechanic {

    protected final CraftBookPlugin plugin = CraftBookPlugin.inst();

    private int broken;
    private int blockId;
    private byte blockData;

    private boolean hasPlanted = false;

    private HashSet<Location> visitedLocations = new HashSet<Location>();

    @Override
    public void onBlockBreak(BlockBreakEvent event) {

        if(MechanicListenerAdapter.shouldIgnoreEvent(event))
            return;
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        blockId = event.getBlock().getTypeId();
        blockData = event.getBlock().getData();
        visitedLocations.add(event.getBlock().getLocation());
        broken = 1;

        Block block = event.getBlock();

        TreeSpecies species = null;
        if(CraftBookPlugin.inst().getConfiguration().treeLopperPlaceSapling && (block.getRelative(0, -1, 0).getTypeId() == BlockID.DIRT || block.getRelative(0, -1, 0).getTypeId() == BlockID.GRASS || block.getRelative(0, -1, 0).getTypeId() == BlockID.MYCELIUM) && !hasPlanted)
            species = ((Tree) block.getState().getData()).getSpecies();
        block.breakNaturally(event.getPlayer().getItemInHand());
        if(species != null) {
            block.getRelative(0,-1,0).setTypeId(BlockID.SAPLING);
            ((Tree) block.getRelative(0,-1,0).getState().getData()).setSpecies(species);
            hasPlanted = true;
        }

        for(BlockFace face : plugin.getConfiguration().treeLopperAllowDiagonals ? LocationUtil.getIndirectFaces() : LocationUtil.getDirectFaces()) {
            if(block.getRelative(face).getTypeId() == blockId && (!plugin.getConfiguration().treeLopperEnforceData || block.getRelative(face).getData() == blockData))
                if(searchBlock(event, block.getRelative(face))) {
                    ItemStack heldItem = event.getPlayer().getItemInHand();
                    if(heldItem != null && ItemUtil.getMaxDurability(heldItem.getTypeId()) > 0) {
                        heldItem.setDurability((short) (heldItem.getDurability() + 1));
                        if(heldItem.getDurability() <= ItemUtil.getMaxDurability(heldItem.getTypeId()))
                            event.getPlayer().setItemInHand(heldItem);
                        else
                            event.getPlayer().setItemInHand(null);
                    }
                }
        }
    }

    public boolean searchBlock(BlockBreakEvent event, Block block) {

        LocalPlayer player = CraftBookPlugin.inst().wrapPlayer(event.getPlayer());
        if(visitedLocations.contains(block.getLocation()))
            return false;
        if(broken > plugin.getConfiguration().treeLopperMaxSize)
            return false;
        if(!CraftBookPlugin.inst().getConfiguration().treeLopperItems.contains(player.getHeldItemType()))
            return false;
        if(!plugin.canBuild(event.getPlayer(), block, false)) {
            player.printError("area.break-permissions");
            return false;
        }
        TreeSpecies species = null;
        if(CraftBookPlugin.inst().getConfiguration().treeLopperPlaceSapling && (block.getRelative(0, -1, 0).getTypeId() == BlockID.DIRT || block.getRelative(0, -1, 0).getTypeId() == BlockID.GRASS || block.getRelative(0, -1, 0).getTypeId() == BlockID.MYCELIUM) && !hasPlanted)
            species = ((Tree) block.getState().getData()).getSpecies();
        block.breakNaturally(event.getPlayer().getItemInHand());
        if(species != null) {
            block.getRelative(0,-1,0).setTypeId(BlockID.SAPLING);
            ((Tree) block.getRelative(0,-1,0).getState().getData()).setSpecies(species);
            hasPlanted = true;
        }
        visitedLocations.add(block.getLocation());
        broken += 1;
        for(BlockFace face : plugin.getConfiguration().treeLopperAllowDiagonals ? LocationUtil.getIndirectFaces() : LocationUtil.getDirectFaces()) {
            if(block.getRelative(face).getTypeId() == blockId && (!plugin.getConfiguration().treeLopperEnforceData || block.getRelative(face).getData() == blockData))
                if(searchBlock(event, block.getRelative(face))) {
                    ItemStack heldItem = event.getPlayer().getItemInHand();
                    if(heldItem != null && ItemUtil.getMaxDurability(heldItem.getTypeId()) > 0) {
                        heldItem.setDurability((short) (heldItem.getDurability() + 1));
                        if(heldItem.getDurability() <= ItemUtil.getMaxDurability(heldItem.getTypeId()))
                            event.getPlayer().setItemInHand(heldItem);
                        else
                            event.getPlayer().setItemInHand(null);
                    }
                }
        }

        return true;
    }

    public static class Factory extends AbstractMechanicFactory<TreeLopper> {

        @Override
        public TreeLopper detect(BlockWorldVector pt, LocalPlayer player) {

            Block block = BukkitUtil.toWorld(pt).getBlockAt(BukkitUtil.toLocation(pt));
            if (CraftBookPlugin.inst().getConfiguration().treeLopperBlocks.contains(block.getTypeId())) {
                return CraftBookPlugin.inst().getConfiguration().treeLopperItems.contains(player.getHeldItemType()) && player.hasPermission("craftbook.mech.treelopper.use") ? new TreeLopper() : null;
            }
            return null;
        }
    }
}