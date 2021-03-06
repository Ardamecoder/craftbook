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

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

import com.sk89q.craftbook.AbstractMechanic;
import com.sk89q.craftbook.AbstractMechanicFactory;
import com.sk89q.craftbook.SourcedBlockRedstoneEvent;
import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.bukkit.BukkitUtil;

/**
 * This mechanism allow players to toggle Jack-o-Lanterns.
 *
 * @author sk89q
 */
public class JackOLantern extends AbstractMechanic {

    public static class Factory extends AbstractMechanicFactory<JackOLantern> {

        @Override
        public JackOLantern detect(BlockWorldVector pt) {

            int type = BukkitUtil.toWorld(pt).getBlockTypeIdAt(BukkitUtil.toLocation(pt));

            if (type == BlockID.PUMPKIN || type == BlockID.JACKOLANTERN) return new JackOLantern(pt);

            return null;
        }
    }

    BlockWorldVector pt;

    /**
     * Construct the mechanic for a location.
     *
     * @param pt
     */
    private JackOLantern(BlockWorldVector pt) {

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

        if(event.isOn() == (event.getBlock().getTypeId() == BlockID.JACKOLANTERN))
            return;

        setPowered(event.getBlock(), event.isOn());

        event.getBlock().setData(event.getBlock().getData(), false);
    }

    public void setPowered(Block block, boolean on) {

        byte data = block.getData();
        block.setTypeId(on ? BlockID.JACKOLANTERN : BlockID.PUMPKIN);
        block.setData(data);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent evt) {
        if (evt.getBlock().getTypeId() == BlockID.JACKOLANTERN && (evt.getBlock().isBlockIndirectlyPowered() || evt.getBlock().isBlockPowered()))
            evt.setCancelled(true);
    }
}