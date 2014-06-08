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
import com.stealthyone.mcb.stbukkitlib.utils.QuickHashMap;
import com.stealthyone.mcb.thebuildinggame.util.ConfigOption;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class Arena {

    private YamlFileManager file;

    private int id;

    private Map<String, ConfigOption> configOptions = Collections.unmodifiableMap(new QuickHashMap<String, ConfigOption>()
                    .put(ArenaConfig.NICKNAME, new ConfigOption<>(ArenaConfig.NICKNAME, (String) null))
                    .put(ArenaConfig.ENABLED, new ConfigOption<>(ArenaConfig.ENABLED, false))
                    .put(ArenaConfig.MAX_PLAYERS, new ConfigOption<>(ArenaConfig.MAX_PLAYERS, 7))
                    .put(ArenaConfig.ROOM_TYPE, new ConfigOption<>(ArenaConfig.ROOM_TYPE, -1))
                    .put(ArenaConfig.ROOM_SPACING, new ConfigOption<>(ArenaConfig.ROOM_SPACING, 1))
                    .put(ArenaConfig.CHAT_ENABLED, new ConfigOption<>(ArenaConfig.CHAT_ENABLED, true))
                    .put(ArenaConfig.TIME_RESULTS_ROUND, new ConfigOption<>(ArenaConfig.TIME_RESULTS_ROUND, true))
                    .put(ArenaConfig.ROUND_TIME, new ConfigOption<>(ArenaConfig.ROUND_TIME, 300))
                    .build()
    );
    
    private Set<String> saveQueue = new HashSet<>();

    public Arena(int id, YamlFileManager file) {
        this.id = id;
        this.file = file;
        load();
    }

    public int getId() {
        return id;
    }

    private void load() {
        ConfigurationSection config = file.getConfig();
        for (ConfigOption opt : configOptions.values()) {
            opt.load(config);
        }
    }

    public void save() {
        ConfigurationSection config = file.getConfig();
        for (String name : saveQueue) {
            configOptions.get(name).save(config);
        }
        saveQueue.clear();
        file.saveFile();
    }

    public String getNickname() {
        String nick = ((ConfigOption<String>) configOptions.get(ArenaConfig.NICKNAME.toString())).getValue();
        return nick != null ? nick : "Arena " + id;
    }

    public boolean setNickname(String name) {
        if (name.equals(getNickname())) {
            return false;
        }

        if (name.equals("")) {
            name = null;
        }

        ((ConfigOption<String>) configOptions.get(ArenaConfig.NICKNAME.toString())).setValue(name);
        saveQueue.add(ArenaConfig.NICKNAME.toString());
        return true;
    }

    public boolean isEnabled() {
        return ((ConfigOption<Boolean>) configOptions.get(ArenaConfig.ENABLED.toString())).getValue();
    }

    public boolean setEnabled(boolean enabled) {
        if (enabled == isEnabled()) {
            return false;
        }

        ((ConfigOption<Boolean>) configOptions.get(ArenaConfig.ENABLED.toString())).setValue(enabled);
        saveQueue.add(ArenaConfig.ENABLED.toString());
        return true;
    }

    public int getMaxPlayers() {
        return ((ConfigOption<Integer>) configOptions.get(ArenaConfig.MAX_PLAYERS.toString())).getValue();
    }

    public boolean setMaxPlayers(int maxPlayers) {
        if (maxPlayers == getMaxPlayers()) {
            return false;
        }

        ((ConfigOption<Integer>) configOptions.get(ArenaConfig.MAX_PLAYERS.toString())).setValue(maxPlayers);
        saveQueue.add(ArenaConfig.MAX_PLAYERS.toString());
        return true;
    }

    public int getRoomType() {
        return ((ConfigOption<Integer>) configOptions.get(ArenaConfig.ROOM_TYPE.toString())).getValue();
    }

    public boolean setRoomType(int roomTypeId) {
        if (roomTypeId == getRoomType()) {
            return false;
        }

        ((ConfigOption<Integer>) configOptions.get(ArenaConfig.ROOM_TYPE.toString())).setValue(roomTypeId);
        saveQueue.add(ArenaConfig.ROOM_TYPE.toString());
        return true;
    }

    public int getRoomSpacing() {
        return ((ConfigOption<Integer>) configOptions.get(ArenaConfig.ROOM_SPACING.toString())).getValue();
    }

    public boolean setRoomSpacing(int roomSpacing) {
        if (roomSpacing == getRoomSpacing()) {
            return false;
        }

        ((ConfigOption<Integer>) configOptions.get(ArenaConfig.ROOM_SPACING.toString())).setValue(roomSpacing);
        saveQueue.add(ArenaConfig.ROOM_SPACING.toString());
        return true;
    }

    public boolean isChatEnabled() {
        return ((ConfigOption<Boolean>) configOptions.get(ArenaConfig.CHAT_ENABLED.toString())).getValue();
    }

    public boolean setChatEnabled(boolean enabled) {
        if (enabled == isChatEnabled()) {
            return false;
        }

        ((ConfigOption<Boolean>) configOptions.get(ArenaConfig.CHAT_ENABLED.toString())).setValue(enabled);
        saveQueue.add(ArenaConfig.CHAT_ENABLED.toString());
        return true;
    }

    public boolean timeResultsRound() {
        return ((ConfigOption<Boolean>) configOptions.get(ArenaConfig.TIME_RESULTS_ROUND.toString())).getValue();
    }

    public boolean setTimeResultsRound(boolean enabled) {
        if (enabled == timeResultsRound()) {
            return false;
        }

        ((ConfigOption<Boolean>) configOptions.get(ArenaConfig.TIME_RESULTS_ROUND.toString())).setValue(enabled);
        saveQueue.add(ArenaConfig.TIME_RESULTS_ROUND.toString());
        return true;
    }

    public int getRoundTime() {
        return ((ConfigOption<Integer>) configOptions.get(ArenaConfig.ROUND_TIME.toString())).getValue();
    }

    public boolean setRoundTime(int roundTime) {
        if (roundTime == getRoundTime()) {
            return false;
        }

        ((ConfigOption<Integer>) configOptions.get(ArenaConfig.ROUND_TIME.toString())).setValue(roundTime);
        saveQueue.add(ArenaConfig.ROUND_TIME.toString());
        return true;
    }

}