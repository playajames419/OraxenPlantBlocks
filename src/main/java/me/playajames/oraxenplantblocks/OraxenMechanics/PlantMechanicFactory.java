package me.playajames.oraxenplantblocks.OraxenMechanics;

import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import me.playajames.oraxenplantblocks.Listeners.PlantBreakListener;
import me.playajames.oraxenplantblocks.Listeners.PlantInteractListener;
import me.playajames.oraxenplantblocks.Listeners.PlantPlaceListener;
import org.bukkit.configuration.ConfigurationSection;

public class PlantMechanicFactory extends MechanicFactory {

    public PlantMechanicFactory(ConfigurationSection section) {
        super(section);
        MechanicsManager.registerListeners(OraxenPlugin.get(), new PlantPlaceListener());
        MechanicsManager.registerListeners(OraxenPlugin.get(), new PlantBreakListener());
        MechanicsManager.registerListeners(OraxenPlugin.get(), new PlantInteractListener());
    }

    @Override
    public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
        Mechanic mechanic = new PlantMechanic(this, itemMechanicConfiguration);
        addToImplemented(mechanic);
        return mechanic;
    }
}
