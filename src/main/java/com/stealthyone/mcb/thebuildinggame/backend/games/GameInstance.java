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
package com.stealthyone.mcb.thebuildinggame.backend.games;

import com.stealthyone.mcb.stbukkitlib.lib.utils.TimeUtils;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame.Log;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.Arena;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.rooms.Room;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.rooms.RoomManager;
import com.stealthyone.mcb.thebuildinggame.backend.games.rounds.*;
import com.stealthyone.mcb.thebuildinggame.backend.players.BgPlayer;
import com.stealthyone.mcb.thebuildinggame.backend.players.PlayerManager;
import com.stealthyone.mcb.thebuildinggame.messages.NoticeMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GameInstance {

    private Arena arena;
    private GameState state;

    private Map<String, BgPlayer> players = new HashMap<String, BgPlayer>();
    private Map<Integer, BgPlayer> playerIds = new HashMap<Integer, BgPlayer>();

    private int roundTime = -2, currentRound = -1;
    private Map<Integer, Round> rounds = new HashMap<Integer, Round>();
    private Map<Round, List<Room>> rooms = new HashMap<Round, List<Room>>();

    private Scoreboard scoreboard;
    private Objective objective;
    private Score time;

    public GameInstance(Arena arena) {
        this.arena = arena;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(TheBuildingGame.getInstance(), new Runnable() {
            @Override
            public void run() {
                gameTick();
            }
        }, 20L, 20L);

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("game", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("" + ChatColor.GOLD + ChatColor.BOLD + "The Building Game");
        time = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Time"));
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof GameInstance && ((GameInstance) object).arena.equals(arena);
    }

    public void setupScoreboard() {
        if (state == GameState.STARTING) {
            for (BgPlayer player : players.values()) {
                Player rawPlayer = player.getPlayer();
                Score score = objective.getScore(rawPlayer);
                score.setScore(0);
            }
            for (BgPlayer player : players.values()) {
                player.getPlayer().setScoreboard(scoreboard);
            }
        }
    }

    public Score getScore(BgPlayer player) {
        if (!isPlayerJoined(player)) {
            return null;
        } else {
            return objective.getScore(player.getPlayer());
        }
    }

    public void resetScores() {
        for (BgPlayer player : players.values()) {
            getScore(player).setScore(0);
        }
    }

    public void gameTick() {
        if (state != GameState.INACTIVE) {
            if (roundTime >= 0) {
                if (state == GameState.IN_PROGRESS) {
                    Round curRound = getCurrentRound();
                    if (!((curRound instanceof RoundResults && arena.timeResultsRound()) || curRound instanceof RoundBuild)) roundTime++;
                }
                roundTime--;
                time.setScore(roundTime);
            }

            if (state == GameState.WAITING) {
                if (getPlayerCount() == arena.getMaxPlayers()) {
                    //start game
                    if (roundTime == -2) roundTime = 15;

                    if (roundTime == 30 || roundTime == 15 || roundTime >= 0 && roundTime <= 10)
                        sendMessage(NoticeMessage.GAME_TIME_START_NOTICE, TimeUtils.translateSeconds(roundTime));

                    if (roundTime == -1)
                        startGame();
                } else if (roundTime != -2) {
                    roundTime = -2;
                    sendMessage(NoticeMessage.GAME_STARTING_CANCELLED, "Player quit");
                }
            } else if (state == GameState.IN_PROGRESS) {
                Round currentRound = getCurrentRound();
                if (currentRound instanceof RoundGuess || currentRound instanceof RoundThink) {
                    if ((currentRound instanceof RoundGuess && ((RoundGuess) currentRound).hasEveryoneGuessed()) || (currentRound instanceof RoundThink && ((RoundThink) currentRound).hasEveryoneEnteredIdea()))
                        endCurrentRound();
                } else {
                    if (currentRound instanceof RoundBuild && ((RoundBuild) currentRound).allPlayersComplete())
                        endCurrentRound();

                    int maxRoundTime = arena.getRoundTime();
                    if (roundTime == maxRoundTime / 2 || roundTime == 30 || roundTime == 15 || roundTime >= 0 && roundTime <= 10)
                        sendMessage(NoticeMessage.GAME_TIME_NOTICE, TimeUtils.translateSeconds(roundTime));

                    if (roundTime == -1) endCurrentRound();
                }
            }
        }
    }

    public Arena getArena() {
        return arena;
    }

    public boolean isPlayerJoined(BgPlayer player) {
        return players.containsKey(player.getName().toLowerCase());
    }

    public BgPlayer getPlayerById(int id) {
        return playerIds.get(id);
    }

    public Map<Integer, BgPlayer> getPlayerIds() {
        return playerIds;
    }

    public int getIdByPlayer(BgPlayer player) {
        if (isPlayerJoined(player)) {
            for (Entry<Integer, BgPlayer> entry : playerIds.entrySet()) {
                if (entry.getValue().equals(player))
                    return entry.getKey();
            }
        }
        return -1;
    }

    public boolean addPlayer(BgPlayer player) {
        if (!player.isInGame() && !isPlayerJoined(player)) {
            players.put(player.getName().toLowerCase(), player);
            arena.updateSigns();

            int playerCount = getPlayerCount();
            int maxPlayerCount = arena.getMaxPlayers();
            sendMessage(NoticeMessage.GAME_PLAYER_NOTICE, Integer.toString(playerCount), Integer.toString(maxPlayerCount));
            TheBuildingGame.getInstance().getGameBackend().getPlayerManager().reindexPlayerArenas();
            player.setCurrentGame(this);
            return true;
        } else {
            return false;
        }
    }

    public boolean removePlayer(BgPlayer player) {
        if (isPlayerJoined(player)) {
            players.remove(player.getName().toLowerCase());
            arena.updateSigns();
            PlayerManager playerManager = TheBuildingGame.getInstance().getGameBackend().getPlayerManager();
            playerManager.reindexPlayerArenas();
            playerManager.loadPlayerData(player);
            player.setCurrentGame(null);
            if (state == GameState.IN_PROGRESS) {
                sendMessage(NoticeMessage.GAME_ENDED_PLAYER_QUIT);
                endGame();
            }
            return true;
        } else {
            return false;
        }
    }

    public void startGame() {
        if (state == GameState.WAITING) {
            setState(GameState.STARTING);
            preparePlayers();
            setupRounds();
            currentRound = 1;
            Round round = getRound(currentRound);
            round.teleportPlayers();
            round.sendStartingMessage();
            setupScoreboard();
            setState(GameState.IN_PROGRESS);
            roundTime = arena.getRoundTime();
        }
    }

    private void preparePlayers() {
        PlayerManager playerManager = TheBuildingGame.getInstance().getGameBackend().getPlayerManager();
        int i = 1;
        for (BgPlayer player : players.values()) {
            playerManager.savePlayerData(player);
            player.getPlayer().getInventory().clear();
            player.getPlayer().setGameMode(GameMode.CREATIVE);
            playerIds.put(i, player);
            i++;
        }
    }

    private void setupRounds() {
        if (state == GameState.STARTING) {
            Log.debug("setting up rounds");
            int playerCount = getPlayerCount();
            RoomManager roomManager = TheBuildingGame.getInstance().getGameBackend().getRoomManager();
            for (int i = 1; i <= playerCount + 1; i++) {
                Log.debug("i: " + i);
                if (i == 1) {
                    Log.debug("added think round");
                    //think
                    Round round = new RoundThink(this, i);
                    rounds.put(i, round);
                    rooms.put(round, new ArrayList<Room>(roomManager.getNextRooms(playerCount, true)));
                    round.allocateRooms();
                } else if (i > playerCount) {
                    Log.debug("added results round");
                    //results
                    Round round = new RoundResults(this, i);
                    rounds.put(i, round);
                    rooms.put(round, rooms.get(getRound(1)));
                    round.allocateRooms();
                } else if (i % 2 == 0) {
                    Log.debug("added build round");
                    //build
                    Round round = new RoundBuild(this, i);
                    rounds.put(i, round);
                    if (i != 2) {
                        rooms.put(round, new ArrayList<Room>(roomManager.getNextRooms(playerCount, true)));
                    } else {
                        rooms.put(round, rooms.get(getRound(i - 1)));
                    }
                    round.allocateRooms();
                } else {
                    Log.debug("added guess round");
                    //guess
                    Round round = new RoundGuess(this, i);
                    rounds.put(i, round);
                    rooms.put(round, rooms.get(getRound(i - 1)));
                    round.allocateRooms();
                }
            }
        }
    }

    public void endGame() {
        if (state == GameState.IN_PROGRESS) {
            setState(GameState.ENDING);
            clearPlayers();
            for (Round round : rounds.values()) {
                round.cleanup();
            }
            rounds.clear();
            rooms.clear();
            setState(GameState.WAITING);
        }
    }

    private void clearPlayers() {
        PlayerManager playerManager = TheBuildingGame.getInstance().getGameBackend().getPlayerManager();
        for (BgPlayer player : players.values()) {
            Log.debug("Clear player: " + player.getName());
            player.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            player.setCurrentGame(null);
            playerManager.loadPlayerData(player);
        }
        players.clear();
        playerIds.clear();
    }

    public Round getRound(int roundNum) {
        return rounds.get(roundNum);
    }

    public Round getCurrentRound() {
        return currentRound == -1 ? null : rounds.get(currentRound);
    }

    public List<Room> getRooms(Round round) {
        return rooms.get(round);
    }

    public void endCurrentRound() {
        Round round = getCurrentRound();
        if (round != null) {
            round.endRound();
            if (round instanceof RoundResults) {
                sendMessage(NoticeMessage.GAME_OVER);
                endGame();
            } else {
                currentRound++;
                Round newRound = getCurrentRound();
                newRound.teleportPlayers();
                newRound.sendStartingMessage();
                newRound.startRound();
                roundTime = arena.getRoundTime();
                resetScores();
                if (!(newRound instanceof RoundResults || newRound instanceof RoundBuild)) time.setScore(0);
            }
        }
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        if (state != this.state) {
            this.state = state;
            arena.updateSigns();
        }
    }

    public int getPlayerCount() {
        return players.size();
    }

    public void sendMessage(NoticeMessage message) {
        for (BgPlayer player : players.values()) {
            message.sendTo(player.getPlayer());
        }
    }

    public void sendMessage(NoticeMessage message, String... replacements) {
        for (BgPlayer player : players.values()) {
            message.sendTo(player.getPlayer(), replacements);
        }
    }

}