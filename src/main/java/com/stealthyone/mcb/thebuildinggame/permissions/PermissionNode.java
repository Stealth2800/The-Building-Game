package com.stealthyone.mcb.thebuildinggame.permissions;

import com.stealthyone.mcb.thebuildinggame.messages.Messages;
import org.bukkit.command.CommandSender;

public enum PermissionNode {

    ADMIN_RELOAD,

    ARENA_LIST;

    private String permission;

    private PermissionNode() {
        permission = "thebuildinggame." + toString().toLowerCase().replace("_", ".");
    }

    public boolean isAllowed(CommandSender sender) {
        return sender.hasPermission(permission);
    }

    public boolean isAllowedAlert(CommandSender sender) {
        boolean result = isAllowed(sender);
        if (!result) {
            Messages.NO_PERMISSION.sendTo(sender);
        }
        return result;
    }

}