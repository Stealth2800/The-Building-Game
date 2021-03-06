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
package com.stealthyone.mcb.thebuildinggame.backend;

import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.ArenaManager;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.rooms.RoomManager;
import com.stealthyone.mcb.thebuildinggame.backend.players.PlayerManager;
import com.stealthyone.mcb.thebuildinggame.backend.signs.SignManager;

public class GameBackend {

    private TheBuildingGame plugin;

    private ArenaManager arenaManager;
    private PlayerManager playerManager;
    private RoomManager roomManager;
    private SignManager signManager;

    public GameBackend(TheBuildingGame plugin) {
        this.plugin = plugin;
        this.arenaManager = new ArenaManager(plugin, this);
        this.playerManager = new PlayerManager(plugin, this);
        this.roomManager = new RoomManager(plugin, this);
        this.signManager = new SignManager(plugin, this);
    }

    public void saveAll() {
        arenaManager.save();
        playerManager.save();
        signManager.save();
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public SignManager getSignManager() {
        return signManager;
    }

}