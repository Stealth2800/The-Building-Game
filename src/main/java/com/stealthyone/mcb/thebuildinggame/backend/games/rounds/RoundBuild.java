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
        int playerCount = gameInstance.getPlayerCount();
        gameInstance.sendMessage(NoticeMessage.START_MESSAGE_BUILD);
        for (BgPlayer player : gameInstance.getPlayerIds().values()) {
            Player rawPlayer = player.getPlayer();
            int playerId = gameInstance.getIdByPlayer(player);
            if (playerId == -1) {
                rawPlayer.sendMessage(ChatColor.RED + "ERROR");
                return;
            }
            int nextId = playerId + 1;
            while (nextId > playerCount) nextId -= playerCount;

            String idea;
            Round prevRound = gameInstance.getRound(roundNum - 1);
            if (prevRound instanceof RoundThink) {
                idea = ((RoundThink) prevRound).getIdea(gameInstance.getPlayerById(nextId));
            } else if (prevRound instanceof RoundGuess) {
                idea = ((RoundGuess) prevRound).getGuess(gameInstance.getPlayerById(nextId));
            } else {
                rawPlayer.sendMessage(ChatColor.RED + "ERROR");
                return;
            }
            NoticeMessage.BUILD_NOTICE.sendTo(player.getPlayer(), idea);
        }
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

}