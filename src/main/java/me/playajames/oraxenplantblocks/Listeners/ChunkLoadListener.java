package me.playajames.oraxenplantblocks.Listeners;

import me.playajames.oraxenplantblocks.PlantManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkLoadListener implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        PlantManager.loadChunk(event.getChunk());
    }
}
