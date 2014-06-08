package com.stealthyone.mcb.thebuildinggame.backend.games;

import com.stealthyone.mcb.thebuildinggame.backend.arenas.Arena;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Game {

    private Arena arena;

    private Map<String, Player> joinedPlayers = new HashMap<>();

    public Game(Arena arena) {
        this.arena = arena;
    }

    public Arena getArena() {
        return arena;
    }

}