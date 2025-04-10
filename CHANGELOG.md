# Changelog

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