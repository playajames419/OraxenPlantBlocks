# OraxenPlantBlocks
This is an addon to the Premium plugin [Oraxen](https://github.com/oraxen/Oraxen). It adds the ability to add custom modeled plants with multiple grow stages to your server.

Native Version: 1.17

[Oraxen Plant Demo Video](https://imgur.com/a/g9kffFT)

## Getting Started

### Prerequisites

You must have [Oraxen](https://github.com/oraxen/Oraxen), [OraxenTransparentBlocks](https://github.com/playajames419/OraxenTransparentBlocks), [WorldEdit](https://github.com/EngineHub/WorldEdit), and [WorldGuard](https://github.com/EngineHub/WorldGuard) installed on your server to use this plugin.

### Installing

Place the plugin jar file in your servers plugins directory.

### Adding Transparent Block Mechanic to Oraxen

Add the following to mechanics.yml in your Oraxen plugin folder.

```yaml
plant:
  enabled: true
```

## Configuring Blocks

There are quite a few parts needed to make the plant work correctly. These will all be created within the Oraxen plugins directory, I find it easiest to create a new file in Oraxen/items(eg. example_plant.yml) that will house all the blocks and items for your plant. Reference the example file below, there are a couple sections you will need to create a plant; plant details, plant seed, plant product, and stages.

### Example:

```yaml
###################
#  Plant Details  #
###################
example_plant: # Plant ID, can be must be unique from other oraxen items
  displayname: "Example Plant" # Display name you will see in game
  material: STICK # Default material, players not using the resource pack will see this material
  Mechanics: # Mechanics Section
    plant: # Mechanic type, in this case plant
      grow_chance: 0.50 # Chance plant will grow every tick interval, defined in config.yml
      growth_mediums: # Valid block(s) plant can be planted(placed) on, must be vanilla minecraft block(s).
        - DIRT
        - FARMLAND
      stages: # List as many as you want, in order, starting with 0
        0: # Stage number
          oraxen_item: example_plant_stage_0 # Item for each stage, must be oraxen transparent block type
        1:
          oraxen_item: example_plant_stage_1
        2:
          oraxen_item: example_plant_stage_2
        3:
          oraxen_item: example_plant_stage_3
      seed_item: # The plants seed item
        oraxen_item: example_seeds

## Plant Product Details ##
example:
  displayname: "example"
  material: STICK
  Pack:
    generate_model: true
    parent_model: "item/handheld"
    textures:
      - example.png
  
## Seed Details ##
# Must be a transparent_block mechanic type
example_seeds:
  displayname: "example Seeds"
  material: STICK
  Pack:
    generate_model: true
    parent_model: "item/handheld"
    textures:
      - example_seeds.png
  Mechanics:
    transparent_block:
      ## Set armorstand paramaters here, it will persist through all stages of growth, this section is not nessicery in grow stage item(s) details.
      armorstand_visible: false
      armorstand_small: true
      block_gravity: false

## Growth Stage Details ## 
# Should be of block or transparent_block mechanic type
example_plant_stage_0:
  displayname: "Example Plant Stage 0"
  material: STICK
  Pack:
    generate_model: false
    model: example_plant_stage0
  Mechanics:
    transparent_block:
      ## Armorstand parameters not required here only on your seed item.
      drop: # Drop section
        loots:
          - {oraxen_item: example_seeds, probability: 0.5, max_amount: 1} # Drops when broken, list as many as you'd like
          - {oraxen_item: example_seeds, probability: 0.5, max_amount: 1} # Another drop

## Another growth stage
example_plant_stage_1:
  displayname: "Example Plant Stage 1"
  material: STICK
  Pack:
    generate_model: false
    model: example_plant_stage1
  Mechanics:
    transparent_block:
      drop:
        loots:
          - {oraxen_item: example_seeds, probability: 1.0, max_amount: 1}

## Another growth stage, have as many of these as you want and define them in the stages section of the plant details
example_plant_stage_2:
  displayname: "Example Plant Stage 2"
  material: STICK
  Pack:
    generate_model: false
    model: example_plant_stage2
  Mechanics:
    transparent_block:
      drop:
        loots:
          - {oraxen_item: example_seeds, probability: 1.0, max_amount: 2}

example_plant_stage_3:
  displayname: "Example Plant Stage 3"
  material: STICK
  Pack:
    generate_model: false
    model: example_plant_stage3
  Mechanics:
    transparent_block:
      drop:
        loots:
          - {oraxen_item: example, probability: 1.0, max_amount: 10}
```

## Block Models

When creating custom models to use with this plugin, you will need to modify the armorstand's helmet display settings to your likings. This is how the block will show in-game.

## License

This project is licensed under the GNU GPLv3 License
