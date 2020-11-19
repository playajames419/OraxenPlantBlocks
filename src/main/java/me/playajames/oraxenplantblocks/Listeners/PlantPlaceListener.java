package me.playajames.oraxenplantblocks.Listeners;

import me.playajames.oraxenplantblocks.Plant;
import me.playajames.oraxenplantblocks.PlantManager;
import me.playajames.oraxentransparentblocks.Events.OraxenTransparentBlockPlaceEvent;
import me.playajames.oraxentransparentblocks.Events.OraxenTransparentBlockPrePlaceEvent;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlantPlaceListener implements Listener {

    @EventHandler
    public void onPlantPrePlace(OraxenTransparentBlockPrePlaceEvent event) {
        if (!PlantManager.isSeed(event.getItem())) return;
        if (!PlantManager.canPlant(PlantManager.getPlantId(event.getItem()), event.getLocation().getBlock().getRelative(BlockFace.DOWN).getType())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlantPlace(OraxenTransparentBlockPlaceEvent event) {
        if (!PlantManager.isSeed(event.getItem())) return;
        Plant plant = new Plant(PlantManager.getPlantId(event.getItem()), event.getBlock());
        PlantManager.addPlant(plant);
    }
}
