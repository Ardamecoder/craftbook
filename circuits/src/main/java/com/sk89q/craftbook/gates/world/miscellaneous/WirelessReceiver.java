// $Id$
/*
 * Copyright (C) 2010, 2011 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.craftbook.gates.world.miscellaneous;

import org.bukkit.Server;

import com.sk89q.craftbook.BaseConfiguration;
import com.sk89q.craftbook.ChangedSign;
import com.sk89q.craftbook.LocalPlayer;
import com.sk89q.craftbook.ic.AbstractIC;
import com.sk89q.craftbook.ic.AbstractICFactory;
import com.sk89q.craftbook.ic.ChipState;
import com.sk89q.craftbook.ic.IC;
import com.sk89q.craftbook.ic.ICFactory;
import com.sk89q.craftbook.ic.ICVerificationException;
import com.sk89q.craftbook.ic.SelfTriggeredIC;

public class WirelessReceiver extends AbstractIC implements SelfTriggeredIC {

    protected String band;

    public WirelessReceiver(Server server, ChangedSign sign, ICFactory factory) {

        super(server, sign, factory);

        try {
            band = sign.getLine(2);
            band += sign.getLine(3);
        } catch (Exception e) {
            band = "test";
        }
    }

    @Override
    public String getTitle() {

        return "Wireless Receiver";
    }

    @Override
    public String getSignTitle() {

        return "RECEIVER";
    }

    @Override
    public void trigger(ChipState chip) {

        if (chip.getInput(0)) {
            Boolean val = WirelessTransmitter.getValue(band);
            if (val == null) return;

            chip.setOutput(0, val);
        }
    }

    @Override
    public void think(ChipState chip) {

        Boolean val = WirelessTransmitter.getValue(band);

        if (val == null) return;

        chip.setOutput(0, val);
    }

    public static class Factory extends AbstractICFactory {

        public boolean requirename;

        public Factory(Server server) {

            super(server);
        }

        @Override
        public IC create(ChangedSign sign) {

            return new WirelessReceiver(getServer(), sign, this);
        }

        @Override
        public String getDescription() {

            return "Recieves signal from wireless transmitter.";
        }

        @Override
        public String[] getLineHelp() {

            String[] lines = new String[] {
                    "wireless band",
                    "user"
            };
            return lines;
        }

        @Override
        public void checkPlayer(ChangedSign sign, LocalPlayer player) throws ICVerificationException {
            if(requirename)
                sign.setLine(3, player.getName());
            else if(!sign.getLine(3).isEmpty())
                sign.setLine(3, player.getName());
        }

        @Override
        public void addConfiguration(BaseConfiguration.BaseConfigurationSection section) {

            requirename = section.getBoolean("per-player", false);
        }

        @Override
        public boolean needsConfiguration() {
            return true;
        }
    }

    @Override
    public boolean isActive () {
        return true;
    }
}