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
package com.stealthyone.mcb.thebuildinggame.backend.arenas;

import com.stealthyone.mcb.stbukkitlib.storage.YamlFileManager;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;

import java.io.File;
import java.util.*;

/*
 * The arena manager is in charge of tracking arenas.
 * Arenas contain the configuration for every individual Building Game type, such as the max amount of players, etc.
 */
public class ArenaManager {

    private TheBuildingGame plugin;

    private File arenaDir;
    private Map<Integer, Arena> loadedArenas = new HashMap<>();

    public ArenaManager(TheBuildingGame plugin) {
        this.plugin = plugin;
        arenaDir = new File(plugin.getDataFolder() + File.separator + "data" + File.separator + "arenas");
        arenaDir.mkdir();
    }

    public int reload() {
        loadedArenas.clear();
        for (File rawFile : arenaDir.listFiles()) {
            if (rawFile.getName().matches("arena[0-9]+.yml")) {
                YamlFileManager file = new YamlFileManager(rawFile);
                int id;
                try {
                    id = Integer.parseInt(rawFile.getName().replace(".yml", "").replace("arena", ""));
                } catch (Exception ex) {
                    plugin.getLogger().severe("Invalid arena file '" + rawFile.getName() + "' found. I was unable to load any data from it.");
                    continue;
                }
                Arena arena = new Arena(id, file);
                loadedArenas.put(id, arena);
            }
        }
        return loadedArenas.size();
    }

    public Arena getArena(int id) {
        return loadedArenas.get(id);
    }

    public List<Arena> getLoadedArenas() {
        List<Arena> newList = new ArrayList<>(loadedArenas.values());
        Collections.sort(newList, new Comparator<Arena>() {
            @Override
            public int compare(Arena o1, Arena o2) {
                int id1 = o1.getId();
                int id2 = o2.getId();
                if (id1 > id2) {
                    return 1;
                } else if (id1 < id2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return newList;
    }

}