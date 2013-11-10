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
package com.stealthyone.mcb.thebuildinggame.backend.signs;

import com.stealthyone.mcb.stbukkitlib.lib.signs.BetterSign;
import com.stealthyone.mcb.stbukkitlib.lib.utils.SignUtils;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame.Log;
import com.stealthyone.mcb.thebuildinggame.backend.GameBackend;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.Arena;
import com.stealthyone.mcb.thebuildinggame.backend.games.GameInstance;
import com.stealthyone.mcb.thebuildinggame.backend.players.BgPlayer;
import com.stealthyone.mcb.thebuildinggame.messages.ErrorMessage;
import com.stealthyone.mcb.thebuildinggame.messages.NoticeMessage;
import com.stealthyone.mcb.thebuildinggame.permissions.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class GameSign {

    private ConfigurationSection config;

    public GameSign(ConfigurationSection config) {
        this.config = config;
    }

    public Location getLocation() {
        return new Location(
                Bukkit.getWorld(config.getParent().getParent().getParent().getName()),
                Integer.parseInt(config.getParent().getParent().getName()),
                Integer.parseInt(config.getName()),
                Integer.parseInt(config.getParent().getName())
        );
    }

    public Arena getArena() {
        return TheBuildingGame.getInstance().getGameBackend().getArenaManager().getArena(config.getInt("arena"));
    }

    public int getArenaId() {
        return config.getInt("arena");
    }

    public BetterSign getSign() {
        BetterSign returnSign = SignUtils.getBetterSign(getLocation().getBlock());
        //Check if null, create if it is
        return (returnSign == null) ? recreateSign() : returnSign;
    }

    public BetterSign recreateSign() {
        BetterSign sign;
        try {
            Material type = Material.valueOf(config.getString("block.type"));
            if (type != Material.SIGN_POST || type != Material.WALL_SIGN) {
                return null;
            }
            BlockFace facing = BlockFace.valueOf(config.getString("block.facing"));
            Block block = getLocation().getBlock();
            block.setType(type);
            sign = SignUtils.getBetterSign(block);

            sign.startEditing();
            sign.setFacing(facing);
            sign.endEditing();
        } catch (Exception ex) {
            sign = null;
        }
        return sign;
    }

    public void update() {
        Log.debug("update");
        BetterSign sign = getSign();
        sign.startEditing();
        Arena arena = getArena();
        if (arena == null) {
            Log.debug("arena null");
            Log.debug("sign being edited: " + sign.isBeingEdited());
            sign.setLine(0, ChatColor.RED + "ERROR");
            sign.setLine(1, ChatColor.RED + "Arena #" + config.getInt("arena"));
            sign.setLine(2, ChatColor.RED + "not found!");
        } else {
            Log.debug("arena not null");
            Log.debug("sign being edited: " + sign.isBeingEdited());
            //Arena #
            //State
            sign.setLine(0, "Arena " + arena.getId());
            GameInstance gameInstance = arena.getGameInstance();
            if (!arena.isEnabled()) {
                sign.setLine(1, "" + ChatColor.DARK_RED + ChatColor.ITALIC + "DISABLED");
            } else if (gameInstance != null) {
                sign.setLine(1, gameInstance.getState().getText());
                sign.setLine(2, gameInstance.getPlayerCount() + "/" + arena.getMaxPlayers());
            }
        }
        sign.endEditing();
    }

    public void playerInteract(PlayerInteractEvent e) {
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        GameBackend gameBackend = TheBuildingGame.getInstance().getGameBackend();
        Player player = e.getPlayer();
        BgPlayer playerCast = gameBackend.getPlayerManager().castPlayer(player);
        Arena arena = getArena();
        if (arena == null) {
            ErrorMessage.ARENA_DOES_NOT_EXIST.sendTo(player, Integer.toString(getArenaId()));
        } else if (!arena.isEnabled()) {
            NoticeMessage.ARENA_DISABLED.sendTo(player, Integer.toString(getArenaId()));
        } else if (!PermissionNode.GAME_PLAY.isAllowed(player)) {
            ErrorMessage.NO_PERMISSION.sendTo(player);
        } else {
            GameInstance gameInstance = arena.getGameInstance();
            if (!player.isSneaking()) {
                if (gameInstance.addPlayer(playerCast)) {
                    NoticeMessage.JOINED_GAME.sendTo(player);
                    //} else if (playerCast.getCurrentGame().equals(gameInstance)) {
                } else if (gameInstance.isPlayerJoined(playerCast)) {
                    ErrorMessage.ALREADY_IN_GAME.sendTo(player);
                    NoticeMessage.INFO_GAME_LEAVE.sendTo(player);
                } else {
                    ErrorMessage.ALREADY_IN_OTHER_GAME.sendTo(player);
                }
            } else if (gameInstance.removePlayer(playerCast)) {
                NoticeMessage.LEFT_GAME.sendTo(player);
            } else {
                ErrorMessage.NOT_IN_GAME.sendTo(player);
            }
        }
    }

}