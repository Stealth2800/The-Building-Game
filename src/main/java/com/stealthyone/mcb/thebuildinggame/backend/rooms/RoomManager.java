/*
 * The Building Game - Bukkit Plugin
 * Copyright (C) 2014 Stealth2800 <stealth2800@stealthyone.com>
 * Website: <http://stealthyone.com>
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
package com.stealthyone.mcb.thebuildinggame.backend.rooms;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.config.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the different types of rooms for Building Game arenas along with the world they are placed in.
 */
public class RoomManager {

    private TheBuildingGame plugin;

    private String worldName;

    private File roomDir;
    private File schematicDir;
    private Map<Integer, RoomType> roomTypes = new HashMap<>();

    public RoomManager(TheBuildingGame plugin) {
        this.plugin = plugin;
        roomDir = new File(plugin.getDataFolder() + File.separator + "data" + File.separator + "rooms");
        roomDir.mkdir();
        schematicDir = new File(plugin.getDataFolder() + File.separator + "data" + File.separator + "roomSchematics");
        schematicDir.mkdir();
    }

    public void reload() throws InvalidConfigurationException {
        // Get the world name
        worldName = PluginConfig.ARENAS_WORLD_NAME.getValue();
        if (worldName == null) {
            throw new InvalidConfigurationException("No name is specified for the arena world name in the config.yml file.");
        }

        // Get the world
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            plugin.getLogger().warning("I was unable to find world: '" + worldName + "' so I am creating it for you now. Pardon any lag, it should go away shortly.");
            world = new WorldCreator(worldName).environment(Environment.NORMAL).generateStructures(false).type(WorldType.FLAT).createWorld();
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setTime(6000L);
            world.setPVP(false);
        }

        // Setup the global region on the world
        RegionManager regionManager = WorldGuardPlugin.inst().getRegionManager(world);
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
        return Bukkit.getWorld(worldName);
    }

    public File getRoomDir() {
        return roomDir;
    }

    public File getSchematicDir() {
        return schematicDir;
    }

    public RoomType getRoomType(int roomTypeId) {
        return roomTypeId <= 0 ? null : roomTypes.get(roomTypeId);
    }

}