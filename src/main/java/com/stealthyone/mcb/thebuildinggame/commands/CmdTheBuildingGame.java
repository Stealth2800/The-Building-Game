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
package com.stealthyone.mcb.thebuildinggame.commands;

import com.stealthyone.mcb.stbukkitlib.lib.updates.UpdateChecker;
import com.stealthyone.mcb.stbukkitlib.lib.utils.ArrayUtils;
import com.stealthyone.mcb.stbukkitlib.lib.utils.TimeUtils;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame.Log;
import com.stealthyone.mcb.thebuildinggame.backend.GameBackend;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.Arena;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.exceptions.InvalidArenaException;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.exceptions.PlayersInArenaException;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.rooms.Room;
import com.stealthyone.mcb.thebuildinggame.backend.games.GameInstance;
import com.stealthyone.mcb.thebuildinggame.backend.games.GameState;
import com.stealthyone.mcb.thebuildinggame.backend.games.rounds.*;
import com.stealthyone.mcb.thebuildinggame.backend.players.BgPlayer;
import com.stealthyone.mcb.thebuildinggame.backend.players.PlayerManager;
import com.stealthyone.mcb.thebuildinggame.messages.ErrorMessage;
import com.stealthyone.mcb.thebuildinggame.messages.NoticeMessage;
import com.stealthyone.mcb.thebuildinggame.messages.UsageMessage;
import com.stealthyone.mcb.thebuildinggame.permissions.PermissionNode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map.Entry;

public class CmdTheBuildingGame implements CommandExecutor {

    private TheBuildingGame plugin;

    public CmdTheBuildingGame(TheBuildingGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                /* Base arena command */
                case "arena":
                    cmdArena(sender, command, label, args);
                    return true;

                /* Base debug command */
                case "debug":
                    cmdDebug(sender, command, label, args);
                    return true;

                /* Base help command */
                case "help":
                    plugin.getHelpManager().handleHelpCommand(sender, null, label, args, 1);
                    break;

                /* Base game commands */
                case "game":
                    cmdGame(sender, command, label, args);
                    return true;

                /* Reload plugin config command */
                case "reload":
                    cmdReload(sender, command, label, args);
                    return true;

                /* Save plugin files command */
                case "save":
                    cmdSave(sender, command, label, args);
                    return true;

                /* Plugin version command */
                case "version":
                    cmdVersion(sender, command, label, args);
                    return true;

                default:
                    ErrorMessage.UNKNOWN_COMMAND.sendTo(sender, label);
                    break;
            }
        }
        plugin.getHelpManager().handleHelpCommand(sender, null, label, args, 1);
        return true;
    }

    /**
     * Handler for arena commands
     * @param sender
     * @param command
     * @param label
     * @param args
     */
    private void cmdArena(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 1) {
            switch (args[1]) {
                /* Create arena */
                case "create":
                    cmdArena_Create(sender, command, label, args);
                    return;

                /* Disable arena */
                case "disable":
                    cmdArena_Disable(sender, command, label, args);
                    return;

                /* Enable arena */
                case "enable":
                    cmdArena_Enable(sender, command, label, args);
                    return;

                /* Arena info */
                case "info":
                    cmdArena_Info(sender, command, label, args);
                    return;

                /* List arenas */
                case "list":
                    cmdArena_List(sender, command, label, args);
                    return;

                /* Modify arena */
                case "modify":
                    cmdArena_Modify(sender, command, label, args);
                    return;

                /* Arena help */
                case "help":
                    plugin.getHelpManager().handleHelpCommand(sender, "arenas", label, args, 2);
                    return;

                default:
                    ErrorMessage.UNKNOWN_COMMAND.sendTo(sender, label + " arena");
                    break;
            }
        }
        plugin.getHelpManager().handleHelpCommand(sender, "arenas", label, args, 1);
    }

    /*
     * Arenas - disable arena
     */
    private void cmdArena_Disable(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.ADMIN_ARENA_MODIFY.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (args.length < 3) {
            UsageMessage.ARENA_DISABLE.sendTo(sender, label);
        } else {
            int arenaId;
            try {
                arenaId = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                ErrorMessage.MUST_BE_INT.sendTo(sender, "Arena ID");
                return;
            }
            Arena arena = plugin.getGameBackend().getArenaManager().getArena(arenaId);
            if (arena == null) {
                ErrorMessage.ARENA_DOES_NOT_EXIST.sendTo(sender, Integer.toString(arenaId));
            } else {
                try {
                    if (arena.setEnabled(false)) {
                        NoticeMessage.ARENA_DISABLED.sendTo(sender, Integer.toString(arenaId));
                    } else {
                        ErrorMessage.ARENA_ALREADY_DISABLED.sendTo(sender, Integer.toString(arenaId));
                    }
                } catch (PlayersInArenaException ex) {
                    ErrorMessage.PLAYERS_IN_ARENA.sendTo(sender);
                }
            }
        }
    }

    /*
     * Arenas - enable arena
     */
    private void cmdArena_Enable(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.ADMIN_ARENA_MODIFY.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (args.length < 3) {
            UsageMessage.ARENA_ENABLE.sendTo(sender, label);
        } else {
            int arenaId;
            try {
                arenaId = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                ErrorMessage.MUST_BE_INT.sendTo(sender, "Arena ID");
                return;
            }
            Arena arena = plugin.getGameBackend().getArenaManager().getArena(arenaId);
            if (arena == null) {
                ErrorMessage.ARENA_DOES_NOT_EXIST.sendTo(sender, Integer.toString(arenaId));
            } else {
                try {
                    if (arena.setEnabled(true)) {
                        NoticeMessage.ARENA_ENABLED.sendTo(sender, Integer.toString(arenaId));
                    } else {
                        ErrorMessage.ARENA_ALREADY_ENABLED.sendTo(sender, Integer.toString(arenaId));
                    }
                } catch (InvalidArenaException ex) {
                    ErrorMessage.ARENA_INVALID_CONFIGURATION.sendTo(sender, Integer.toString(arenaId), Integer.toString(arenaId));
                }
            }
        }
    }

    /*
     * Arenas - create arena
     */
    private void cmdArena_Create(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.ADMIN_ARENA_CREATE.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else {
            Arena arena = plugin.getGameBackend().getArenaManager().createArena();
            NoticeMessage.CREATED_ARENA.sendTo(sender, Integer.toString(arena.getId()));
        }
    }

    /*
     * Arenas - info command
     */
    private void cmdArena_Info(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.ARENAS_INFO.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (args.length < 3) {
            UsageMessage.ARENA_INFO.sendTo(sender, label);
        } else {
            int id;
            try {
                id = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                ErrorMessage.MUST_BE_INT.sendTo(sender, "Arena ID");
                return;
            }

            GameBackend gameBackend = plugin.getGameBackend();
            Arena arena = gameBackend.getArenaManager().getArena(id);
            if (arena == null) {
                ErrorMessage.ARENA_DOES_NOT_EXIST.sendTo(sender, Integer.toString(id));
            } else {
                GameInstance gameInstance = arena.getGameInstance();
                String nickname = ChatColor.GOLD + "Nickname: " + (arena.getNickname() != null ? arena.getNickname() : "" + ChatColor.RED + ChatColor.ITALIC + "Not set");
                String enabled = ChatColor.GOLD + "Enabled: " + (arena.isEnabled() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No");
                GameState gameStateRaw = gameInstance.getState();
                String gameState = ChatColor.GOLD + "Game state: " + gameStateRaw.getText();
                int maxPlayersRaw = arena.getMaxPlayers();
                String maxPlayers = ChatColor.GOLD + "Max players: " + ChatColor.YELLOW + maxPlayersRaw;
                boolean chatEnabledRaw = arena.isChatEnabled();
                String chatEnabled = ChatColor.GOLD + "Chat enabled: " + (chatEnabledRaw ? ChatColor.GREEN + "True" : ChatColor.RED + "False");
                int roundTimeSec = arena.getRoundTime();
                String roundTime = ChatColor.GOLD + "Round time: " + ChatColor.YELLOW + TimeUtils.translateSeconds(roundTimeSec) + ChatColor.DARK_GRAY + " (" + roundTimeSec + " seconds)";
                boolean timeResultsRoundRaw = arena.timeResultsRound();
                String timeResultsRound = ChatColor.GOLD + "Time results round: " + (timeResultsRoundRaw ? ChatColor.GREEN : ChatColor.RED) + timeResultsRoundRaw;
                int signCountNum;
                try {
                    signCountNum = gameBackend.getSignManager().getSigns(arena).size();
                } catch (NullPointerException ex) {
                    signCountNum = -1;
                }
                String signCount = ChatColor.GOLD + "Sign count: " + (signCountNum == -1 ? "" + ChatColor.RED + ChatColor.ITALIC + "No signs" : signCountNum);

                sender.sendMessage(ChatColor.DARK_GRAY + "=====" + ChatColor.GREEN + "The Building Game" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + "Arena " + id + ChatColor.DARK_GRAY + "=====");
                sender.sendMessage(nickname);
                sender.sendMessage(enabled);
                sender.sendMessage(maxPlayers);
                sender.sendMessage(roundTime);
                sender.sendMessage(timeResultsRound);
                sender.sendMessage(chatEnabled);
                sender.sendMessage(signCount);
                sender.sendMessage(gameState);
                if (gameStateRaw != GameState.INACTIVE)
                    sender.sendMessage(ChatColor.GOLD + "Players: " + ChatColor.YELLOW + gameInstance.getPlayerCount() + "/" + maxPlayersRaw);
            }
        }
    }

    /*
     * Arenas - list arenas
     */
    private void cmdArena_List(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.ARENAS_LIST.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else {
            StringBuilder arenaList = new StringBuilder();
            for (Arena arena : plugin.getGameBackend().getArenaManager().getArenas().values()) {
                if (arenaList.length() > 0) arenaList.append(", ");
                arenaList.append(arena.isEnabled() ? ChatColor.GREEN : ChatColor.RED).append(arena.getId());
            }
            if (arenaList.length() == 0) arenaList.append("" + ChatColor.RED + ChatColor.ITALIC + "No arenas have been created");
            sender.sendMessage(ChatColor.DARK_GRAY + "=====" + ChatColor.GREEN + "The Building Game" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + "Arenas" + ChatColor.DARK_GRAY + "=====");
            sender.sendMessage(arenaList.toString());

        }
    }

    /*
     * Arenas - modify arena
     */
    private void cmdArena_Modify(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.ADMIN_ARENA_MODIFY.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (args.length >= 5) {
            int arenaId;
            try {
                arenaId = Integer.valueOf(args[2]);
            } catch (NumberFormatException ex) {
                if (args[2].equalsIgnoreCase("help")) {
                    sender.sendMessage("-help-");
                } else {
                    ErrorMessage.MUST_BE_INT.sendTo(sender, "Arena ID");
                }
                return;
            }

            Arena arena = plugin.getGameBackend().getArenaManager().getArena(arenaId);
            if (arena == null) {
                ErrorMessage.ARENA_DOES_NOT_EXIST.sendTo(sender);
                return;
            } else if (arena.isEnabled()) {
                ErrorMessage.ARENA_MUST_BE_DISABLED.sendTo(sender);
                return;
            }

            String option = args[3];
            String value = ArrayUtils.stringArrayToString(args, 4);

            switch (option) {
                case "maxplayers":
                    int newCount;
                    try {
                        newCount = Integer.parseInt(value);
                    } catch (NumberFormatException ex) {
                        ErrorMessage.MUST_BE_INT.sendTo(sender, "Max player count");
                        return;
                    }
                    if (!arena.setMaxPlayers(newCount)) {
                        ErrorMessage.INVALID_PLAYER_COUNT.sendTo(sender);
                    } else {
                        NoticeMessage.ARENA_SET_MAXPLAYERS.sendTo(sender, Integer.toString(arenaId), Integer.toString(newCount));
                    }
                    return;

                case "nickname":
                    if (!arena.setNickname(value)) {
                        ErrorMessage.UNABLE_TO_SET_NICKNAME.sendTo(sender, Integer.toString(arenaId));
                    } else {
                        NoticeMessage.ARENA_SET_NICKNAME.sendTo(sender, Integer.toString(arenaId), value);
                    }
                    return;

                case "roundtime":
                    int newTime;
                    try {
                        newTime = Integer.parseInt(value);
                    } catch (NumberFormatException ex) {
                        ErrorMessage.MUST_BE_INT.sendTo(sender, "Max player count");
                        return;
                    }
                    if (newTime < 30) {
                        ErrorMessage.INVALID_ROUND_TIME.sendTo(sender);
                    } else {
                        arena.setRoundTime(newTime);
                        NoticeMessage.ARENA_SET_ROUNDTIME.sendTo(sender, Integer.toString(arenaId), TimeUtils.translateSeconds(newTime));
                    }
                    return;

                default:
                    ErrorMessage.UNKNOWN_ARENA_SETTING.sendTo(sender, option);
                    break;
            }
        }
        plugin.getHelpManager().handleHelpCommand(sender, "arenas.modification", label, args, 2);
    }

    /**
     * Handler for debug commands
     * @param sender
     * @param command
     * @param label
     * @param args
     */
    private void cmdDebug(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.DEV_DEBUG.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (args.length > 1) {
            switch (args[1]) {
                /* Generate room */
                case "genroom":
                    cmdDebug_Genroom(sender, command, label, args);
                    return;

                /* Load inventory */
                case "loadinv":
                    cmdDebug_Loadinv(sender, command, label, args);
                    return;

                /* Save inventory */
                case "saveinv":
                    cmdDebug_Saveinv(sender, command, label, args);
                    return;

                case "help":
                    plugin.getHelpManager().handleHelpCommand(sender, "debug", label, args, 2);
                    return;

                default:
                    ErrorMessage.UNKNOWN_COMMAND.sendTo(sender, label + " debug");
                    break;
            }
            plugin.getHelpManager().handleHelpCommand(sender, "debug", label, args, 1);
        } else {
            plugin.getHelpManager().handleHelpCommand(sender, "debug", label, args, 1);
        }
    }

    /*
     * Debug - Generate room
     */
    private void cmdDebug_Genroom(CommandSender sender, Command command, String label, String[] args) {
        int amount = 1;
        if (args.length > 2) {
            try {
                amount = Integer.valueOf(args[2]);
            } catch (NumberFormatException ex) {
                ErrorMessage.MUST_BE_INT.sendTo(sender, "amount");
                return;
            }
        }
        plugin.getGameBackend().getRoomManager().getNextRooms(amount);
        sender.sendMessage("Created " + amount + " room(s)");
    }

    /*
     * Debug - Load inventory
     */
    private void cmdDebug_Loadinv(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ErrorMessage.MUST_BE_PLAYER.sendTo(sender);
        } else {
            PlayerManager playerManager = plugin.getGameBackend().getPlayerManager();
            playerManager.loadPlayerData(playerManager.castPlayer((Player) sender));
            sender.sendMessage("inventory loaded");
        }
    }

    /*
     * Debug - Save inventory
     */
    private void cmdDebug_Saveinv(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ErrorMessage.MUST_BE_PLAYER.sendTo(sender);
        } else {
            PlayerManager playerManager = plugin.getGameBackend().getPlayerManager();
            playerManager.savePlayerData(playerManager.castPlayer((Player) sender));
            sender.sendMessage("inventory saved");
        }
    }

    /**
     * Handler for game commands
     * @param sender
     * @param command
     * @param label
     * @pardeb ugam args
     */
    private void cmdGame(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 1) {
            switch (args[1]) {
                /* End game command */
                case "end":
                    cmdGame_End(sender, command, label, args);
                    return;

                /* Guess command */
                case "guess":
                    cmdGame_Guess(sender, command, label, args);
                    return;

                /* Idea command */
                case "idea":
                    cmdGame_Idea(sender, command, label, args);
                    return;

                /* Join game command */
                case "join":
                    cmdGame_Join(sender, command, label, args);
                    return;

                /* Leave game command */
                case "leave":
                    cmdGame_Leave(sender, command, label, args);
                    return;

                /* Game ready command */
                case "ready":
                    cmdGame_Ready(sender, command, label, args);
                    return;

                /* Game results command */
                case "results":
                    cmdGame_Results(sender, command, label, args);
                    return;

                /* Game room command */
                case "room":
                    cmdGame_Room(sender, command, label, args);
                    return;

                /* Game help command */
                case "help":
                    plugin.getHelpManager().handleHelpCommand(sender, "game", label, args, 2);
                    return;

                default:
                    ErrorMessage.UNKNOWN_COMMAND.sendTo(sender, label + " game");
                    return;
            }
        }
        plugin.getHelpManager().handleHelpCommand(sender, "game", label, args, 1);
    }

    /*
     * Game end command
     */
    private void cmdGame_End(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.ADMIN_GAME_END.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (args.length < 3) {
            int arenaId;
            try {
                arenaId = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                ErrorMessage.MUST_BE_INT.sendTo(sender, "Arena ID");
                return;
            }
            Arena arena = plugin.getGameBackend().getArenaManager().getArena(arenaId);
            if (arena == null) {
                ErrorMessage.ARENA_DOES_NOT_EXIST.sendTo(sender, Integer.toString(arenaId));
            } else {
                GameInstance gameInstance = arena.getGameInstance();
                if (gameInstance.getState() != GameState.IN_PROGRESS) {
                    ErrorMessage.NO_GAME_IN_PROGRESS.sendTo(sender);
                } else {
                    gameInstance.endGame();
                    NoticeMessage.ENDED_GAME.sendTo(sender);
                }
            }
        }
    }

    /*
     * Game guess command
     */
    private void cmdGame_Guess(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ErrorMessage.MUST_BE_PLAYER.sendTo(sender);
        } else if (!PermissionNode.GAME_PLAY.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (args.length < 3) {
            UsageMessage.GAME_GUESS.sendTo(sender, label);
        } else {
            String guess = ArrayUtils.stringArrayToString(args, 2);
            BgPlayer player = plugin.getGameBackend().getPlayerManager().castPlayer((Player) sender);
            GameInstance game = player.getCurrentGame();
            if (game == null) {
                ErrorMessage.NOT_IN_ANY_GAME.sendTo(sender);
            } else {
                Round round = game.getCurrentRound();
                if (!(round instanceof RoundGuess)) {
                    ErrorMessage.NOT_GUESSING_ROUND.sendTo(sender);
                } else if (((RoundGuess) round).submitGuess(player, guess)) {
                    NoticeMessage.SUBMITTED_GUESS.sendTo(sender, guess);
                } else {
                    ErrorMessage.GUESS_ALREADY_SUBMITTED.sendTo(sender);
                }
            }
        }
    }

    /*
     * Game idea command
     */
    private void cmdGame_Idea(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ErrorMessage.MUST_BE_PLAYER.sendTo(sender);
        } else if (!PermissionNode.GAME_PLAY.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (args.length < 3) {
            UsageMessage.GAME_IDEA.sendTo(sender, label);
        } else {
            String idea = ArrayUtils.stringArrayToString(args, 2);
            BgPlayer player = plugin.getGameBackend().getPlayerManager().castPlayer((Player) sender);
            GameInstance game = player.getCurrentGame();
            if (game == null) {
                ErrorMessage.NOT_IN_ANY_GAME.sendTo(sender);
            } else {
                Round round = game.getCurrentRound();
                if (!(round instanceof RoundThink)) {
                    ErrorMessage.NOT_GUESSING_ROUND.sendTo(sender);
                } else if (((RoundThink) round).submitIdea(player, idea)) {
                    NoticeMessage.SUBMITTED_IDEA.sendTo(sender, idea);
                } else {
                    ErrorMessage.IDEA_ALREADY_SUBMITTED.sendTo(sender);
                }
            }
        }
    }

    /*
     * Join game command
     */
    private void cmdGame_Join(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ErrorMessage.MUST_BE_PLAYER.sendTo(sender);
        } else if (!PermissionNode.GAME_PLAY.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else if (args.length < 3) {
            UsageMessage.GAME_JOIN.sendTo(sender, label);
        } else {
            int arenaId;
            try {
                arenaId = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                ErrorMessage.MUST_BE_INT.sendTo(sender, "Arena ID");
                return;
            }

            GameBackend gameBackend = plugin.getGameBackend();
            BgPlayer player = gameBackend.getPlayerManager().castPlayer((Player) sender);
            Arena arena = gameBackend.getArenaManager().getArena(arenaId);
            if (arena == null) {
                ErrorMessage.ARENA_DOES_NOT_EXIST.sendTo(sender);
            } else {
                GameInstance game = arena.getGameInstance();
                if (player.isInGame()) {
                    ErrorMessage.ALREADY_IN_OTHER_GAME.sendTo(sender);
                } else if (game.addPlayer(player)) {
                    NoticeMessage.JOINED_GAME.sendTo(sender);
                } else {
                    ErrorMessage.ALREADY_IN_GAME.sendTo(sender);
                }
            }
        }
    }

    /*
     * Leave game command
     */
    private void cmdGame_Leave(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ErrorMessage.MUST_BE_PLAYER.sendTo(sender);
        } else if (!PermissionNode.GAME_PLAY.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else {
            Log.debug("gameLeave command");
            BgPlayer player = plugin.getGameBackend().getPlayerManager().castPlayer((Player) sender);
            GameInstance game = player.getCurrentGame();
            if (game != null) {
                //Add verification message
                game.removePlayer(player);
                NoticeMessage.LEFT_GAME.sendTo(sender);
            } else {
                ErrorMessage.NOT_IN_ANY_GAME.sendTo(sender);
            }
        }
    }

    /*
     * Game ready command
     */
    private void cmdGame_Ready(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ErrorMessage.MUST_BE_PLAYER.sendTo(sender);
        } else if (!PermissionNode.GAME_PLAY.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else {
            BgPlayer player = plugin.getGameBackend().getPlayerManager().castPlayer((Player) sender);
            GameInstance game = player.getCurrentGame();
            if (game == null) {
                ErrorMessage.NOT_IN_ANY_GAME.sendTo(sender);
            } else {
                Round round = game.getCurrentRound();
                if (!(round instanceof RoundBuild)) {
                    ErrorMessage.CANNOT_MARK_READY.sendTo(sender);
                } else if (((RoundBuild) round).setComplete(player)) {
                    NoticeMessage.MARKED_READY.sendTo(sender);
                } else {
                    ErrorMessage.ALREADY_MARKED_READY.sendTo(sender);
                }
            }
        }
    }

    /*
     * Game results command
     */
    private void cmdGame_Results(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ErrorMessage.MUST_BE_PLAYER.sendTo(sender);
        } else if (!PermissionNode.GAME_PLAY.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else {
            BgPlayer player = plugin.getGameBackend().getPlayerManager().castPlayer((Player) sender);
            GameInstance game = player.getCurrentGame();
            if (game == null) {
                ErrorMessage.NOT_IN_ANY_GAME.sendTo(sender);
            } else {
                Round round = game.getCurrentRound();
                if (!(round instanceof RoundResults)) {
                    ErrorMessage.NOT_RESULTS_ROUND.sendTo(sender);
                } else if (args.length < 3) {
                    UsageMessage.GAME_RESULTS.sendTo(sender, label);
                    StringBuilder message = new StringBuilder();
                    List<BgPlayer> resultNumbers = ((RoundResults) round).getResultNumbers();
                    for (int i = 0; i < resultNumbers.size(); i++) {
                        if (message.length() != 0) message.append(ChatColor.DARK_GRAY + ", ");
                        message.append(ChatColor.GREEN + Integer.toString(i + 1) + ". " + ChatColor.GOLD + resultNumbers.get(i).getName());
                    }
                    sender.sendMessage(message.toString());
                } else {
                    int resultNum;
                    try {
                        resultNum = Integer.parseInt(args[2]) - 1;
                    } catch (NumberFormatException ex) {
                        ErrorMessage.MUST_BE_INT.sendTo(sender, "Result number");
                        return;
                    }

                    Entry<BgPlayer, List<String>> results;
                    try {
                        results = ((RoundResults) round).getResults(resultNum);
                    } catch (IndexOutOfBoundsException ex) {
                        ErrorMessage.INVALID_RESULT_NUMBER.sendTo(sender);
                        return;
                    }

                    sender.sendMessage(ChatColor.DARK_GRAY + "=====" + ChatColor.GREEN + "Results: " + ChatColor.GOLD + results.getKey().getName() + ChatColor.DARK_GRAY + "=====");
                    for (String message : results.getValue()) sender.sendMessage(message);
                }
            }
        }
    }

    /*
     * Game room teleport command
     */
    private void cmdGame_Room(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ErrorMessage.MUST_BE_PLAYER.sendTo(sender);
        } else if (!PermissionNode.GAME_PLAY.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else {
            BgPlayer player = plugin.getGameBackend().getPlayerManager().castPlayer((Player) sender);
            GameInstance game = player.getCurrentGame();
            if (game == null) {
                ErrorMessage.NOT_IN_ANY_GAME.sendTo(sender);
            } else {
                Round round = game.getCurrentRound();
                if (!(round instanceof RoundResults)) {
                    ErrorMessage.NOT_RESULTS_ROUND.sendTo(sender);
                } else if (args.length < 3) {
                    UsageMessage.GAME_ROOM.sendTo(sender, label);
                } else {
                    int roomNum;
                    try {
                        roomNum = Integer.parseInt(args[2]);
                    } catch (NumberFormatException ex) {
                        ErrorMessage.MUST_BE_INT.sendTo(sender, "Room number");
                        return;
                    }

                    Room room;
                    try {
                        room = ((RoundResults) round).getRoomNumbers().get(roomNum - 1);
                    } catch (IndexOutOfBoundsException ex) {
                        ErrorMessage.INVALID_ROOM.sendTo(sender);
                        return;
                    }

                    room.teleportPlayer(player);
                }
            }
        }
    }

    /**
     * Handler for reload command
     * @param sender
     * @param command
     * @param label
     * @param args
     */
    private void cmdReload(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.ADMIN_RELOAD.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else {
            plugin.reloadConfig();
            NoticeMessage.PLUGIN_RELOADED.sendTo(sender);
        }
    }

    /**
     * Handler for save command
     * @param sender
     * @param command
     * @param label
     * @param args
     */
    private void cmdSave(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.ADMIN_SAVE.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        } else {
            plugin.saveAll();
            NoticeMessage.PLUGIN_SAVED.sendTo(sender);
        }
    }

    /**
     * Handler for version command
     * @param sender
     * @param command
     * @param label
     * @param args
     */
    private final void cmdVersion(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GREEN + plugin.getName() + ChatColor.GOLD + " v" + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.GOLD + "Created by Stealth2800");
        sender.sendMessage(ChatColor.GOLD + "Website: " + ChatColor.AQUA + "http://stealthyone.com/bukkit");
        UpdateChecker updateChecker = plugin.getUpdateChecker();
        if (updateChecker.checkForUpdates()) {
            String curVer = plugin.getDescription().getVersion();
            String remVer = updateChecker.getNewVersion().replace("v", "");
            sender.sendMessage(ChatColor.RED + "A different version was found on BukkitDev! (Current: " + curVer + " | Remote: " + remVer + ")");
            sender.sendMessage(ChatColor.RED + "You can download it from " + updateChecker.getVersionLink());
        }
    }

}