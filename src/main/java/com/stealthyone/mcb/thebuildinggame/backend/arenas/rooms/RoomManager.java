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

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.stealthyone.mcb.stbukkitlib.lib.hooks.HookHelper;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame.Log;
import com.stealthyone.mcb.thebuildinggame.backend.GameBackend;
import com.stealthyone.mcb.thebuildinggame.config.ConfigHelper;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RoomManager {

    private TheBuildingGame plugin;
    private GameBackend gameBackend;

    private File roomFile;
    private Map<String, Room> loadedRooms = new HashMap<String, Room>();

    public RoomManager(TheBuildingGame plugin, GameBackend gameBackend) {
        this.plugin = plugin;
        this.gameBackend = gameBackend;

        String fileName = "bgroom.schematic";
        plugin.saveResource(fileName, false);
        roomFile = new File(plugin.getDataFolder() + File.separator + fileName);

        String worldName = ConfigHelper.NAME_ROOM_WORLD.getString();
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = new WorldCreator(worldName).environment(Environment.NORMAL).generateStructures(false).type(WorldType.FLAT).createWorld();
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setPVP(false);
            world.setTime(6000L);
        }
    }

    public World getRoomWorld() {
        return Bukkit.getWorld(ConfigHelper.NAME_ROOM_WORLD.getString());
    }

    public Room getRoom(int x, int z) {
        return loadedRooms.get(x + "," + z);
    }

    public boolean isRoomValid(int x, int z) {
        Block checkBlock = getRoomWorld().getBlockAt(x * 28, 3, z * 28);
        return checkBlock != null && checkBlock.getType() == Material.DIAMOND_BLOCK;
    }

    public boolean shouldRoomReset(int x, int z) {
        if (isRoomValid(x, z)) {
            Log.debug("room at " + x + "," + z + " should reset");
            Block checkBlock = getRoomWorld().getBlockAt(x * 28, 2, z + 28);
            return checkBlock != null && checkBlock.getType() == Material.GOLD_BLOCK;
        } else {
            return false;
        }
    }

    public boolean isRoomLoaded(int x, int z) {
        return isRoomValid(x, z) && getRoom(x, z) != null;
    }

    public Room loadRoom(int x, int z) {
        Log.debug("loadRoom at " + x + ", " + z);
        if (isRoomValid(x, z)) {
            Log.debug("Room is valid, loading");
            loadedRooms.put(x + "," + z, new Room(x, z));
            return getRoom(x, z);
        }
        return null;
    }

    public Room createRoom(int x, int z) {
        Log.debug("createRoom at " + x + ", " + z);
        if (isRoomValid(x, z)) {
            Log.debug("Room is valid");
            if (!isRoomLoaded(x, z)) {
                Log.debug("Room isn't loaded, loading");
                loadRoom(x, z);
            }
            return getRoom(x, z);
        } else {
            Log.debug("Room is invalid, creating");
            resetRoom(x, z);
            return getRoom(x, z);
        }
    }

    public void resetRoom(int x, int z) {
        if (shouldRoomReset(x, z)) {
            WorldEditPlugin we = HookHelper.getWorldEdit();
            SchematicFormat format = SchematicFormat.getFormat(roomFile);
            CuboidClipboard cb;
            try {
                Log.debug("Getting schematic");
                cb = format.load(roomFile);
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
            Log.debug("Got schematic");
            EditSession session = new EditSession(new BukkitWorld(getRoomWorld()), Integer.MAX_VALUE);
            com.sk89q.worldedit.Vector origin = new com.sk89q.worldedit.Vector((x * 28) + 1, 4, (z * 28) + 1);
            try {
                Log.debug("Pasting");
                cb.paste(session, origin, true, false);
                Log.debug("Pasted successfully");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public Set<Room> getNextRooms(int count) {
        return getNextRooms(count, false);
    }

    public Set<Room> getNextRooms(int count, boolean markInUse) {
        Set<Room> returnSet = new HashSet<Room>();
        int loop = 0, x = 0, z = 0;
        while (returnSet.size() <= count) {
            loop++;
            //Check
            if (addRoomIfAvailable(x, z, returnSet) >= count) {
                return returnSet;
            }

            x++;

            //Check
            if (addRoomIfAvailable(x, z, returnSet) >= count) {
                return returnSet;
            }

            for (int i = 0; i < Math.abs(1 + 2 * (loop - 1)); i++) {
                z--;
                //Check
                if (addRoomIfAvailable(x, z, returnSet) >= count) {
                    return returnSet;
                }
            }
            for (int i = 0; i < Math.abs(2 * loop); i++) {
                x--;
                //Check
                if (addRoomIfAvailable(x, z, returnSet) >= count) {
                    return returnSet;
                }
            }
            for (int i = 0; i < Math.abs(2 * loop); i++) {
                z++;
                //Check
                if (addRoomIfAvailable(x, z, returnSet) >= count) {
                    return returnSet;
                }
            }
            for (int i = 0; i < Math.abs(2 * loop); i++) {
                x++;
                //Check
                if (addRoomIfAvailable(x, z, returnSet) >= count) {
                    return returnSet;
                }
            }
        }
        if (markInUse) {
            for (Room room : returnSet) room.setInUse(true);
        }
        return returnSet;
    }

    private int addRoomIfAvailable(int x, int z, Set<Room> set) {
        Room room = createRoom(x, z);
        if (room != null && !room.isInUse()) {
            set.add(room);
            resetRoom(x, z);
        }
        return set.size();
    }

}