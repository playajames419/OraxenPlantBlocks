package me.playajames.oraxenplantblocks.OraxenMechanics;

import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import org.bukkit.configuration.ConfigurationSection;

public class PlantMechanicFactory extends MechanicFactory {

    public PlantMechanicFactory(ConfigurationSection section) {
        super(section);

    }

    @Override
    public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
        Mechanic mechanic = new PlantMechanic(this, itemMechanicConfiguration);
        addToImplemented(mechanic);
        return mechanic;
    }
}
