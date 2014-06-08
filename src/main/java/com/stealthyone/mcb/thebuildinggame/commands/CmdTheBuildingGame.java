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
package com.stealthyone.mcb.thebuildinggame.commands;

import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.permissions.PermissionNode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdTheBuildingGame implements CommandExecutor {

    private TheBuildingGame plugin;

    private SCmdTBGArena cmdArena;

    public CmdTheBuildingGame(TheBuildingGame plugin) {
        this.plugin = plugin;
        cmdArena = new SCmdTBGArena(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "arena":
                    cmdArena.handleCommand(sender, command, label, args);
                    return true;

                case "reload":
                    cmdReload(sender, command, label, args);
                    return true;

                case "version":
                    cmdVersion(sender, command, label, args);
                    return true;
            }
        }
        return true;
    }

    /*
     * Reload command.
     */
    private void cmdReload(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.ADMIN_RELOAD.isAllowedAlert(sender)) return;

        sender.sendMessage(ChatColor.GOLD + "=====Reloading The Building Game=====");
        try {
            sender.sendMessage(ChatColor.GREEN + "Loaded " + ChatColor.GOLD + plugin.getArenaManager().reload() + ChatColor.GREEN + " arenas.");
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "An error occurred while reloading The Building Game: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "Reloaded The Building Game configuration from the disk.");
    }

    /*
     * Plugin version command.
     */
    private void cmdVersion(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "The Building Game" + ChatColor.DARK_GRAY + ChatColor.ITALIC + " v" + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.GOLD + "Plugin created by Stealth2800, original game created by SethBling");
        sender.sendMessage(ChatColor.BLUE + "Website: " + ChatColor.AQUA + ChatColor.UNDERLINE + "http://stealthyone.com/");
        sender.sendMessage(ChatColor.BLUE + "Original: " + ChatColor.AQUA + ChatColor.UNDERLINE + "http://sethbling.com/downloads/builds/thebuildinggame/");
    }

}