package com.stealthyone.mcb.thebuildinggame.backend.rooms;

import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import org.bukkit.World;

public class EmptySpaceFinder {

    private World world;

    private int length;
    private int width;
    private int y = 4;

    public EmptySpaceFinder(int length, int width) {
        this.length = length;
        this.width = width;
        world = TheBuildingGame.getInstance().getRoomManager().getRoomWorld();
    }

    private void complete() {

    }

    private void check() {

    }

}