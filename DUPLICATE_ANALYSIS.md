# Duplicate Resources Analysis

## Duplicates Found in colors.xml

### Analysis:
After thorough examination of the colors.xml files, the following duplicates/conflicts were identified:

1. **bg_parchment** - Appears in two locations:
   - Line 51: `<color name="bg_parchment_deep">#EDE4D6</color>` (SIMILAR NAME but different value)
   - Line 104: `<color name="bg_parchment">#f6e7d6</color>` (EXACT NAME, this is the main definition)

2. **text_caramel** - Appears multiple times:
   - Line 62: `<color name="text_caramel_light">#C49A6C</color>` (SIMILAR)
   - Line 111: `<color name="text_caramel">#C8A882</color>` (MAIN)

3. **chip_bg_orange** - Line 60 ONLY

4. **bhava_background** - Line 137
   - Same value as bg_parchment (#f6e7d6)

5. **Similar/Related Colors** (Not exact duplicates but very similar):
   - bg_parchment (#f6e7d6)
   - bg_parchment_deep (#EDE4D6)
   - bhava_background (#f6e7d6) <- DUPLICATE VALUE

6. **Live Indicator Colors**:
   - Line 18: live_green (#4ADE80)
   - Line 189: live_dot_green (#4CAF50) <- DIFFERENT GREEN, likely a duplicate intent
   - colors_home.xml: live_indicator (#FF4444) <- DIFFERENT PURPOSE but similar naming

7. **Player/Mini Player Colors**:
   - mini_player_text_muted (appears once)
   - mini_player_bg (appears once)

## Resolution Strategy

1. **Consolidate Color Names**: Remove similar/duplicate names and keep a single source
2. **Standardize Naming**: Use consistent naming conventions
3. **Merge color files**: Optionally consolidate colors_home.xml into colors.xml
4. **Remove unused colors**: Clean up orphaned definitions

## Recommended Actions:
1. Remove or rename `bhava_background` to avoid duplicate value with `bg_parchment`
2. Remove `text_caramel_light` if unused and consolidate into `text_caramel`
3. Consolidate `live_green` and `live_dot_green` to single definition
4. Add `colors_home.xml` colors to main colors.xml

