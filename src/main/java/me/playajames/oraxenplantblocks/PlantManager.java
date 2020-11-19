package me.playajames.oraxenplantblocks;

import de.leonhard.storage.Yaml;
import io.th0rgal.oraxen.items.OraxenItems;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import me.playajames.oraxenplantblocks.OraxenMechanics.PlantMechanic;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static me.playajames.oraxenplantblocks.OraxenPlantBlocks.CONFIG;
import static me.playajames.oraxenplantblocks.OraxenPlantBlocks.DEBUG;

public class PlantManager {

    private static HashMap<World, HashMap<Chunk, HashMap<UUID, Plant>>> loadedPlants = new HashMap<>();
    private static String dataFolderPath = OraxenPlantBlocks.getPlugin(OraxenPlantBlocks.class).getDataFolder().getPath() + "/data/";
    private static HashMap<String, String> plants = new HashMap<>();

    public static void register(ItemStack seedItem, String plantId) {
        plants.put(OraxenItems.getIdByItem(seedItem), plantId);
    }

    public static boolean isSeed(ItemStack item) {
        for (String item1 : plants.keySet())
            if (OraxenItems.getIdByItem(item).equals(item1)) return true;
        return false;
    }

    public static String getPlantId(ItemStack seed) {
        return plants.get(OraxenItems.getIdByItem(seed));
    }

    public static boolean isPlant(ArmorStand armorStand) {
        if (getPlant(armorStand) != null) return true;
        return false;
    }

    public static boolean addPlant(Plant plant) {
        Chunk chunk = plant.getBlock().getArmorStand().getLocation().getChunk();
        if (!isChunkLoaded(chunk)) return false;
        if (loadedPlants.get(chunk.getWorld()).get(chunk).containsKey(plant.getBlock().getArmorStand().getUniqueId())) return false;
        loadedPlants.get(chunk.getWorld()).get(chunk).put(plant.getBlock().getArmorStand().getUniqueId(), plant);
        return true;
    }

    public static boolean removePlant(Plant plant) {
        Chunk chunk = plant.getBlock().getArmorStand().getLocation().getChunk();
        if (!isChunkLoaded(chunk)) return false;
        if (!loadedPlants.get(chunk.getWorld()).get(chunk).containsKey(plant.getBlock().getArmorStand().getUniqueId())) return false;
        loadedPlants.get(chunk.getWorld()).get(chunk).remove(plant.getBlock().getArmorStand().getUniqueId());
        return true;
    }

    private static boolean worldMapExists(World world) {
        if (loadedPlants.isEmpty()) return false;
        if (loadedPlants.containsKey(world)) return true;
        return false;
    }

    private static void generateWorldMap(World world) {
        loadedPlants.put(world, new HashMap<>());
    }

    private static void removeWorldMapIfEmpty(World world) {
        if (loadedPlants.get(world).isEmpty())
            loadedPlants.remove(world);
    }

    private static boolean isChunkLoaded(Chunk chunk) {
        if (!worldMapExists(chunk.getWorld())) return false;
        if (loadedPlants.get(chunk.getWorld()).containsKey(chunk)) return true;
        return false;
    }

    public static boolean loadChunk(Chunk chunk) {
        Yaml storage = new Yaml(String.valueOf(chunk.getChunkKey()), dataFolderPath + chunk.getWorld().getUID());
        HashMap<UUID, Plant> plants = parseChunkStorageFile(chunk, storage);
        if (!worldMapExists(chunk.getWorld())) generateWorldMap(chunk.getWorld());
        if (isChunkLoaded(chunk)) return true;
        loadedPlants.get(chunk.getWorld()).put(chunk, plants);
        return false;
    }

    public static boolean unloadChunk(Chunk chunk) {
        if (!isChunkLoaded(chunk)) return true;
        loadedPlants.get(chunk.getWorld()).remove(chunk);
        removeWorldMapIfEmpty(chunk.getWorld());
        return false;
    }

    public static Plant getPlant(ArmorStand armorStand) {
        List<Plant> plants = getPlants(armorStand.getChunk());
        if (plants == null) return null;
        for (Plant plant : plants)
            if (plant.getBlock().getArmorStand().getUniqueId().equals(armorStand.getUniqueId()))
                return plant;
        return null;
    }

    public static List<Plant> getPlants(World world) {
        List<Plant> plantsList = new ArrayList<Plant>();
        if (loadedPlants.isEmpty()) return null;
        for (HashMap<UUID, Plant> chunkBlockMap : loadedPlants.get(world).values())
            for (Plant plant : chunkBlockMap.values())
                plantsList.add(plant);
        return plantsList;
    }

    public static List<Plant> getPlants(Chunk chunk) {
        if (!isChunkLoaded(chunk)) return null;
        List<Plant> plantsList = new ArrayList<>();
        for (Plant plant : loadedPlants.get(chunk.getWorld()).get(chunk).values())
            plantsList.add(plant);
        return plantsList;
    }

    public static List<Plant> getPlants() {
        List<Plant> plants = new ArrayList<>();
        for (HashMap<Chunk, HashMap<UUID, Plant>> world : loadedPlants.values())
            for (HashMap<UUID, Plant> chunk : world.values())
                for (Plant plant : chunk.values())
                    plants.add(plant);
        return plants;
    }

    public static boolean canPlant(String plantId, Material medium) {
        PlantMechanic mechanic = (PlantMechanic) MechanicsManager.getMechanicFactory("plant").getMechanic(plantId);
        if (mechanic.getValidGrowMediums().contains(medium)) return true;
        return false;
    }

    public static void saveWorld(World world) {
        if (!worldMapExists(world)) return;
        for (Chunk chunk : loadedPlants.get(world).keySet())
            saveChunk(chunk);
    }

    public static void saveChunk(Chunk chunk) {
        if (!isChunkLoaded(chunk)) return;
        Yaml storage = new Yaml(String.valueOf(chunk.getChunkKey()), dataFolderPath + chunk.getWorld().getUID());
        Collection<Plant> plants = loadedPlants.get(chunk.getWorld()).get(chunk).values();
        Set<String> activeStoredBlocks = storage.keySet();
        for (Plant plant : plants) {
            if (activeStoredBlocks.contains(plant.getBlock().getArmorStand().getUniqueId().toString())) {
                activeStoredBlocks.remove(plant.getBlock().getArmorStand().getUniqueId().toString());
                continue;
            }
            storage.set(plant.serialize(), null);
        }
        if (activeStoredBlocks.isEmpty()) return;
        for (String key : activeStoredBlocks)
            storage.remove(key);
    }

    public static void saveAll() {
        if (DEBUG) Bukkit.getLogger().info("Saving all plant data...");
        for (World world : loadedPlants.keySet())
            saveWorld(world);
        if (DEBUG) Bukkit.getLogger().info("Plant data saved successfully.");
    }

    public static boolean destroyChunkStorageFile(long chunkId) {
        return false;
    }

    private static HashMap<UUID, Plant> parseChunkStorageFile(Chunk chunk, Yaml storage) {

        HashMap<UUID, Plant> plants = new HashMap<>();

        Set<String> sections = storage.keySet();

        for (String section : sections) {
            Plant plant = new Plant(section, chunk);
            if (plant.getBlock().getArmorStand() == null) continue;
            plants.put(plant.getBlock().getArmorStand().getUniqueId(), plant);
        }

        return plants;
    }

    public static void startSaveScheduler() {
        try {
            Bukkit.getScheduler().runTaskTimer(OraxenPlantBlocks.getPlugin(OraxenPlantBlocks.class), () -> PlantManager.saveAll(), 20L * CONFIG.getInt("save_interval"), 20L * CONFIG.getInt("save_interval"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startTickScheduler() {
        try {
            Bukkit.getScheduler().runTaskTimer(OraxenPlantBlocks.getPlugin(OraxenPlantBlocks.class), () -> {
                if (DEBUG) Bukkit.getLogger().info("Attempting to tick " + getPlants().size() + " plants.");
                for (Plant plant : getPlants()) {
                    plant.tick();
                }
            }, 20L * CONFIG.getInt("tick_interval"), 20L * CONFIG.getInt("tick_interval"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
