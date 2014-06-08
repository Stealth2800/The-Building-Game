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
package com.stealthyone.mcb.thebuildinggame;

import com.stealthyone.mcb.stbukkitlib.messages.MessageManager;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.ArenaManager;
import com.stealthyone.mcb.thebuildinggame.backend.games.GameManager;
import com.stealthyone.mcb.thebuildinggame.backend.rooms.RoomManager;
import com.stealthyone.mcb.thebuildinggame.commands.CmdTheBuildingGame;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class TheBuildingGame extends JavaPlugin {

    private static TheBuildingGame instance;
    {
        instance = this;
    }

    public static TheBuildingGame getInstance() {
        return instance;
    }

    private MessageManager messageManager;

    private ArenaManager arenaManager;
    private GameManager gameManager;
    private RoomManager roomManager;

    @Override
    public final void onLoad() {
        getDataFolder().mkdir();
        new File(getDataFolder() + File.separator + "data").mkdir();
    }

    @Override
    public final void onEnable() {
        /* Setup config */
        saveDefaultConfig();
        getConfig().options().copyDefaults(false);
        saveConfig();

        messageManager = new MessageManager(this);

        arenaManager = new ArenaManager(this);
        gameManager = new GameManager(this);
        roomManager = new RoomManager(this);

        getLogger().info("Loaded " + arenaManager.reload() + " arenas.");

        getCommand("thebuildinggame").setExecutor(new CmdTheBuildingGame(this));

        //updateChecker = UpdateChecker.scheduleForMe(this, 68583);
        getLogger().info("TheBuildingGame v" + getDescription().getVersion() + " by Stealth2800 enabled!");
    }

    @Override
    public final void onDisable() {
        saveAll();
        getLogger().info("TheBuildingGame v" + getDescription().getVersion() + " by Stealth2800 disabled!");
    }

    public final void reloadAll() {
        try {
            roomManager.reload();
        } catch (InvalidConfigurationException ex) {
            getLogger().severe("An error occurred while reloading the plugin's configuration: " + ex.getMessage());
        }
    }

    public final void saveAll() {

    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

}