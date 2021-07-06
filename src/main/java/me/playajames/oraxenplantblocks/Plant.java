package me.playajames.oraxenplantblocks;

import io.th0rgal.oraxen.items.OraxenItems;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import me.playajames.oraxenplantblocks.OraxenMechanics.PlantMechanic;
import me.playajames.oraxentransparentblocks.OraxenTransparentBlock;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.UUID;

import static me.playajames.oraxenplantblocks.OraxenPlantBlocks.DEBUG;

public class Plant {

    private String plantId;
    private OraxenTransparentBlock block;
    private int stage;


    public Plant(String plantId, OraxenTransparentBlock block) {
        this.plantId = plantId;
        this.block = block;
        this.stage = 0;
        updateBlock();
    }

    public Plant(String plantId, String worldId, String chunkId, String uuidString, int stage) {
        this.plantId = plantId;
        this.block = new OraxenTransparentBlock(worldId, chunkId, uuidString);
        this.stage = stage;
    }

    public String getPlantId() {
        return plantId;
    }

    public OraxenTransparentBlock getBlock() {
        return block;
    }

    public boolean canGrow() {
        if (!hasStage(stage + 1)) return false;
        PlantMechanic mechanic = (PlantMechanic) MechanicsManager.getMechanicFactory("plant").getMechanic(plantId);
        double random = new Random().nextDouble();
        if (DEBUG) Bukkit.getLogger().info("Calculation: Grow Chance = " + mechanic.getGrowChance() + " | Random = " + random);
        if (mechanic.getGrowChance() >= random) return true;
        return false;
    }

    public boolean canPlace(Material material) {
        PlantMechanic mechanic = (PlantMechanic) MechanicsManager.getMechanicFactory("plant").getMechanic(plantId);
        if (mechanic.getValidGrowMediums().contains(material)) return true;
        return false;
    }

    public void grow() {
        setStage(stage + 1);
    }

    public void tick() {
        if (canGrow())
            grow();
    }

    public boolean hasStage(int stage) {
        PlantMechanic mechanic = (PlantMechanic) MechanicsManager.getMechanicFactory("plant").getMechanic(plantId);
        if (!mechanic.getStages().containsKey(stage)) return false;
        return true;
    }

    public void setStage(int stage) {
        if (!hasStage(stage)) return;
        if (DEBUG) Bukkit.getLogger().info("Plant stage update from " + this.stage + " to " + stage + ".");
        this.stage = stage;
        updateBlock();
        PlantManager.update(this);
    }

    public int getStage() {
        return stage;
    }

    private void updateBlock() {
        PlantMechanic mechanic = (PlantMechanic) MechanicsManager.getMechanicFactory("plant").getMechanic(plantId);
        if (!hasStage(stage)) return;
        ItemStack stageItem = OraxenItems.getItemById(mechanic.getStages().get(stage)).build();
        block.setBlockType(stageItem);
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder();
        String armorstandUuid = block.getArmorStand().getUniqueId().toString();

        builder.append(plantId).append(",");
        builder.append(stage).append(",");
        builder.append(armorstandUuid);

        return builder.toString();

    }

    public String serializeVerbose() {
        StringBuilder builder = new StringBuilder();
        String armorstandUuid = block.getArmorStand().getUniqueId().toString();

        builder.append(plantId).append(",");
        builder.append(stage).append(",");
        builder.append(armorstandUuid).append(",");
        builder.append(block.getArmorStand().getLocation().getWorld().getUID()).append(",");
        builder.append(block.getArmorStand().getLocation().getChunk().getChunkKey());

        return builder.toString();

    }
}
