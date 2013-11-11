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

import com.stealthyone.mcb.thebuildinggame.backend.games.GameInstance;
import com.stealthyone.mcb.thebuildinggame.backend.players.BgPlayer;
import com.stealthyone.mcb.thebuildinggame.messages.NoticeMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class RoundBuild extends Round {

    protected Set<String> completedPlayers = new HashSet<String>();

    public RoundBuild(GameInstance gameInstance, int roundNum) {
        super(gameInstance, roundNum);
    }

    @Override
    public void sendStartingMessage() {
        gameInstance.sendMessage(NoticeMessage.START_MESSAGE_BUILD);
        for (BgPlayer player : gameInstance.getPlayerIds().values()) {
            NoticeMessage.BUILD_NOTICE.sendTo(player.getPlayer(), getIdea(player));
        }
    }

    public BgPlayer getLastIdeaPlayer(BgPlayer player) {
        int playerCount = gameInstance.getPlayerCount();
        Player rawPlayer = player.getPlayer();
        int playerId = gameInstance.getIdByPlayer(player);
        if (playerId == -1) {
            rawPlayer.sendMessage(ChatColor.RED + "ERROR");
            return null;
        }
        int nextId = playerId + 1;
        while (nextId > playerCount) nextId -= playerCount;
        return gameInstance.getPlayerById(nextId);
    }

    public String getIdea(BgPlayer player) {
        BgPlayer lastPlayer = getLastIdeaPlayer(player);
        if (lastPlayer == null) return null;
        String idea;
        Round prevRound = gameInstance.getRound(roundNum - 1);
        if (prevRound instanceof RoundThink) {
            idea = ((RoundThink) prevRound).getIdea(lastPlayer);
        } else if (prevRound instanceof RoundGuess) {
            idea = ((RoundGuess) prevRound).getGuess(lastPlayer);
        } else {
            player.getPlayer().sendMessage(ChatColor.RED + "ERROR");
            return null;
        }
        return idea;
    }

    public boolean setComplete(BgPlayer player) {
        if (gameInstance.isPlayerJoined(player)) {
            completedPlayers.add(player.getName().toLowerCase());
            sendReadyMessage(completedPlayers.size());
            gameInstance.getScore(player).setScore(1);
            return true;
        } else {
            return false;
        }
    }

    public boolean isComplete(BgPlayer player) {
        return completedPlayers.contains(player.getName().toLowerCase());
    }

    public boolean allPlayersComplete() {
        return completedPlayers.size() == gameInstance.getPlayerCount();
    }

    @Override
    public void endRound() {
        /*RoundResults resultsRound = (RoundResults) gameInstance.getRound(gameInstance.getPlayerCount() + 1);
        for (BgPlayer player : gameInstance.getPlayerIds().values()) {
            resultsRound.addResult(getLastIdeaPlayer(player), NoticeMessage.RESULTS_BUILD.getMessage(player.getName(), getIdea(player), Integer.toString(resultsRound.getRoomNumber(getRoom(player)))));
        }*/
    }

}