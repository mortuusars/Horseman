# Changelog

## 1.3.0 - 2025-04-18
- Improved mount gui - shows player's hunger bar, xp bar and xp levels. Horse jump bar will only show when jumping.
- When riding a horse - jump meter will no longer fill up when mount is in water.
- Using saddle in creative mode on an untamed horse instantly tames it.
- Fixed hitching with block in hand playing block place sound and briefly showing block placed on client.
- Changed config:
  - 'free_camera.angle_threshold' default has been changed from 65 to 100 degrees.
  - Some settings were renamed.

**Due to archiving of the Horse Buff mod and an agreement with its author, Horseman will now include Horse Buff's features.**

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