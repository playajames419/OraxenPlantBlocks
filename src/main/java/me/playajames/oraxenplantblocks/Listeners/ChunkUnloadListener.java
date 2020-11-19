package me.playajames.oraxenplantblocks.Listeners;

import me.playajames.oraxenplantblocks.PlantManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkUnloadListener implements Listener {
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        PlantManager.unloadChunk(event.getChunk());
    }
}
