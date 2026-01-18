# Changelog

## 1.5.9 - 2026-01-18
- `Switch Inventory`
  - Changed the position and look of a button in player's inventory. It is now right aligned.
    - For existing configs - `player_button_position_x` option will need to be updated manually to new position (176)
  - Button now properly changes position when recipe book is open.
- Added config option to not require an empty hand when taming a horse. (disabled by default)

## 1.5.8 - 2025-12-21
- [Fabric] Attempt to fix weird crashing with fabric mixin.

## 1.5.7 - 2025-12-16
- [Fabric] Attempt to fix crash with `I'm Fast` again, due to not disabling Horseman's mixin correctly.

## 1.5.6 - 2025-10-16
- Fixed `switch_inventory.enabled` config not disabling the feature when turned off.
- Added missing config translations.
- Added Japanese translation.

## 1.5.5 - 2025-09-24
- Increased 'anchorUpdateThreshold' of a 'less wander' feature, to potentially fix horses sometimes wandering off where they shouldn't.

## 1.5.4 - 2025-09-05
- Camels are now summonable with Copper Horn by default (added to `horseman:summonable` tag).
- Fixed Copper Horn binding not working on Camels even if manually added to `horseman:summonable` tag.
- Bumped version to align with version number of the mod on newer MC versions.

## 1.5.2.1 - 2025-08-20
- Added ex_mx translation (cerealconjugo)

## 1.5.2 - 2025-08-20
- When calling a hitched horse that's in a walking range with a Copper Horn, it will be unhitched and come to the player.
  - Horses leashed the usual way will stay in place as before.
- Added 'copper_horn_error_messages' config option.
- Fixed Look Around not rotating the horse correctly at some negative angles of player Y rotation.

## 1.5.1 - 2025-07-30
- Added Trader Llama to `horseman:cannot_be_hitched` tag.

## 1.5.0 - 2025-07-11
- Horse with Leather Horse Armor can now walk on Powder Snow like a player with Leather Boots.
- Backported Leash knot untying and breaking sounds from a recent minecraft version.
- Dismounting from a horse will now place a player towards their view direction rather than on left/right side of a horse.
- Fixed horse sometimes running back when the player dismounts it.

## 1.4.5 - 2025-06-07
- Potentially fixed crash when world is loading again.

## 1.4.4 - 2025-06-07
- Potentially fixed somewhat rare crash when world is loading.

## 1.4.3.1 - 2025-06-06
- Potentially fixed an error for setting attribute multiple times when two players ride the same horse.
  - Issue comes into play with `Two Players One Horse` (and similar mods).

Hotfix:
  - Fixed an oversight in the above fix.

## 1.4.2 - 2025-06-05
- Fixed Copper Horn recipe being craftable with Create mixing.

## 1.4.1 - 2025-06-03
- Bottom leaves blocks will not have a collision when riding a horse to improve forest traversal.
  - Can be configured to remove collision from all leaves.
- Added tooltip with horse stats when looking at a horse while holding `#minecraft:horse_food` item.

## 1.4.0 - 2025-06-01
- Added Copper Horn.
  - Used to summon a horse to the player.
  - Crafted with Goat Horn and some copper.
- Added advancement related to horse summoning.
- Added `#horseman:forbids_horses` entity tag for boats. 
  - Can be used to configure what type of boat can fit a horse. 
  - Empty by default.

Hotfix: 
- Fixed release version.

## 1.3.10
- Changed some Leash-related code to prevent crashes with some mods (Origins/Apoli).  

## 1.3.9 - 2025-05-16
- [Fabric] Renamed "ServerGamePackedListenerImplMixin" to "ServerGPLImplMixin" to fix a crash with some mods. Yeah... I love Fabric, can't you tell?   

## 1.3.8 - 2025-05-13
- Increased 'fast_step_down' speed from 0.5 to 0.65
- Fixed 'fast_step_down_two_blocks' config option breaking jumps.
 
## 1.3.7 - 2025-05-11
- Fixed Llama carpet not rendering.

## 1.3.6 - 2025-05-02
- Horses will have smaller hitbox when in boat with a player to not interfere with item use (previously you'd always click on a horse). 

## 1.3.5 - 2025-05-01
- 'Fix for horizontal camera lag' now only applies to horse-type mobs. Fixes jittery camera in boats (maybe in other vehicles as well).

## 1.3.4 - 2025-05-01
- Fixed crash with `I'm Fast` mod.

## 1.3.3 - 2025-04-30
- Added a fix for horizontal camera lag when mounted. MC-259512.
- Fixed horse armor weird rendering with Iris.
- Fixed button clicking sound playing when pressing Ctrl+Inv key in player's inventory.

## 1.3.2 - 2025-04-20
- Config screen can now be accessed when ModMenu is installed.
- Renamed config setting 'transparent_horse.max_transparency' to 'transparent_horse.max_opacity'.
  - It now ranges from 0 to 1 instead of 0 to 255.
- Fixed crash with Better Mount HUD. 
  - You'll need to disable 'improved_mount_gui' in horseman-client config if you want to use both mods due to some hud display issues.
- Conflicts with other mods should be slightly less likely now.

## 1.3.0 - 2025-04-18 
- Improved mount GUI - shows player's hunger bar, xp bar and xp levels. Horse jump bar will only show when jumping.
- When riding a horse - jump meter will no longer fill up when mount is in water.
- Using saddle in creative mode on an untamed horse instantly tames it.
- Fixed `#horseman:cannot_be_hitched` tag not working.
- Fixed hitching with block in hand playing block place sound and briefly showing block placed on client.
- Changed config:
  - 'free_camera.angle_threshold' default has been changed from 65 to 100 degrees. 
  - Moved all Common settings to Server. Removed Common config.
  - Some settings were renamed.

**Due to archiving of Horse Buff mod and an agreement with its author, Horseman will now include Horse Buff's features.**

Features ported/adapted from Horse Buff:
- Horses now fit in boats. Their hitbox gets a bit smaller to make breaking the boat easier. 
- Saddled horse now have a limit on how far they can wander when player dismounts it.
- Prevent rearing when riding (no bucking).
- Horses now can swim when ridden by pressing jump key. Allows crossing rivers and lakes on horse. 
- Added button and hotkey to switch between horse and player inventory. 
- Add 10% to horse step-up height to not get stuck on paths.
- Remove block break speed slowdown when mounted.
- Horses become transparent when you look down.
- 'jeb_' horses become the rainbow.

## 1.2.1 - 2025-04-10
- Fixed loading crash with Horse Buff.

## 1.21.1 - 1.2.0 - 2025-03-20
- Ported to 1.21.

## 1.2.0 - 2025-03-19
**This update changes some internal structure of horse hitching. Serious bugs should not happen, 
but if you want to be extra safe - remove Leads from horses before the update.**

- Lead can now be attached by Sneak+Using Lead item on a horse.
- Using shears on a horse with Lead attached will remove the Lead.
- Added `horse_hitch_lead_required` config option. Enabled by default.
  - Allows turning off Lead slot (for compatibility with some mods), but still require Lead to be attached.
  - When `horse_hitch_lead_slot` is turned off, the only way to attach a Lead is to Sneak+Use. Icon will be shown in horse UI when horse has Lead attached. 

## 1.1.4 - 2024-08-31
- [Forge] Added some compatibility with Realistic Horse Genetics. 
  - Hitching Lead slot background is now rendering in their inventories.
  - Shearing to remove chests now works.
  - Other features seem to work fine out of the box.

## 1.1.3 - 2024-08-31
- Horse head while riding will be slightly lower now (both in rotation and position) to reduce view blocking.

## 1.1.2 - 2024-08-18
- [Forge] Fixed startup crash caused by checking if other mod is loaded too early.

## 1.1.1 - 2024-08-18
- [Fabric] Fixed startup crash with `Horse Buff` mod. 

## 1.1.0 - 2024-08-17
- Added ability to remove chests from Mules, Donkeys and Llamas by using shears.
- Horses can now be Quick Hitched when clicked on an existing leash knot. Previously the knot would've been removed.
- Quick Hitching horses when holding a block in hand will no longer show a client-only ghost block for a split second. 

## 1.0.2 - 2024-08-14
- Removed leftover debug message in log 

## 1.0.1 - 2024-08-14
- Fixed startup crash with KubeJS

## 1.0.0 - 2024-08-13
- Release