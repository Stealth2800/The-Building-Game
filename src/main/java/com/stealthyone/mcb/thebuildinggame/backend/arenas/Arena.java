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

import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.exceptions.InvalidArenaException;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.exceptions.PlayersInArenaException;
import com.stealthyone.mcb.thebuildinggame.backend.games.GameInstance;
import com.stealthyone.mcb.thebuildinggame.backend.games.GameState;
import org.bukkit.configuration.ConfigurationSection;

public class Arena {

    private ConfigurationSection config;
    private GameInstance gameInstance;

    public Arena(ConfigurationSection config) {
        this.config = config;
        gameInstance = new GameInstance(this);
        gameInstance.setState(isEnabled() ? GameState.WAITING : GameState.INACTIVE);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Arena && ((Arena) object).getId() == getId();
    }

    public int getId() {
        return Integer.parseInt(config.getName());
    }

    public GameInstance getGameInstance() {
        return gameInstance;
    }

    public int getMaxPlayers() {
        return config.getInt("maxPlayers");
    }

    public boolean setMaxPlayers(int newValue) {
        if (newValue >= 3 && newValue % 2 != 0) {
            config.set("maxPlayers", newValue);
            return true;
        } else {
            return false;
        }
    }

    public int getRoundTime() {
        return config.getInt("roundTime");
    }

    public void setRoundTime(int newValue) {
        config.set("roundTime", newValue);
    }

    public boolean isEnabled() {
        return config.getBoolean("enabled");
    }

    public boolean setEnabled(boolean newValue) {
        if (isEnabled() != newValue) {
            if (newValue && !checkConfiguration()) {
                throw new InvalidArenaException();
            } else if (!newValue) {
                if (gameInstance.getPlayerCount() > 0) {
                    throw new PlayersInArenaException();
                }
            }
            config.set("enabled", newValue);
            gameInstance.setState(newValue ? GameState.WAITING : GameState.INACTIVE);
            return true;
        }
        return false;
    }

    public boolean checkConfiguration() {
        int maxPlayers = getMaxPlayers();
        return getRoundTime() > 0 && maxPlayers >= 3 && maxPlayers % 2 != 0;
    }

    public boolean isChatEnabled() {
        return config.getBoolean("chatEnabled");
    }

    public void setChatEnabled(boolean newValue) {
        if (isChatEnabled() != newValue) {
            config.set("chatEnabled", newValue);
        }
    }

    public void updateSigns() {
        try {
            TheBuildingGame.getInstance().getGameBackend().getSignManager().updateSigns(this);
        } catch (NullPointerException ex) {}
    }

}