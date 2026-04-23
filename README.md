# CIT Mod for 1.21.4 (Fabric)

This mod allows players to change item textures/models using resource packs, similar to Optifine's CIT.

## Resource Pack Structure
Place your files in: `assets/minecraft/optifine/cit/`

### Example `.properties` file
**File:** `assets/minecraft/optifine/cit/cool_sword.properties`
```properties
type=item
items=diamond_sword
model=cit-mod:item/cool_sword
nbt.display.Name=pattern:Cool Sword
```

### Example Model file
**File:** `assets/cit-mod/models/item/cool_sword.json`
(You can also use `minecraft` namespace if you prefer)

## Features
- Match items by ID.
- Match items by Display Name (Literal, Pattern, Regex).
- Custom models (`.json`).
- Weight support for rule priority.

## Development
Built for Minecraft 1.21.4 using Fabric Loom.
