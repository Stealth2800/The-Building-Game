/*
 * The Building Game - Bukkit Plugin
 * Copyright (C) 2014 Stealth2800 <stealth2800@stealthyone.com>
 * Website: <http://stealthyone.com>
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

import com.stealthyone.mcb.stbukkitlib.messages.MessagePath;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import org.bukkit.command.CommandSender;

import java.util.Map;

public enum Messages implements MessagePath {

    ARENAS_LIST(Messages.CATEGORY_PLUGIN),
    ARENAS_LIST_ITEM(Messages.CATEGORY_PLUGIN),
    ARENAS_LIST_NONE(Messages.CATEGORY_PLUGIN),
    PAGE_NOTICE(Messages.CATEGORY_PLUGIN),

    NO_PERMISSION(Messages.CATEGORY_ERRORS),
    PAGE_MUST_BE_INT(Messages.CATEGORY_ERRORS);

    private final static String CATEGORY_ERRORS = "errors";
    private final static String CATEGORY_PLUGIN = "plugin";

    private String path;

    private Messages(String category) {
        path = category + "." + toString().toLowerCase();
    }

    @Override
    public String getPath() {
        return path;
    }

    public void sendTo(CommandSender sender) {
        TheBuildingGame.getInstance().getMessageManager().getMessage(this).sendTo(sender);
    }

    public void sendTo(CommandSender sender, Map<String, String> replacements) {
        TheBuildingGame.getInstance().getMessageManager().getMessage(this).sendTo(sender, replacements);
    }

    public String[] getFormattedMessages() {
        return TheBuildingGame.getInstance().getMessageManager().getMessage(this).getFormattedMessages();
    }

    public String[] getFormattedMessages(Map<String, String> replacements) {
        return TheBuildingGame.getInstance().getMessageManager().getMessage(this).getFormattedMessages(replacements);
    }

}