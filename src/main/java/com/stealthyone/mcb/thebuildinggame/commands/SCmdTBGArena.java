package com.stealthyone.mcb.thebuildinggame.commands;

import com.stealthyone.mcb.stbukkitlib.utils.MessageUtils;
import com.stealthyone.mcb.stbukkitlib.utils.MiscUtils;
import com.stealthyone.mcb.stbukkitlib.utils.QuickHashMap;
import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.backend.arenas.Arena;
import com.stealthyone.mcb.thebuildinggame.messages.Messages;
import com.stealthyone.mcb.thebuildinggame.permissions.PermissionNode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SCmdTBGArena {

    private TheBuildingGame plugin;

    protected SCmdTBGArena(TheBuildingGame plugin) {
        this.plugin = plugin;
    }

    protected void handleCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args[1].toLowerCase()) {
            case "list":
                cmdList(sender, command, label, args);
                return;
        }
    }

    /*
     * List loaded arenas.
     */
    private void cmdList(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.ARENA_LIST.isAllowedAlert(sender)) return;

        int page;
        try {
            page = Integer.parseInt(args[2]);
        } catch (IndexOutOfBoundsException ex) {
            page = 1;
        } catch (NumberFormatException ex) {
            Messages.PAGE_MUST_BE_INT.sendTo(sender);
            return;
        }

        List<Arena> loadedArenas = plugin.getArenaManager().getLoadedArenas();
        int maxPages = MiscUtils.getPageCount(loadedArenas.size(), 8);

        List<String> messages = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int index = i + ((page - 1) * 8);
            Arena arena;
            try {
                arena = loadedArenas.get(index);
                messages.addAll(Arrays.asList(Messages.ARENAS_LIST_ITEM.getFormattedMessages(new QuickHashMap<String, String>()
                    .put("{LISTNUMBER}", Integer.toString(index))
                    .put("{NAMERAW}", arena.getNickname())
                    .put("{NAME}", (arena.isEnabled() ? ChatColor.GREEN : ChatColor.RED) + arena.getNickname())
                    .put("{ID}", Integer.toString(arena.getId()))
                    .build()
                )));
            } catch (Exception ex) {
                continue;
            }
        }

        if (messages.size() == 0) {
            messages.addAll(Arrays.asList(Messages.ARENAS_LIST_NONE.getFormattedMessages()));
        }

        Messages.ARENAS_LIST.sendTo(sender, new QuickHashMap<String, String>()
            .put("{PAGE}", Integer.toString(page))
            .put("{MAXPAGES}", Integer.toString(maxPages))
            .put("{ARENALIST}", MessageUtils.stringListToString(messages))
            .build()
        );

        if (page < maxPages) {
            Messages.PAGE_NOTICE.sendTo(sender, new QuickHashMap<String, String>()
                .put("{LABEL}", label)
                .put("{COMMAND}", " arena list ")
                .put("{NEXTPAGE}", Integer.toString(page + 1))
                .build()
            );
        }
    }

}