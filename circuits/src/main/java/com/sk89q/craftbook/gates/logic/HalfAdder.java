package com.sk89q.craftbook.gates.logic;

import org.bukkit.Server;
import org.bukkit.block.Sign;

import com.sk89q.craftbook.ic.AbstractIC;
import com.sk89q.craftbook.ic.AbstractICFactory;
import com.sk89q.craftbook.ic.ChipState;
import com.sk89q.craftbook.ic.IC;

public class HalfAdder extends AbstractIC {

    public HalfAdder(Server server, Sign block) {
        super(server, block);
    }

    @Override
    public String getTitle() {
        return "Half Adder";
    }

    @Override
    public String getSignTitle() {
        return "HALF ADDER";
    }

    @Override
    public void trigger(ChipState chip) {
        boolean B = chip.getInput(1);
        boolean C = chip.getInput(2);

        boolean S = B ^ C;
        boolean Ca = B & C;

        chip.setOutput(0, S);
        chip.setOutput(1, Ca);
        chip.setOutput(2, Ca);
    }

    public static class Factory extends AbstractICFactory {

        public Factory(Server server) {

            super(server);
        }

        @Override
        public IC create(Sign sign) {

            return new HalfAdder(getServer(), sign);
        }
    }
}