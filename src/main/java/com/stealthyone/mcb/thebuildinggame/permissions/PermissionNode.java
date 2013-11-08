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
package com.stealthyone.mcb.thebuildinggame.permissions;

import org.bukkit.command.CommandSender;

public enum PermissionNode {

    ADMIN_ARENA_CREATE,
    ADMIN_ARENA_MODIFY,

    ARENAS_INFO,
    ARENAS_LIST,

    DEV_DEBUG,

    GAME_PLAY;

    private String permission;

    private PermissionNode() {
        this.permission = "thebuildinggame." + toString().toLowerCase().replace("_", ".");
    }

    public boolean isAllowed(CommandSender sender) {
        return sender.hasPermission(permission);
    }

}