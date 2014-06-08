package com.stealthyone.mcb.thebuildinggame.backend.rooms;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.stealthyone.mcb.stbukkitlib.storage.YamlFileManager;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public class RoomType {

    private YamlFileManager file;

    private int id;
    private String nickname;

    private String schematicName;
    private int schematicHeight;
    private int schematicWidth;
    private int schematicLength;

    public RoomType(int id, YamlFileManager file) throws InvalidConfigurationException {
        this.id = id;
        this.file = file;
        load();
    }

    private void load() throws InvalidConfigurationException {
        FileConfiguration config = file.getConfig();

        nickname = config.getString("nickname");
        schematicName = config.getString("schematicName");
        if (schematicName == null) {
            throw new InvalidConfigurationException("Schematic name cannot be null.");
        }

        if (getSchematicFile() == null) {
            throw new InvalidConfigurationException("Unable to find room schematic '" + schematicName + "'");
        }

        CuboidClipboard schematic;
        try {
            schematic = loadSchematic();
        } catch (Exception ex) {
            TheBuildingGame.getInstance().getLogger().severe("Unable to load schematic for RoomType #" + id);
            ex.printStackTrace();
            return;
        }

        schematicHeight = schematic.getHeight();
        schematicWidth = schematic.getWidth();
        schematicLength = schematic.getLength();
    }

    private File getSchematicFile() {
        File file = new File(TheBuildingGame.getInstance().getRoomManager().getSchematicDir() + File.separator + schematicName);
        return !file.exists() ? null : file;
    }

    private CuboidClipboard loadSchematic() throws IOException, DataException {
        File file = getSchematicFile();
        return SchematicFormat.getFormat(file).load(file);
    }

    public int getHeight() {
        return schematicHeight;
    }

    public int getWidth() {
        return schematicWidth;
    }

    public int getLength() {
        return schematicLength;
    }

}