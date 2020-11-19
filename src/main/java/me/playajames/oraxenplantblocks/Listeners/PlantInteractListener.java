package me.playajames.oraxenplantblocks.Listeners;

import me.playajames.oraxenplantblocks.Plant;
import me.playajames.oraxenplantblocks.PlantManager;
import me.playajames.oraxentransparentblocks.Events.OraxenTransparentBlockInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlantInteractListener implements Listener {
    @EventHandler
    public void onPlantBreak(OraxenTransparentBlockInteractEvent event) {

        if (!PlantManager.isPlant(event.getBlock().getArmorStand())) return;

        Plant plant = PlantManager.getPlant(event.getBlock().getArmorStand());
        event.getPlayer().sendMessage(plant.serializeVerbose());

    }
}
