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

import com.stealthyone.mcb.stbukkitlib.lib.messages.IMessagePath;
import com.stealthyone.mcb.stbukkitlib.lib.messages.MessageRetriever;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public enum ErrorMessage implements IMessagePath {

    ALREADY_MARKED_READY,
    ALREADY_IN_GAME,
    ALREADY_IN_OTHER_GAME,
    NOT_IN_GAME,
    NOT_IN_ANY_GAME,

    ARENA_INVALID_CONFIGURATION,
    ARENA_ALREADY_ENABLED,
    ARENA_ALREADY_DISABLED,
    ARENA_MUST_BE_DISABLED,

    INVALID_ROOM,
    INVALID_ROUND_TIME,
    INVALID_RESULT_NUMBER,
    INVALID_PLAYER_COUNT,
    NOT_THINKING_ROUND,
    CANNOT_MARK_READY,
    GAME_CANNOT_PLACE_SIGN,

    NOT_GUESSING_ROUND,
    NOT_RESULTS_ROUND,
    GUESS_ALREADY_SUBMITTED,

    NO_GAME_IN_PROGRESS,

    UNKNOWN_ARENA_SETTING,
    UNABLE_TO_SET_NICKNAME,

    IDEA_ALREADY_SUBMITTED,

    ARENA_DOES_NOT_EXIST,

    CANNOT_JOIN_GAME,

    PLAYERS_IN_ARENA,

    MUST_BE_INT,
	MUST_BE_PLAYER,
	NO_PERMISSION,
    UNKNOWN_COMMAND;
	
	private final String PREFIX = "messages.errors.";
	
	private String path;
	private boolean isList;
	
	private ErrorMessage() {
		this(false);
	}
	
	private ErrorMessage(boolean isList) {
		this.path = this.toString().toLowerCase();
		this.isList = isList;
	}
	
	@Override
	public final String getPrefix() {
		return PREFIX;
	}

	@Override
	public final String getMessagePath() {
		return this.path;
	}
	
	@Override
	public final boolean isList() {
		return this.isList;
	}
	
	public final String getFirstLine() {
		return TheBuildingGame.getInstance().getMessageManager().getMessage(this)[0];
	}
	
	public final void sendTo(CommandSender sender) {
		MessageRetriever messageRetriever = TheBuildingGame.getInstance().getMessageManager();
		String[] messages = messageRetriever.getMessage(this);
		
		for (String message : messages) {
			message = ChatColor.RED + message;
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{TAG}", messageRetriever.getTag())));
		}
	}
	
	public final void sendTo(CommandSender sender, String... replacements) {
		MessageRetriever messageRetriever = TheBuildingGame.getInstance().getMessageManager();
		String[] messages = messageRetriever.getMessage(this);
		
		for (String message : messages) {
			message = ChatColor.RED + message;
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + String.format(message.replace("{TAG}", messageRetriever.getTag()), (Object[]) replacements)));
		}
	}
	
}