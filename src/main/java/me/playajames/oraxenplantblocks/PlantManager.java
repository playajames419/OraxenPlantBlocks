package me.playajames.oraxenplantblocks;

import io.th0rgal.oraxen.items.OraxenItems;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import me.playajames.oraxenplantblocks.OraxenMechanics.PlantMechanic;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static me.playajames.oraxenplantblocks.OraxenPlantBlocks.CONFIG;
import static me.playajames.oraxenplantblocks.OraxenPlantBlocks.CONNECTION;

public class PlantManager {

    private static HashMap<String, String> plants = new HashMap<>();

    public static boolean isPlant(ArmorStand armorStand) {
        if (getPlant(armorStand) != null) return true;
        return false;
    }

    public static boolean isSeed(ItemStack item) {
        for (String item1 : plants.keySet())
            if (OraxenItems.getIdByItem(item).equals(item1)) return true;
        return false;
    }

    public static boolean addPlant(Plant plant) {
        UUID world = plant.getBlock().getArmorStand().getLocation().getWorld().getUID();
        long chunk = plant.getBlock().getArmorStand().getLocation().getChunk().getChunkKey();
        UUID uuid = plant.getBlock().getArmorStand().getUniqueId();
        try {
            Statement statement = CONNECTION.createStatement();
            statement.executeUpdate("INSERT INTO 'plants' (plant_id,world,chunk,uuid,stage) VALUES('" + plant.getPlantId() + "','" + world + "','" + chunk + "','" + uuid + "','" + plant.getStage() + "')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean removePlant(Plant plant) {
        UUID world = plant.getBlock().getArmorStand().getLocation().getWorld().getUID();
        long chunk = plant.getBlock().getArmorStand().getLocation().getChunk().getChunkKey();
        UUID uuid = plant.getBlock().getArmorStand().getUniqueId();
        try {
            Statement statement = CONNECTION.createStatement();
            statement.executeUpdate("DELETE FROM 'plants' WHERE world = '" + world + "' AND chunk = '" + chunk + "' AND uuid = '" + uuid + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    public static String getPlantId(ItemStack seed) {
        return plants.get(OraxenItems.getIdByItem(seed));
    }

    public static String getPlantId(ArmorStand armorStand) {
        return plants.get(OraxenItems.getIdByItem(armorStand.getItem(EquipmentSlot.HEAD)));
    }

    public static Plant getPlant(ArmorStand armorStand) {
        String world = armorStand.getLocation().getWorld().getUID().toString();
        String chunk = String.valueOf(armorStand.getLocation().getChunk().getChunkKey());
        String uuid = armorStand.getUniqueId().toString();
        Plant plant = null;
        try {
            Statement statement = CONNECTION.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM plants WHERE world = '" + world + "' AND chunk = '" + chunk + "' AND uuid = '" + uuid + "'");
            if (result.next()) {
                plant = new Plant(
                        result.getString("plant_id"),
                        result.getString("world"),
                        result.getString("chunk"),
                        result.getString("uuid"),
                        result.getInt("stage"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return plant;
    }

    public static List<Plant> getPlants() {
        List<Plant> plants = new ArrayList<>();
        try {
            Statement statement = CONNECTION.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM 'plants'");
            while (result.next())
                plants.add(new Plant(
                        result.getString("plant_id"),
                        result.getString("world"),
                        result.getString("chunk"),
                        result.getString("uuid"),
                        result.getInt("stage")));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return plants;
    }

    public static List<Plant> getPlants(World world) {
        List<Plant> plants = new ArrayList<>();
        try {
            Statement statement = CONNECTION.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM 'plants' WHERE world = '" + world.getUID() + "'");
            while (result.next())
                plants.add(new Plant(
                        result.getString("plant_id"),
                        result.getString("world"),
                        result.getString("chunk"),
                        result.getString("uuid"),
                        result.getInt("stage")));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return plants;
    }

    public static List<Plant> getPlants(Chunk chunk) {
        List<Plant> plants = new ArrayList<>();
        try {
            Statement statement = CONNECTION.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM 'plants' WHERE world = '" + chunk.getWorld().getUID() + "' AND chunk = '" + chunk.getChunkKey() + "'");
            while (result.next())
                plants.add(new Plant(
                        result.getString("plant_id"),
                        result.getString("world"),
                        result.getString("chunk"),
                        result.getString("uuid"),
                        result.getInt("stage")));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return plants;
    }

    public static void register(ItemStack seedItem, String plantId) {
        plants.put(OraxenItems.getIdByItem(seedItem), plantId);
    }

    public static boolean canPlant(String plantId, Material medium) {
        PlantMechanic mechanic = (PlantMechanic) MechanicsManager.getMechanicFactory("plant").getMechanic(plantId);
        if (mechanic.getValidGrowMediums().contains(medium)) return true;
        return false;
    }

    public static boolean update(Plant plant) {
        try {
            Statement statment = CONNECTION.createStatement();
            Location location = plant.getBlock().getArmorStand().getLocation();
            statment.executeUpdate("UPDATE plants SET stage = " + plant.getStage() + " WHERE plant_id = '" + plant.getPlantId() + "' AND world = '" + location.getWorld().getUID() + "' AND chunk = '" + location.getChunk().getChunkKey() + "' AND uuid = '" + plant.getBlock().getArmorStand().getUniqueId() + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public static void startTickScheduler() {
        try {
            Bukkit.getScheduler().runTaskTimer(OraxenPlantBlocks.getPlugin(OraxenPlantBlocks.class), () -> {
                if (OraxenPlantBlocks.DEBUG) Bukkit.getLogger().info("Attempting to tick " + getPlants().size() + " plants.");
                for (Plant plant : getPlants()) {
                    plant.tick();
                }
            }, 20L * CONFIG.getInt("tick_interval"), 20L * CONFIG.getInt("tick_interval"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
