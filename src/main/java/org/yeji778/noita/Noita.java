package org.yeji778.noita;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Noita extends JavaPlugin {
   public static Noita Instance;
    @Override
    public void onEnable() {
        Instance = this;
        Bukkit.getPluginManager().registerEvents(new PlayerListener(),getInstance());
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }

    public static Noita getInstance() {
        return Instance;
    }
}
