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
package com.stealthyone.mcb.thebuildinggame.listeners;

import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.backend.players.BgPlayer;
import com.stealthyone.mcb.thebuildinggame.backend.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private TheBuildingGame plugin;

    public PlayerListener(TheBuildingGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        BgPlayer playerCast = plugin.getGameBackend().getPlayerManager().castPlayer(e.getPlayer());
        try {
            playerCast.getCurrentGame().removePlayer(playerCast);
        } catch (NullPointerException ex) {}
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        BgPlayer playerCast = plugin.getGameBackend().getPlayerManager().castPlayer(e.getPlayer());
        try {
            playerCast.getCurrentGame().removePlayer(playerCast);
        } catch (NullPointerException ex) {}
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        plugin.getGameBackend().getSignManager().playerInteract(e);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                PlayerManager playerManager = plugin.getGameBackend().getPlayerManager();
                playerManager.loadPlayerData(playerManager.castPlayer(e.getPlayer()));
            }
        }, 20L);
    }

}