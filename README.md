# PleasureCraft

**PleasureCraft** is an adult-themed mod for **Minecraft 1.21+** built using the **Fabric Mod Loader**. It’s a full remake and modern reimagining of the old 1.12.2 **FapCraft** mod made by *Schnurri_tv*, rebuilt from the ground up to support current versions of Minecraft using modern tools and practices.

**Join the Discord Server**
https://discord.gg/bjRXDZU3Xa

---

## Current Features

### Modular Girl Entities
- Lucy and Bia are currently implemented, with a modular system allowing more girls to be added easily.
- Girls are **tameable** with specific items and **only respond to their owner**.
- Shift right-clicking opens a custom **owner-only GUI** with:
    - Interaction buttons (`Sit`, `Follow`, `Talk`, `Strip/Dress`, etc.)
    - 4x3 **inventory** 
    - Option to set a **home position** as a respawn point.

### Scene System
- Starting a **"Talk"** interaction triggers an **interactive sex scene system**:
    - Player **rides** the girl entity with custom positioning.
    - Player is **invisible** and placed at a **specific model bone** (e.g., `"hips"`).
    - Scene progress will eventually include **keybind input**, **progress bars**, and **transitions** (in development).

---

## Roadmap

### Completed
- [x] Multiple girl entities with modular code
- [x] GUI with custom inventory and buttons
- [x] Strip/Dress-up toggle with model bone visibility
- [x] Freeze/Unfreeze system (prevents movement during scenes)
- [x] Custom passenger position via model bone
- [x] Configurable jiggle physics system
- [x] Scene camera that follows animation bone
- [x] Full scene interaction system with progress and climax stages
- [x] Keybinds for advancing and exiting scenes
- [x] Player model integration with girl's scene pose
- [x] Armor and equipment rendering

### Planned
- [ ] Better AI (pathfinding, behavior trees, etc.)
- [ ] JSON-driven entity definition (animations, stats, behavior)

---

## Frequently Asked Questions

➞ **How do I interact with the girls**
> Before you can do anything with the girls you need to tame and improve your relationship with them by using the corresponding flower
> 
> **Momo**: Poppy
>
> **Lucy**: Allium
>
> **Mika**: Open Eyeblossoms
> 
> Then just right click on the girl to open up the entity GUI.
> To start a scene just level them up to at least level 4 then click the talk button. which then opens up all the possible scene interactions 

➞ **Can you add "x" character**
> Maybe, if you have a model and animations that you made or got permission to use then i could add said character later down the line

➞ **How do I thrust and cum when having sex with the girls**
> The default button for thrusting is "Z" and the default button for cumming is "V" but they can be changed in the change keybind screen

---

## Requirements

- [Fabric Loader](https://fabricmc.net/)
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [GeckoLib](https://modrinth.com/mod/geckolib)
- [Cloth Config](https://modrinth.com/mod/cloth-config)
- [Mod Menu (Optional)](https://modrinth.com/mod/modmenu)
- Minecraft **1.21+** _(just check in with the faq on the discord)_

---

## Disclaimer

This mod contains **adult content** and is intended for **mature audiences only**.  
Use responsibly and respect Minecraft’s community guidelines, platform policies, and age requirements.

> PleasureCraft is **heavily inspired** by SexCraft+ (by VyP3X) and FapCraft (by Schnurri_tv), but is an **independent project** with original implementation and goals.






[//]: # (## 4. Girl Property Reference)

[//]: # ()
[//]: # (### id)

[//]: # (Unique internal name. Must match the filenames of your model, animations and texture.)

[//]: # ()
[//]: # (### name)

[//]: # (Display name shown in-game.)

[//]: # ()
[//]: # (### gui_size)

[//]: # (Size of the girl in the GUI window)

[//]: # ()
[//]: # (### gui_y_offset)

[//]: # (Vertical offset in the GUI for centering.)

[//]: # ()
[//]: # (### tame_item)

[//]: # (Minecraft item ID used to tame the girl.)

[//]: # ()
[//]: # (### Attributes)

[//]: # ()
[//]: # (- health – Maximum HP.)

[//]: # ()
[//]: # (- speed – Movement speed.)

[//]: # ()
[//]: # (- damage – Base melee damage dealt.)

[//]: # ()
[//]: # (## 5. Scene Field Reference)

[//]: # (Each entry in "scenes" describes a possible scene or interaction.)

[//]: # ()
[//]: # (### name)

[//]: # ()
[//]: # (The name of the scene. This is displayed on the GUI.)

[//]: # ()
[//]: # (### required_level)

[//]: # ()
[//]: # (The minimum relationship level required to unlock this scene.)

[//]: # ()
[//]: # (### intro_anim)

[//]: # ()
[//]: # (List of animations played at the start of the scene.)

[//]: # ()
[//]: # (### slow_anim)

[//]: # ()
[//]: # (List of looping animations for the slow phase.)

[//]: # ()
[//]: # (### fast_anim)

[//]: # ()
[//]: # (List of looping animations for the fast phase.)

[//]: # ()
[//]: # (### cum_anim)

[//]: # ()
[//]: # (The final animation that plays when the cum threshold is reached.)

[//]: # ()
[//]: # (### cum_threshold)

[//]: # ()
[//]: # (A numeric value determining how much progress is required before the cum animation triggers.)

[//]: # ()
[//]: # (### needs_to_strip)

[//]: # ()
[//]: # (Whether the girl must be naked before this scene can start.)

[//]: # ()
[//]: # (### is_bed_scene)

[//]: # ()
[//]: # (Marks the scene as one that happens on a bed. Adjusts positioning and animation accordingly.)

[//]: # ()
[//]: # (### bed_offset )

[//]: # ()
[//]: # (Vertical offset used to correctly align the girl model on the bed. &#40;Only if is_bed_scene is true&#41;)

[//]: # ()
[//]: # (### lay_on_bed_anim)

[//]: # ()
[//]: # (The animation that plays when the girl first lies on the bed. &#40;Only if is_bed_scene is true&#41;)

[//]: # ()
[//]: # (### bed_idle_anim)

[//]: # ()
[//]: # (The looping animation that plays while the girl is idle on the bed. &#40;Only if is_bed_scene is true&#41;)
