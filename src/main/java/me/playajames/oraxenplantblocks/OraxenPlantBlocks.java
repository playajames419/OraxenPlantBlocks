package me.playajames.oraxenplantblocks;

import de.leonhard.storage.Config;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import me.playajames.oraxenplantblocks.Listeners.ChunkLoadListener;
import me.playajames.oraxenplantblocks.Listeners.ChunkUnloadListener;
import me.playajames.oraxenplantblocks.OraxenMechanics.PlantMechanic;
import me.playajames.oraxenplantblocks.OraxenMechanics.PlantMechanicFactory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public final class OraxenPlantBlocks extends JavaPlugin {

    // TODO Break plants when block below plant is broken or changed, eg. farmland changes to dirt.
    // TODO Add possibility to trample plants

    public static Config CONFIG;
    public static boolean DEBUG;

    @Override
    public void onEnable() {
        loadConfig();
        registerListeners();
        MechanicsManager.registerMechanicFactory("plant", PlantMechanicFactory.class);
        getLogger().info("Mechanic 'plant' registered with Oraxen.");
        loadPlants();
        PlantManager.startTickScheduler();
        PlantManager.startSaveScheduler();
        getLogger().info("Enabled successfully.");
    }

    @Override
    public void onDisable() {
        PlantManager.saveAll();
        getLogger().info("Disabled successfully.");
    }

    private void loadConfig() {
        this.saveDefaultConfig();
        CONFIG = new Config("config", "plugins/OraxenPlantBlocks");
        DEBUG = CONFIG.getBoolean("debug");
        getLogger().info("Debugging is set to " + DEBUG + ".");
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new ChunkLoadListener(), this);
        this.getServer().getPluginManager().registerEvents(new ChunkUnloadListener(), this);
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
        }, 20L* 5);
    }
}
