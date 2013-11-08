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

import java.util.HashMap;
import java.util.Map;

public class RoundThink extends Round {

    private Map<BgPlayer, String> ideas = new HashMap<BgPlayer, String>();

    public RoundThink(GameInstance gameInstance, int roundNum) {
        super(gameInstance, roundNum);
    }

    public boolean submitIdea(BgPlayer player, String idea) {
        if (gameInstance.isPlayerJoined(player)) {
            ideas.put(player, idea);
            sendReadyMessage(ideas.size());
            gameInstance.getScore(player).setScore(1);
            return true;
        } else {
            return false;
        }
    }

    public String getIdea(BgPlayer player) {
        return ideas.get(player);
    }

    public boolean hasEveryoneEnteredIdea() {
        return ideas.size() == gameInstance.getPlayerCount();
    }

    @Override
    public void sendStartingMessage() {
        gameInstance.sendMessage(NoticeMessage.START_MESSAGE_THINK);
    }

}