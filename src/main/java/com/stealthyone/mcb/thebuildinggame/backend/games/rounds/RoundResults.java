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

import com.stealthyone.mcb.thebuildinggame.backend.arenas.rooms.Room;
import com.stealthyone.mcb.thebuildinggame.backend.games.GameInstance;
import com.stealthyone.mcb.thebuildinggame.backend.players.BgPlayer;
import com.stealthyone.mcb.thebuildinggame.messages.NoticeMessage;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RoundResults extends RoundBuild {

    private Map<BgPlayer, List<String>> resultMap = new HashMap<BgPlayer, List<String>>();
    private List<BgPlayer> resultNumbers = new ArrayList<BgPlayer>();

    private List<Room> tempRoomNumbers = new ArrayList<Room>();

    public RoundResults(GameInstance gameInstance, int roundNum) {
        super(gameInstance, roundNum);
        resultNumbers = new ArrayList<BgPlayer>(gameInstance.getPlayerIds().values());
    }

    @Override
    public void sendStartingMessage() {
        gameInstance.sendMessage(NoticeMessage.START_MESSAGE_RESULTS);
    }

    public void indexResults() {
        int roundCount = gameInstance.getPlayerCount();
        for (Entry<Integer, BgPlayer> entry : gameInstance.getPlayerIds().entrySet()) {
            BgPlayer player = entry.getValue();
            int playerId = entry.getKey();
            for (int i = 1; i <= roundCount; i++) {
                Round round = gameInstance.getRound(i);
                if (i == 1) {
                    //Think
                    addResult(player, NoticeMessage.RESULTS_THINK.getMessage(player.getName(), ((RoundThink) round).getIdea(player)));
                } else if (i % 2 == 0) {
                    //Build round
                    int finalId = playerId - (i - 1);
                    while (finalId <= 0) finalId += roundCount;
                    BgPlayer buildPlayer = gameInstance.getPlayerById(finalId);
                    RoundBuild roundCast = (RoundBuild) round;
                    String buildIdea = roundCast.getIdea(buildPlayer);
                    int roomNum = getRoomNumber(roundCast.getRoom(buildPlayer));
                    addResult(player, NoticeMessage.RESULTS_BUILD.getMessage(buildPlayer.getName(), buildIdea, Integer.toString(roomNum)));
                } else {
                    //Guess
                    int finalId = playerId - (i - 1);
                    while (finalId <= 0) finalId += roundCount;
                    BgPlayer guessPlayer = gameInstance.getPlayerById(finalId);
                    RoundGuess roundCast = (RoundGuess) round;
                    String guess = roundCast.getGuess(guessPlayer);
                    addResult(player, NoticeMessage.RESULTS_GUESS.getMessage(guessPlayer.getName(), guess));
                }
            }
        }
    }

    public List<Room> getRoomNumbers() {
        return tempRoomNumbers;
    }

    public List<BgPlayer> getResultNumbers() {
        return resultNumbers;
    }

    public int getRoomNumber(Room room) {
        if (!tempRoomNumbers.contains(room)) {
            tempRoomNumbers.add(room);
        }
        return tempRoomNumbers.indexOf(room) + 1;
    }

    public void addResult(BgPlayer player, String result) {
        List<String> list = resultMap.get(player);
        if (list == null) list = new ArrayList<String>();
        list.add(ChatColor.translateAlternateColorCodes('&', result));
        resultMap.put(player, list);
    }

    public Entry<BgPlayer, List<String>> getResults(int resultNum) {
        BgPlayer player = resultNumbers.get(resultNum);
        if (player == null) return null;

        for (Entry<BgPlayer, List<String>> entry : resultMap.entrySet()) {
            if (entry.getKey().equals(player)) return entry;
        }
        return null;
    }

    @Override
    public void startRound() {
        super.startRound();
        indexResults();
    }

}