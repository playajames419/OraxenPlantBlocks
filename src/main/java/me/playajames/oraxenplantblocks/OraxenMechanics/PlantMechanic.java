package me.playajames.oraxenplantblocks.OraxenMechanics;

import io.th0rgal.oraxen.items.OraxenItems;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.playajames.oraxenplantblocks.OraxenPlantBlocks.DEBUG;

public class PlantMechanic extends Mechanic {

    double growChance;
    HashMap<Integer, String> stages = new HashMap<>();
    ItemStack seed;
    List<Material> growMediums = new ArrayList<>();

    public PlantMechanic(MechanicFactory mechanicFactory, ConfigurationSection section) {
        super(mechanicFactory, section);

        if (section.isDouble("grow_chance"))
            this.growChance = section.getDouble("grow_chance");

        if (section.contains("seed_item"))
            parseSeed(section.getConfigurationSection("seed_item"));

        if (section.contains("stages"))
            parseStages(section.getConfigurationSection("stages"));

        if (section.contains("growth_mediums"))
            parseValidGrowMediums(section);
    }

    public ItemStack getSeed() {
        return seed;
    }

    public double getGrowChance() {
        return growChance;
    }

    public HashMap<Integer, String> getStages() {
        return stages;
    }

    public List<Material> getValidGrowMediums() {
        return growMediums;
    }

    private void parseSeed(ConfigurationSection section) {
        if (section.contains("oraxen_item")) {
            this.seed = OraxenItems.getItemById(section.getString("oraxen_item")).build();
        }
    }

    private void parseStages(ConfigurationSection section) {
        for (String stage : section.getKeys(false)) {
            this.stages.put(Integer.valueOf(stage), section.getString(stage + ".oraxen_item"));
        }
    }

    private void parseValidGrowMediums(ConfigurationSection section) {
        if (section.getStringList("growth_mediums").isEmpty()) return;
        for (String medium : section.getStringList("growth_mediums")) {
            if (DEBUG) Bukkit.getLogger().info("Parsing growth medium(" + medium + ") for " + this.getItemID() + ".");
            Material material = Material.getMaterial(medium);
            if (material != null)
                if (material.isBlock()) {
                    growMediums.add(material);
                    continue;
                }
            Bukkit.getLogger().info(medium + " is not a valid growth medium type for " + this.getItemID() + ". Make sure to use valid vanilla block types.");
        }
    }
}