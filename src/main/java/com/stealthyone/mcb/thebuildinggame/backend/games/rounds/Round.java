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
package com.stealthyone.mcb.thebuildinggame.backend.games.rounds;

import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame.Log;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.rooms.Room;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.rooms.RoomManager;
import com.stealthyone.mcb.thebuildinggame.backend.games.GameInstance;
import com.stealthyone.mcb.thebuildinggame.backend.players.BgPlayer;
import com.stealthyone.mcb.thebuildinggame.messages.NoticeMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Round {

    protected GameInstance gameInstance;
    protected int roundNum;
    protected Map<BgPlayer, Room> roomAllocation = new HashMap<BgPlayer, Room>();

    public Round(GameInstance gameInstance, int roundNum) {
        this.gameInstance = gameInstance;
        this.roundNum = roundNum;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Round && ((Round) object).gameInstance.equals(gameInstance);
    }

    public void allocateRooms() {
        Log.debug("allocating rooms");
        Map<Integer, BgPlayer> players = gameInstance.getPlayerIds();
        int playerCount = players.size();
        List<Room> rooms = gameInstance.getRooms(this);
        RoomManager roomManager = TheBuildingGame.getInstance().getGameBackend().getRoomManager();
        for (int i = 1; i <= playerCount; i++) {
            Log.debug("allocation, i: " + i);
            int roomNum = (i + roundNum) - 1;
            while (roomNum > playerCount) roomNum -= playerCount;
            BgPlayer player = players.get(i);
            Room room = rooms.get(roomNum - 1);
            room.setInUse(true);
            roomManager.setRoomRegionOwner(room.getX(), room.getZ(), player);
            roomAllocation.put(player, room);
        }
    }

    public Room getRoom(BgPlayer player) {
        return roomAllocation.get(player);
    }

    public void teleportPlayers() {
        Log.debug("teleport players, players: " + roomAllocation.size());
        for (Entry<BgPlayer, Room> entry : roomAllocation.entrySet()) {
            Log.debug("teleporting " + entry.getKey().getName());
            entry.getValue().teleportPlayer(entry.getKey());
        }
    }

    public void cleanup() {
        RoomManager roomManager = TheBuildingGame.getInstance().getGameBackend().getRoomManager();
        for (Room room : roomAllocation.values()) {
            room.setInUse(false);
            roomManager.setRoomRegionOwner(room.getX(), room.getZ(), null);
        }
        roomAllocation.clear();
    }

    public void sendReadyMessage(int readyCount) {
        gameInstance.sendMessage(NoticeMessage.PLAYER_READY_NOTICE, Integer.toString(readyCount), readyCount == 1 ? "" : "s", readyCount == 1 ? "is" : "are");
    }

    public abstract void sendStartingMessage();

    public abstract void endRound();

}