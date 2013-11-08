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
package com.stealthyone.mcb.thebuildinggame.backend.players;

import com.stealthyone.mcb.thebuildinggame.TheBuildingGame.Log;
import com.stealthyone.mcb.thebuildinggame.backend.games.GameInstance;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BgPlayer {

    private String playerName;

    private GameInstance currentGame;

    public BgPlayer(Player player) {
        this.playerName = player.getName();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof BgPlayer && ((BgPlayer) object).playerName.equals(this.playerName);
    }

    public String getName() {
        return playerName;
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(playerName);
    }

    public Player getPlayer() {
        return getOfflinePlayer().getPlayer();
    }

    public boolean isOnline() {
        return getOfflinePlayer().isOnline();
    }

    public GameInstance getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(GameInstance gameInstance) {
        Log.debug("setCurrentGame for player, gameInstance is null: " + (gameInstance == null));
        currentGame = gameInstance;
    }

    public boolean isInGame() {
        return currentGame != null;
    }

}