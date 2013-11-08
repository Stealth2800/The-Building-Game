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
package com.stealthyone.mcb.thebuildinggame.backend.arenas;

import com.stealthyone.mcb.stbukkitlib.lib.storage.YamlFileManager;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame.Log;
import com.stealthyone.mcb.thebuildinggame.backend.GameBackend;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ArenaManager {

    private TheBuildingGame plugin;
    private GameBackend gameBackend;

    private YamlFileManager arenaFile;
    private Map<Integer, Arena> loadedArenas = new HashMap<Integer, Arena>();

    public ArenaManager(TheBuildingGame plugin, GameBackend gameBackend) {
        this.plugin = plugin;
        this.gameBackend = gameBackend;
        this.arenaFile = new YamlFileManager(plugin.getDataFolder() + File.separator + "arenas.yml");
        Log.info("Loaded " + reloadArenas() + " arenas.");
    }

    public void save() {
        arenaFile.saveFile();
    }

    public Map<Integer, Arena> getArenas() {
        return loadedArenas;
    }

    public int reloadArenas() {
        loadedArenas.clear();
        FileConfiguration arenaConfig = arenaFile.getConfig();
        for (String key : arenaConfig.getKeys(false)) {
            loadArena(arenaConfig.getConfigurationSection(key));
        }
        return loadedArenas.size();
    }

    public void loadArena(ConfigurationSection config) {
        Arena arena = new Arena(config);
        loadedArenas.put(arena.getId(), arena);
    }

    public Arena createArena() {
        int id = getNextId();
        ConfigurationSection config = arenaFile.getConfig().createSection(Integer.toString(id));
        config.set("enabled", true);
        config.set("maxPlayers", 7);
        config.set("roundTime", 300);
        loadArena(config);
        return getArena(id);
    }

    public int getNextId() {
        FileConfiguration arenaConfig = arenaFile.getConfig();
        int i = 1;
        while (arenaConfig.getConfigurationSection(Integer.toString(i)) != null) {
            i++;
        }
        return i;
    }

    public Arena getArena(int arenaId) {
        return loadedArenas.get(arenaId);
    }

}