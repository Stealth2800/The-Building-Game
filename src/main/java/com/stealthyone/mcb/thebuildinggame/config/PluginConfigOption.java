/*
 * The Building Game - Bukkit Plugin
 * Copyright (C) 2014 Stealth2800 <stealth2800@stealthyone.com>
 * Website: <http://stealthyone.com>
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
package com.stealthyone.mcb.thebuildinggame.config;

import com.stealthyone.mcb.thebuildinggame.TheBuildingGame;
import com.stealthyone.mcb.thebuildinggame.util.ConfigOption;
import org.bukkit.configuration.ConfigurationSection;

public class PluginConfigOption<T> extends ConfigOption<T> {

    public PluginConfigOption(String name, T def) {
        super(name, def);
    }

    public T load() {
        return super.load(TheBuildingGame.getInstance().getConfig());
    }

    @Override
    public void save(ConfigurationSection config) {
        super.save(TheBuildingGame.getInstance().getConfig());
    }

}