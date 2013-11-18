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
package com.stealthyone.mcb.thebuildinggame.messages;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public enum UsageMessage {

    ARENA_ENABLE("/%s arena enable <arena ID>"),
    ARENA_DISABLE("/%s arena disable <arena ID>"),
    ARENA_INFO("/%s arena info <arena ID>"),

    GAME_GUESS("/%s game guess <guess word/phrase>"),
    GAME_END("/%s game end <arena ID>"),
    GAME_IDEA("/%s game idea <idea word/phrase>"),
    GAME_JOIN("/%s game join <arena ID"),
    GAME_RESULTS("/%s game results <result number>"),
    GAME_ROOM("/%s game room <room number>");
	
	private String message;
	
	private UsageMessage(String message) {
		this.message = message;
	}
	
	public final void sendTo(CommandSender sender, String label) {
		sender.sendMessage(ChatColor.DARK_RED + "USAGE: " + ChatColor.RED + String.format(message, label));
	}
	
}