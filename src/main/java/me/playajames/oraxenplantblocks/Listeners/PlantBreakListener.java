package me.playajames.oraxenplantblocks.Listeners;

import me.playajames.oraxenplantblocks.PlantManager;
import me.playajames.oraxentransparentblocks.Events.OraxenTransparentBlockPreBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlantBreakListener implements Listener {
    @EventHandler
    public void onPlantBreak(OraxenTransparentBlockPreBreakEvent event) {

        if (!PlantManager.isPlant(event.getBlock().getArmorStand())) return;

        PlantManager.removePlant(PlantManager.getPlant(event.getBlock().getArmorStand()));

    }
}
