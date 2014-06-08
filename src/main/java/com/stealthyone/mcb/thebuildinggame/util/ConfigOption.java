package com.stealthyone.mcb.thebuildinggame.util;

import org.bukkit.configuration.ConfigurationSection;

public class ConfigOption<T> implements Cloneable {

    private String name;
    private T def;
    private T val;

    public ConfigOption(String name, T def) {
        this.name = name;
        this.def = def;
    }

    public ConfigOption<T> create() {
        return new ConfigOption<>(name, def);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof ConfigOption) {
            return ((ConfigOption) obj).name.equals(this.name);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public T getDefault() {
        return def;
    }

    public T getValueRaw() {
        return val;
    }

    public T getValue() {
        return val != null ? val : def;
    }

    public void setValue(T val) {
        this.val = val;
    }

    public T load(ConfigurationSection config) {
        if (!config.isSet(name)) {
            val = def;
        } else {
            val = (T) config.get(name);
        }
        return val;
    }

    public void save(ConfigurationSection config) {
        config.set(name, val);
    }

}