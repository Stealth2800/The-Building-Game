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
package com.stealthyone.mcb.thebuildinggame.backend.games;

import org.bukkit.ChatColor;

public enum GameState {

    INACTIVE(ChatColor.DARK_GRAY), //Arena disabled
    WAITING(ChatColor.GREEN), //Waiting for players
    STARTING(ChatColor.YELLOW), //Game starting, players can't join
    IN_PROGRESS(ChatColor.RED), //Game in progress, players can't join
    FREEZING(ChatColor.BLUE), //Game in pause, some players leave and can't join
    ENDING(ChatColor.GOLD); //Finishing up, kicking players, cleanup, etc.

    private String text;

    private GameState(ChatColor color) {
        this.text = color + toString().replace("_", " ");
    }

    public String getText() {
        return text;
    }

}