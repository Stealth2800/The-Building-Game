package com.stealthyone.mcb.thebuildinggame.backend.rooms;

import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.backend.games.Game;
import org.bukkit.configuration.InvalidConfigurationException;

/*
 * Handles the build process for arenas.
 */
public class RoomBuilder {

    private int maxPlayers;
    private RoomType roomType;

    private boolean started = false;
    private int totalLength;
    private int totalWidth;
    private int startX;
    private int startY;
    private int startZ;
    private int spacing;

    public RoomBuilder(Game game) throws InvalidConfigurationException {
        this.maxPlayers = game.getArena().getMaxPlayers();
        this.roomType = TheBuildingGame.getInstance().getRoomManager().getRoomType(game.getArena().getRoomType());
        spacing = game.getArena().getRoomSpacing();
        if (roomType == null) {
            throw new InvalidConfigurationException("No room type found with ID: " + game.getArena().getRoomType());
        }
    }

    /**
     * Begins building the arena.
     *
     * @return True if successful.
     *         False if process has already started.
     *
     * @throws InvalidConfigurationException Thrown if the builder encountered problems with the
     *         arena's configuration.
     */
    public boolean initiateBuild() throws InvalidConfigurationException {
        if (started) return false;

        int neededRooms = (int) ((0.5 * Math.pow(maxPlayers, 2)) - (0.5 * maxPlayers));
        if (neededRooms <= 1 || maxPlayers % 2 != 1) {
            throw new InvalidConfigurationException("Invalid max players number: " + maxPlayers);
        }

        int rows = neededRooms / maxPlayers;
        int columns = maxPlayers;

        totalWidth = (columns * roomType.getWidth()) + ((columns + 1) * spacing);
        totalLength = (rows * roomType.getLength()) + ((rows + 1) * spacing);

        started = true;
        return true;
    }

}