/*
 *               The Building Game - Bukkit Plugin
 * Copyright (C) 2013 Stealth2800 <stealth2800@stealthyone.com>
 *               Website: <http://stealthyone.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stealthyone.mcb.thebuildinggame.backend.arenas.rooms;

import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame.Log;
import com.stealthyone.mcb.thebuildinggame.backend.players.BgPlayer;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class Room {

    private int x, z;

    private boolean inUse = false;

    public Room(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Room && ((Room) object).x == x && ((Room) object).z == z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public void teleportPlayer(BgPlayer player) {
        player.getPlayer().teleport(new Location(TheBuildingGame.getInstance().getGameBackend().getRoomManager().getRoomWorld(), (x * 28) + 1.5D, 4, (z * 28) + 1.5D), TeleportCause.PLUGIN);
    }

    public boolean isInUse() {
        Log.debug("room " + x + ", " + z + " is in use: " + inUse);
        return inUse;
    }

    public void setInUse(boolean newValue) {
        Log.debug("set in use, newValue: " + newValue);
        if (isInUse() != newValue) {
            Log.debug("set in use, marking room " + x + ", " + z + " in use: " + newValue);
            inUse = newValue;
            RoomManager roomManager = TheBuildingGame.getInstance().getGameBackend().getRoomManager();
            if (newValue) roomManager.markRoomModified(x, z, true);
        }
    }

}