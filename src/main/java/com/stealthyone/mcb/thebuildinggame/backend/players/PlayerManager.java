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
package com.stealthyone.mcb.thebuildinggame.backend.players;

import com.stealthyone.mcb.stbukkitlib.lib.storage.InventoryIO;
import com.stealthyone.mcb.stbukkitlib.lib.storage.YamlFileManager;
import com.stealthyone.mcb.stbukkitlib.lib.utils.ConfigUtils;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame.Log;
import com.stealthyone.mcb.thebuildinggame.backend.GameBackend;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.Arena;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {

    private TheBuildingGame plugin;
    private GameBackend gameBackend;

    private YamlFileManager tempPlayerDataFile;
    private Map<String, BgPlayer> players = new HashMap<String, BgPlayer>();
    private Map<BgPlayer, Arena> playerArenaIndex = new HashMap<BgPlayer, Arena>();

    public PlayerManager(TheBuildingGame plugin, GameBackend gameBackend) {
        this.plugin = plugin;
        this.gameBackend = gameBackend;
        tempPlayerDataFile = new YamlFileManager(plugin.getDataFolder() + File.separator + "tempPlayerData.yml");
    }

    public void save() {
        tempPlayerDataFile.saveFile();
    }

    public boolean isPlayerLoaded(Player player) {
        return players.containsKey(player.getName().toLowerCase());
    }

    public void unloadPlayer(OfflinePlayer player) {
        String playerName = player.getName().toLowerCase();
        if (players.containsKey(playerName)) {
            players.remove(playerName);
        }
    }

    public BgPlayer castPlayer(Player player) {
        String playerName = player.getName().toLowerCase();
        Log.debug("castPlayer for " + playerName);
        BgPlayer returnPlayer = players.get(playerName);
        if (returnPlayer == null) {
            Log.debug("loaded players: " + players.keySet().toString());
            Log.debug("returnPlayer is null, creating new");
            returnPlayer = new BgPlayer(player);
            Log.debug("loading player");
            players.put(playerName, returnPlayer);
            Log.debug("players after: " + players.keySet().toString());
        }
        return returnPlayer;
    }

    public void savePlayerData(BgPlayer player) {
        String playerName = player.getName().toLowerCase();
        ConfigurationSection config = tempPlayerDataFile.getConfig().getConfigurationSection(playerName);
        if (config == null) config = tempPlayerDataFile.getConfig().createSection(playerName);
        Player rawPlayer = player.getPlayer();
        PlayerInventory inv = rawPlayer.getInventory();
        config.set("inventory", inv.getContents());
        config.set("armor", inv.getArmorContents());
        config.set("prevLoc", ConfigUtils.locationToString(rawPlayer.getLocation()));
        config.set("exp", rawPlayer.getExp());
        config.set("level", rawPlayer.getLevel());
        config.set("exhaustion", rawPlayer.getExhaustion());
        config.set("gamemode", rawPlayer.getGameMode().toString());
    }

    public void loadPlayerData(BgPlayer player) {
        ConfigurationSection config = tempPlayerDataFile.getConfig().getConfigurationSection(player.getName().toLowerCase());
        if (config == null) return;

        Player rawPlayer = player.getPlayer();

        rawPlayer.teleport(ConfigUtils.getLocation(config.getString("prevLoc")), TeleportCause.PLUGIN);

        PlayerInventory inv = rawPlayer.getInventory();
        inv.clear();
        List<?> rawItems = config.getList("inventory");
        List<?> rawArmor = config.getList("armor");
        if (rawItems != null) {
            Log.debug("rawItems not null");
            List<ItemStack> invItems = InventoryIO.getItemstackList(rawItems);
            inv.setContents(invItems.toArray(new ItemStack[invItems.size()]));
        }
        if (rawArmor != null) {
            Log.debug("rawArmor not null");
            List<ItemStack> armorItems = InventoryIO.getItemstackList(rawArmor);
            inv.setArmorContents(armorItems.toArray(new ItemStack[armorItems.size()]));
        }

        rawPlayer.setExp((float) config.getDouble("exp"));
        rawPlayer.setLevel(config.getInt("level"));
        rawPlayer.setExhaustion((float) config.getDouble("exhaustion"));
        rawPlayer.setGameMode(GameMode.valueOf(config.getString("gamemode")));

        tempPlayerDataFile.getConfig().set(config.getCurrentPath(), null);
    }

    public void reindexPlayerArenas() {
        playerArenaIndex.clear();
        for (BgPlayer player : players.values()) {
            if (!player.isOnline()) {
                unloadPlayer(player.getOfflinePlayer());
            } else {
                try {
                    playerArenaIndex.put(player, player.getCurrentGame().getArena());
                } catch (NullPointerException ex) {
                    playerArenaIndex.put(player, null);
                }
            }
        }
    }

}