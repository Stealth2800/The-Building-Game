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
package com.stealthyone.mcb.thebuildinggame;

import com.stealthyone.mcb.stbukkitlib.lib.autosaving.Autosavable;
import com.stealthyone.mcb.stbukkitlib.lib.autosaving.AutosavingAPI;
import com.stealthyone.mcb.stbukkitlib.lib.help.HelpAPI;
import com.stealthyone.mcb.stbukkitlib.lib.help.HelpManager;
import com.stealthyone.mcb.stbukkitlib.lib.messages.MessageRetriever;
import com.stealthyone.mcb.thebuildinggame.backend.GameBackend;
import com.stealthyone.mcb.thebuildinggame.commands.CmdTheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.config.ConfigHelper;
import com.stealthyone.mcb.thebuildinggame.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class TheBuildingGame extends JavaPlugin implements Autosavable {

    public final static class Log {

        public final static void debug(String msg) {
            if (ConfigHelper.DEBUG.getBoolean())
                instance.logger.log(Level.INFO, String.format("[%s DEBUG] %s", instance.getName(), msg));
        }

        public final static void info(String msg) {
            instance.logger.log(Level.INFO, String.format("[%s] %s", instance.getName(), msg));
        }

        public final static void warning(String msg) {
            instance.logger.log(Level.WARNING, String.format("[%s] %s", instance.getName(), msg));
        }

        public final static void severe(String msg) {
            instance.logger.log(Level.SEVERE, String.format("[%s] %s", instance.getName(), msg));
        }

    }

    private static TheBuildingGame instance;
    {
        instance = this;
    }

    public final static TheBuildingGame getInstance() {
        return instance;
    }

    private Logger logger;

    private HelpManager helpManager;
    private MessageRetriever messageManager;

    private GameBackend gameBackend;

    @Override
    public final void onLoad() {
        logger = getServer().getLogger();
        getDataFolder().mkdir();
    }

    @Override
    public final void onEnable() {
        /* Setup config */
        saveDefaultConfig();
        getConfig().options().copyDefaults(false);
        saveConfig();

        /* Setup important plugin parts */
        helpManager = HelpAPI.registerHelp(this);
        messageManager = new MessageRetriever(this);

        gameBackend = new GameBackend(this);

        /* Register listeners */
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new BlockListener(this), this);
        pluginManager.registerEvents(new InventoryListener(this), this);
        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new SignListener(this), this);
        pluginManager.registerEvents(new WeatherListener(this), this);

        /* Register commands */
        getCommand("thebuildinggame").setExecutor(new CmdTheBuildingGame(this));

        AutosavingAPI.registerAutosavable(this, "main", this, ConfigHelper.AUTOSAVE_INTERVAL.getInt() * 60);
        Log.info("TheBuildingGame v" + getDescription().getVersion() + " by Stealth2800 enabled!");
    }

    @Override
    public final void onDisable() {
        saveAll();
        Log.info("TheBuildingGame v" + getDescription().getVersion() + " by Stealth2800 disabled!");
    }

    @Override
    public final void saveAll() {
        gameBackend.saveAll();
    }

    public HelpManager getHelpManager() {
        return helpManager;
    }

    public MessageRetriever getMessageManager() {
        return messageManager;
    }

    public final GameBackend getGameBackend() {
        return gameBackend;
    }

}