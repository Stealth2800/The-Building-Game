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
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.stealthyone.mcb.stbukkitlib.lib.hooks.HookHelper;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame.Log;
import com.stealthyone.mcb.thebuildinggame.backend.GameBackend;
import com.stealthyone.mcb.thebuildinggame.backend.players.BgPlayer;
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
        WorldGuardPlugin worldguard = HookHelper.getWorldGuard();
        RegionManager regionManager = worldguard.getRegionManager(world);
        GlobalProtectedRegion region;
        if (regionManager.getRegion("__global__") == null) {
            region = new GlobalProtectedRegion("__global__");
            regionManager.addRegion(region);
        } else {
            region = (GlobalProtectedRegion) regionManager.getRegion("__global__");
        }
        region.setFlag(DefaultFlag.BUILD, State.DENY);
        region.setFlag(DefaultFlag.FIRE_SPREAD, State.DENY);
        region.setFlag(DefaultFlag.MOB_SPAWNING, State.DENY);
        region.setFlag(DefaultFlag.TNT, State.DENY);
        region.setFlag(DefaultFlag.OTHER_EXPLOSION, State.DENY);
        region.setFlag(DefaultFlag.PISTONS, State.DENY);
        region.setFlag(DefaultFlag.ITEM_DROP, State.DENY);
        region.setFlag(DefaultFlag.EXP_DROPS, State.DENY);
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

    public void markRoomModified(int x, int z, boolean modified) {
        if (isRoomValid(x, z)) {
            Log.debug("room is valid, marking modified: " + modified);
            Block checkBlock = getRoomWorld().getBlockAt(x * 28, 2, z * 28);
            checkBlock.setType(modified ? Material.GOLD_BLOCK : Material.DIRT);
        }
    }

    public boolean shouldRoomReset(int x, int z) {
        if (isRoomValid(x, z)) {
            Block checkBlock = getRoomWorld().getBlockAt(x * 28, 2, z * 28);
            return checkBlock != null && !getRoom(x, z).isInUse() && checkBlock.getType() == Material.GOLD_BLOCK;
        } else {
            return false;
        }
    }

    public boolean isRoomLoaded(int x, int z) {
        return isRoomValid(x, z) && getRoom(x, z) != null;
    }

    public Room loadRoom(int x, int z) {
        Log.debug("loadRoom at " + x + ", " + z);
        Room room = getRoom(x, z);
        if (room == null && isRoomValid(x, z)) {
            Log.debug("Room is valid, loading");
            loadedRooms.put(x + "," + z, new Room(x, z));
            createRegion(x, z);
            return getRoom(x, z);
        }
        return room;
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
            //Create room
            return placeRoom(x, z);
        }
    }

    public Room placeRoom(int x, int z) {
        SchematicFormat format = SchematicFormat.getFormat(roomFile);
        CuboidClipboard cb;
        try {
            Log.debug("Getting schematic");
            cb = format.load(roomFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        Log.debug("Got schematic");
        EditSession session = new EditSession(new BukkitWorld(getRoomWorld()), Integer.MAX_VALUE);
        com.sk89q.worldedit.Vector origin = new com.sk89q.worldedit.Vector((x * 28) + 1, 4, (z * 28) + 1);
        try {
            Log.debug("Pasting");
            cb.paste(session, origin, false, false);
            Log.debug("Pasted successfully");
            return loadRoom(x, z);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void resetRoom(int x, int z) {
        if (shouldRoomReset(x, z)) {
            Log.debug("Room at " + x + ", " + z + " should reset");
            if (!placeRoom(x, z).isInUse()) markRoomModified(x, z, false);;
        }
    }

    public Set<Room> getNextRooms(int count) {
        return getNextRooms(count, false);
    }

    public Set<Room> getNextRooms(int count, boolean markInUse) {
        Log.debug("getRooms, count: " + count);
        long startTime = System.currentTimeMillis();
        Set<Room> returnSet = new HashSet<Room>();
        int loop = 0, x = 0, z = 0;
        outerloop:
        while (returnSet.size() < count || System.currentTimeMillis() - startTime < 10000) {
            loop++;
            //Check
            if (addRoomIfAvailable(x, z, returnSet) == count) {
                Log.debug("count equal, end loop");
                break;
            }

            x++;

            //Check
            if (addRoomIfAvailable(x, z, returnSet) == count) {
                Log.debug("count equal, end loop");
                break;
            }

            for (int i = 0; i < Math.abs(1 + 2 * (loop - 1)); i++) {
                z--;
                //Check
                if (addRoomIfAvailable(x, z, returnSet) == count) {
                    Log.debug("count equal, end loop");
                    break outerloop;
                }
            }

            for (int i = 0; i < Math.abs(2 * loop); i++) {
                x--;
                //Check
                if (addRoomIfAvailable(x, z, returnSet) == count) {
                    Log.debug("count equal, end loop");
                    break outerloop;
                }
            }

            for (int i = 0; i < Math.abs(2 * loop); i++) {
                z++;
                //Check
                if (addRoomIfAvailable(x, z, returnSet) == count) {
                    Log.debug("count equal, end loop");
                    break outerloop;
                }
            }

            for (int i = 0; i < Math.abs(2 * loop); i++) {
                x++;
                //Check
                if (addRoomIfAvailable(x, z, returnSet) == count) {
                    Log.debug("count equal, end loop");
                    break outerloop;
                }
            }
        }
        if (markInUse) {
            Log.debug("setting rooms in use");
            for (Room room : returnSet) room.setInUse(true);
        }
        return returnSet;
    }

    private int addRoomIfAvailable(int x, int z, Set<Room> set) {
        Room room = createRoom(x, z);
        if (room != null && !room.isInUse()) {
            Log.debug("room at " + x + ", " + z + " is not in use");
            set.add(room);
            resetRoom(x, z);
            Log.debug("room added to set");
        }
        Log.debug("set size: " + set.size());
        return set.size();
    }

    public ProtectedRegion createRegion(int x, int z) {
        if (isRoomValid(x, z)) {
            WorldGuardPlugin worldguard = HookHelper.getWorldGuard();
            String regionName = "bgroom_" + x + "_" + z;
            RegionManager regionManager = worldguard.getRegionManager(getRoomWorld());
            ProtectedRegion region = regionManager.getRegion(regionName);
            if (region == null) {
                com.sk89q.worldedit.BlockVector l1 = new com.sk89q.worldedit.BlockVector((x * 28) + 1, 4, (z * 28) + 1);
                com.sk89q.worldedit.BlockVector l2 = new com.sk89q.worldedit.BlockVector((x * 28) + 24, 17, (z * 28) + 24);

                region = new ProtectedCuboidRegion(regionName, l1, l2);
                regionManager.addRegion(region);
            }
            return region;
        }
        return null;
    }

    public void setRoomRegionOwner(int x, int z, BgPlayer player) {
        ProtectedRegion region = createRegion(x, z);
        if (region != null) {
            DefaultDomain domain = new DefaultDomain();
            if (player != null) domain.addPlayer(player.getName());
            region.setOwners(domain);
        }
    }

}