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

public enum NoticeMessage implements IMessagePath {

    ARENA_DISABLED,
    ARENA_ENABLED,

    ARENA_SET_MAXPLAYERS,
    ARENA_SET_NICKNAME,
    ARENA_SET_ROUNDTIME,
    ARENA_SET_TIMERESULTSROUND,

    BUILD_NOTICE,

    CREATED_ARENA,

    ENDED_GAME,

    SUBMITTED_GUESS,
    SUBMITTED_IDEA,

    GAME_ENDED_PLAYER_QUIT,
    GAME_TIME_START_NOTICE,
    GAME_TIME_END_NOTICE,
    GAME_TIME_NOTICE,
    GAME_STARTING_CANCELLED,
    GAME_PLAYER_NOTICE,

    JOINED_GAME,
    RE_JOINED_GAME,
    LEFT_GAME,

    GAME_OVER,

    INFO_GAME_LEAVE,

    MARKED_READY,

    START_MESSAGE_THINK,
    START_MESSAGE_RESULTS,
    START_MESSAGE_BUILD,
    START_MESSAGE_GUESS,

    RESULTS_BUILD,
    RESULTS_GUESS,
    RESULTS_THINK,

    PLAYER_READY_NOTICE,

    QUESTION_LEAVE_GAME,

    ROUND_END,
	PLUGIN_RELOADED,
    PLUGIN_SAVED,

    SIGN_CREATED,
    SIGN_DESTROYED;
	
	private final String PREFIX = "messages.notices.";
	
	private String path;
	private boolean isList;
	
	private NoticeMessage() {
		this(false);
	}
	
	private NoticeMessage(boolean isList) {
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

    public final String getMessage() {
        MessageRetriever messageRetriever = TheBuildingGame.getInstance().getMessageManager();
        String[] messages = messageRetriever.getMessage(this);
        return messages[0];
    }

    public final String getMessage(String... replacements) {
        MessageRetriever messageRetriever = TheBuildingGame.getInstance().getMessageManager();
        String[] messages = messageRetriever.getMessage(this);
        return String.format(messages[0].replace("{TAG}", messageRetriever.getTag()), replacements);
    }

	public final void sendTo(CommandSender sender) {
		MessageRetriever messageRetriever = TheBuildingGame.getInstance().getMessageManager();
		String[] messages = messageRetriever.getMessage(this);
		
		for (String message : messages) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{TAG}", messageRetriever.getTag())));
		}
	}
	
	public final void sendTo(CommandSender sender, String... replacements) {
		MessageRetriever messageRetriever = TheBuildingGame.getInstance().getMessageManager();
		String[] messages = messageRetriever.getMessage(this);
		
		for (String message : messages) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(message.replace("{TAG}", messageRetriever.getTag()), (Object[]) replacements)));
		}
	}
	
}