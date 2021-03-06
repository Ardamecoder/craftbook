// $Id$
/*
 * CraftBook Copyright (C) 2010 sk89q <http://www.sk89q.com>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.craftbook.circuits;

import org.bukkit.event.block.BlockBreakEvent;

import com.sk89q.craftbook.AbstractMechanic;
import com.sk89q.craftbook.AbstractMechanicFactory;
import com.sk89q.craftbook.SourcedBlockRedstoneEvent;
import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.bukkit.BukkitUtil;

/**
 * This mechanism allow players to toggle GlowStone.
 *
 * @author sk89q
 */
public class GlowStone extends AbstractMechanic {

    public static class Factory extends AbstractMechanicFactory<GlowStone> {

        @Override
        public GlowStone detect(BlockWorldVector pt) {

            int type = BukkitUtil.toWorld(pt).getBlockTypeIdAt(BukkitUtil.toLocation(pt));

            if (CraftBookPlugin.inst().getConfiguration().glowstoneOffBlock.isSame(BukkitUtil.toWorld(pt).getBlockAt(BukkitUtil.toLocation(pt))) || type == BlockID.LIGHTSTONE)
                return new GlowStone(pt);

            return null;
        }
    }

    BlockWorldVector pt;

    /**
     * Construct the mechanic for a location.
     *
     * @param pt
     */
    private GlowStone(BlockWorldVector pt) {

        super();
        this.pt = pt;
    }

    /**
     * Raised when an input redstone current changes.
     */
    @Override
    public void onBlockRedstoneChange(SourcedBlockRedstoneEvent event) {

        if(event.isMinor())
            return;

        if(event.isOn() == (event.getBlock().getTypeId() == BlockID.LIGHTSTONE))
            return;

        event.getBlock().setTypeIdAndData(event.isOn() ? BlockID.LIGHTSTONE : CraftBookPlugin.inst().getConfiguration().glowstoneOffBlock.getId(), (byte) (event.isOn() ? event.getBlock().getData() : CraftBookPlugin.inst().getConfiguration().glowstoneOffBlock.getData() == -1 ? event.getBlock().getData() : CraftBookPlugin.inst().getConfiguration().glowstoneOffBlock.getData()), true);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.getBlock().getTypeId() == BlockID.LIGHTSTONE && (event.getBlock().isBlockIndirectlyPowered() || event.getBlock().isBlockPowered()))
            event.setCancelled(true);
    }
}