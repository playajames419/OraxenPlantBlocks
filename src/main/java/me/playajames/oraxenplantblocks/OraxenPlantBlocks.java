package me.playajames.oraxenplantblocks;

import de.leonhard.storage.Config;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import me.playajames.oraxenplantblocks.Listeners.*;
import me.playajames.oraxenplantblocks.OraxenMechanics.PlantMechanic;
import me.playajames.oraxenplantblocks.OraxenMechanics.PlantMechanicFactory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;

public final class OraxenPlantBlocks extends JavaPlugin {

    // TODO Break plants when block below plant is broken or changed, eg. farmland changes to dirt.
    // TODO Add possibility to trample plants

    public static Config CONFIG;
    public static boolean DEBUG;
    public static Connection CONNECTION;

    @Override
    public void onEnable() {
        if (!verifyDepends()) return;
        loadConfig();
        initDatabase(); // TODO DIsable plugin if false
        registerListeners();
        MechanicsManager.registerMechanicFactory("plant", PlantMechanicFactory.class);
        if (!verifyMechanic()) return;
        getLogger().info("Mechanic 'plant' registered with Oraxen.");
        loadPlants();
        PlantManager.startTickScheduler();
        getLogger().info("Enabled successfully.");
    }

    @Override
    public void onDisable() {
        //PlantManager.saveAll();
        getLogger().info("Disabled successfully.");
    }

    private void loadConfig() {
        this.saveDefaultConfig();
        CONFIG = new Config("config", "plugins/OraxenPlantBlocks");
        DEBUG = CONFIG.getBoolean("debug");
        getLogger().info("Debugging is set to " + DEBUG + ".");
    }

    private void registerListeners() {
        MechanicsManager.registerListeners(OraxenPlugin.get(), new PlantPlaceListener());
        MechanicsManager.registerListeners(OraxenPlugin.get(), new PlantBreakListener());
        MechanicsManager.registerListeners(OraxenPlugin.get(), new PlantInteractListener());
    }

    private boolean verifyDepends() {
        if (!Bukkit.getPluginManager().isPluginEnabled("OraxenTransparentBlocks")) {
            getLogger().info("Depend OraxenTransparentBlocks was not initialized correctly.");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
    }

    private boolean verifyMechanic() {
        if (MechanicsManager.getMechanicFactory("plant") == null) {
            getLogger().info("`plant` mechanic has not been added to the Oraxen mechanics.yml file.");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
    }

    private boolean initDatabase() {
        try {
            CONNECTION = DriverManager.getConnection("jdbc:sqlite:plugins/OraxenPlantBlocks/storage.db");
            CONNECTION.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS 'plants' (" +
                            "'id' INTEGER NOT NULL UNIQUE, " +
                            "'plant_id' TEXT NOT NULL, " +
                            "'world' TEXT NOT NULL, " +
                            "'chunk' TEXT NOT NULL, " +
                            "'uuid' TEXT NOT NULL, " +
                            "'stage' INTEGER NOT NULL, " +
                            "PRIMARY KEY('id' AUTOINCREMENT)" +
                            ");"
            );
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    private void loadPlants() {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            Set<String> itemIds = MechanicsManager.getMechanicFactory("plant").getItems();
            if (itemIds.isEmpty()) return;
            for (String itemId : itemIds) {
                PlantMechanic mechanic = (PlantMechanic) MechanicsManager.getMechanicFactory("plant").getMechanic(itemId);
                PlantManager.register(mechanic.getSeed(), mechanic.getItemID());
                getLogger().info("Added plant '" + itemId + "' to plants.");
            }
        }, 20L * 5);
    }

    // TODO Check for plant at location when placing, can currently place as many plants as player wants in one location
}
