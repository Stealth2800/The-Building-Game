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
import com.stealthyone.mcb.stbukkitlib.lib.signs.BetterWallSign;
import com.stealthyone.mcb.stbukkitlib.lib.storage.YamlFileManager;
import com.stealthyone.mcb.stbukkitlib.lib.utils.ConfigUtils;
import com.stealthyone.mcb.stbukkitlib.lib.utils.SignUtils;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame.Log;
import com.stealthyone.mcb.thebuildinggame.backend.GameBackend;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.Arena;
import com.stealthyone.mcb.thebuildinggame.messages.ErrorMessage;
import com.stealthyone.mcb.thebuildinggame.messages.NoticeMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.util.*;

public class SignManager {

    private TheBuildingGame plugin;
    private GameBackend gameBackend;

    private YamlFileManager signFile;

    private Map<String, GameSign> loadedSigns = new HashMap<String, GameSign>();
    private Map<Arena, List<GameSign>> arenaSignIndex = new HashMap<Arena, List<GameSign>>();

    public SignManager(TheBuildingGame plugin, GameBackend gameBackend) {
        this.plugin = plugin;
        this.gameBackend = gameBackend;
        signFile = new YamlFileManager(plugin.getDataFolder() + File.separator + "signs.yml");
        Log.info("Loaded " + reloadSigns(false) + " signs.");
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                reindexArenaSigns();
                updateAllSigns();
            }
        }, 20L);
    }

    public void save() {
        signFile.saveFile();
    }

    public int reloadSigns(boolean reindexSigns) {
        loadedSigns.clear();
        FileConfiguration signConfig = signFile.getConfig();
        for (String worldName : signConfig.getKeys(false)) {
            for (String x : signConfig.getConfigurationSection(worldName).getKeys(false)) {
                for (String z : signConfig.getConfigurationSection(worldName + "." + x).getKeys(false)) {
                    for (String y : signConfig.getConfigurationSection(worldName + "." + x + "." + z).getKeys(false)) {
                        ConfigurationSection finalConfig = signConfig.getConfigurationSection(worldName + "." + x + "." + z + "." + y);
                        loadSign(finalConfig, false);
                    }
                }
            }
        }
        if (reindexSigns) reindexArenaSigns();
        return loadedSigns.size();
    }

    public void loadSign(ConfigurationSection config, boolean reindexSigns) {
        GameSign sign = new GameSign(config);
        loadedSigns.put(ConfigUtils.locationToString(sign.getLocation()), sign);
        if (reindexSigns) reindexArenaSigns();
    }

    public void deleteSign(GameSign sign, boolean reindexSigns) {
        loadedSigns.remove(ConfigUtils.locationToString(sign.getLocation()));
        if (reindexSigns) reindexArenaSigns();
    }

    public void reindexArenaSigns() {
        arenaSignIndex.clear();
        int signCount = 0;
        Collection<Arena> arenas = gameBackend.getArenaManager().getArenas().values();
        for (Arena arena : arenas) {
            List<GameSign> signs = new ArrayList<GameSign>();
            for (GameSign sign : loadedSigns.values()) {
                if (sign.getArena().equals(arena)) signs.add(sign);
            }
            arenaSignIndex.put(arena, signs);
            signCount += signs.size();
        }
        Log.debug("Indexed " + signCount + " signs for " + arenas.size() + " arenas.");
    }

    public List<GameSign> getSigns(Arena arena) {
        return arenaSignIndex.get(arena);
    }

    public void updateAllSigns() {
        for (GameSign sign : loadedSigns.values()) {
            sign.update();
        }
    }

    public void updateSigns(Arena arena) {
        Log.debug("updateSigns for arena: " + (arena == null ? "null" : arena.getId()));
        List<GameSign> arenaSigns = getSigns(arena);
        if (arenaSigns != null) {
            for (GameSign sign : arenaSigns) {
                sign.update();
            }
        }
    }

    public GameSign getSign(Location location) {
        return loadedSigns.get(ConfigUtils.locationToString(location));
    }

    public void signCreated(SignChangeEvent e) {
        Player player = e.getPlayer();
        String[] lines = e.getLines();
        if (lines[0].equalsIgnoreCase("[BGArena]")) {
            int arenaId;
            try {
                arenaId = Integer.parseInt(lines[1]);
            } catch (NumberFormatException ex) {
                ErrorMessage.MUST_BE_INT.sendTo(player, "Arena ID");
                e.setCancelled(true);
                e.getBlock().breakNaturally();
                return;
            }
            Arena arena = plugin.getGameBackend().getArenaManager().getArena(arenaId);
            if (arena == null) {
                ErrorMessage.ARENA_DOES_NOT_EXIST.sendTo(player, Integer.toString(arenaId));
                e.setCancelled(true);
                e.getBlock().breakNaturally();
            } else {
                BetterSign betterSign = SignUtils.getBetterSign(e.getBlock());
                final Location signLoc = betterSign.getLocation();
                String path = signLoc.getWorld().getName().toLowerCase() + "." + signLoc.getBlockX() + "." + signLoc.getBlockZ() + "." + signLoc.getBlockY();
                ConfigurationSection config = signFile.getConfig().createSection(path);
                config.set("arena", arenaId);
                String type;
                if (betterSign instanceof BetterWallSign) {
                    type = Material.WALL_SIGN.toString();
                } else {
                    type = Material.SIGN_POST.toString();
                }
                config.set("block.type", type);
                config.set("block.facing", betterSign.getFacing().toString());
                loadSign(config, true);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        getSign(signLoc).update();
                    }
                }, 20L);
                NoticeMessage.SIGN_CREATED.sendTo(player);
                reindexArenaSigns();
            }
        }
    }

    public void signDestroyed(BlockBreakEvent e) {
        GameSign sign = getSign(e.getBlock().getLocation());
        if (sign != null) {
            Player player = e.getPlayer();
            if (player == null) {
                e.setCancelled(true);
            } else if (!player.isSneaking()) {
                e.setCancelled(true);
            } else {
                int arenaId = sign.getArenaId();
                deleteSign(sign, true);
                NoticeMessage.SIGN_DESTROYED.sendTo(player, Integer.toString(arenaId));
            }
        }
    }

    public void playerInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block != null && SignUtils.isBlockSign(block)) {
            GameSign sign = getSign(block.getLocation());
            if (sign != null) sign.playerInteract(e);
        }
    }

}