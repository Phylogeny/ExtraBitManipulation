package com.phylogeny.extrabitmanipulation.config;

import java.io.File;
import java.util.Arrays;

import org.apache.logging.log4j.Level;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.phylogeny.extrabitmanipulation.helper.BitIOHelper;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingTool;
import com.phylogeny.extrabitmanipulation.reference.ChiselsAndBitsReferences;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.Reference;
import com.phylogeny.extrabitmanipulation.shape.Shape;

public class ConfigHandlerExtraBitManipulation
{
	public static Configuration configFileClient, configFileServer, configFileCommon, modelingMapConfigFile, sculptingMapConfigFile;
	public static final String SCULPTING_WRENCH_SETTINGS = "Sculpting & Wrech Settings";
	public static final String UNCHISELABLE_BLOCK_STATES = "Unchiselable Block States";
	public static final String INSUFFICIENT_BITS = "Insufficient Bits";
	public static final String MODELING_TOOL_SETTINGS = "Modeling Tool Settings";
	public static final String MODELING_TOOL_MANUAL_MAPPINGS = "Modeling Tool Manual Mappings";
	public static final String BLOCK_TO_BIT_MAP = "Block To Bit Map";
	public static final String STATE_TO_BIT_MAP = "State To Bit Map";
	public static final String DATA_CATAGORY_SCULPT = "Sculpting Tool";
	public static final String DATA_CATAGORY_MODEL = "Modeling Tool";
	public static final String BIT_TOOL_DEFAULT_VALUES = "Default Values";
	public static final String BIT_TOOL_PER_TOOL_OR_PER_CLIENT = "Per Tool or Per Client";
	public static final String BIT_TOOL_DISPLAY_IN_CHAT = "Display In Chat";
	public static final String RENDER_OVERLAYS = "Bit Wrench Overlays";
	public static final String RECIPES_DISABLE = "Recipe Disable";
	private static final String[] COLOR_NAMES = new String[]{"Red", "Green", "Blue"};
	public static final String[] BLOCK_TO_BIT_MAP_DEFAULT_VALUES = new String[]
	{
		ChiselsAndBitsReferences.MOD_ID + ":bittank-minecraft:log",
		"minecraft:acacia_door-minecraft:planks:4",
		"minecraft:acacia_fence_gate-minecraft:planks:4",
		"minecraft:acacia_fence-minecraft:planks:4",
		"minecraft:acacia_stairs-minecraft:planks:4",
		"minecraft:activator_rail-minecraft:redstone_block",
		"minecraft:anvil-minecraft:wool:7",
		"minecraft:barrier-minecraft:air",
		"minecraft:beacon-minecraft:diamond_block",
		"minecraft:bed-minecraft:wool:14",
		"minecraft:bedrock-minecraft:cobblestone",
		"minecraft:birch_door-minecraft:planks:2",
		"minecraft:birch_fence_gate-minecraft:planks:2",
		"minecraft:birch_fence-minecraft:planks:2",
		"minecraft:birch_stairs-minecraft:planks:2",
		"minecraft:brewing_stand-minecraft:wool:7",
		"minecraft:brick_block-minecraft:stained_hardened_clay:6",
		"minecraft:brick_stairs-minecraft:stained_hardened_clay:6",
		"minecraft:brown_mushroom-minecraft:brown_mushroom_block:0",
		"minecraft:cactus-minecraft:wool:13",
		"minecraft:cake-minecraft:stained_hardened_clay:1",
		"minecraft:carpet-minecraft:air",
		"minecraft:carrots-minecraft:wool:5",
		"minecraft:cauldron-minecraft:wool:7",
		"minecraft:chest-minecraft:stained_hardened_clay:4",
		"minecraft:coal_ore-minecraft:coal_block",
		"minecraft:cobblestone_wall-minecraft:cobblestone",
		"minecraft:cocoa-minecraft:stained_hardened_clay:5",
		"minecraft:command_block-minecraft:hardened_clay",
		"minecraft:crafting_table-minecraft:planks:3",
		"minecraft:dark_oak_door-minecraft:planks:5",
		"minecraft:dark_oak_fence_gate-minecraft:planks:5",
		"minecraft:dark_oak_fence-minecraft:planks:5",
		"minecraft:dark_oak_stairs-minecraft:planks:5",
		"minecraft:daylight_detector_inverted-minecraft:planks:5",
		"minecraft:daylight_detector-minecraft:planks:5",
		"minecraft:deadbush-minecraft:air",
		"minecraft:detector_rail-minecraft:redstone_block",
		"minecraft:diamond_ore-minecraft:diamond_block",
		"minecraft:dispenser-minecraft:wool:7",
		"minecraft:double_plant-minecraft:air",
		"minecraft:double_stone_slab2-minecraft:red_sandstone",
		"minecraft:double_stone_slab-minecraft:stonebrick",
		"minecraft:double_wooden_slab-minecraft:planks",
		"minecraft:dragon_egg-minecraft:obsidian",
		"minecraft:dropper-minecraft:wool:7",
		"minecraft:emerald_ore-minecraft:emerald_block",
		"minecraft:enchanting_table-minecraft:obsidian",
		"minecraft:end_portal_frame-minecraft:end_stone",
		"minecraft:end_portal-minecraft:obsidian",
		"minecraft:ender_chest-minecraft:obsidian",
		"minecraft:farmland-minecraft:dirt",
		"minecraft:fence_gate-minecraft:planks",
		"minecraft:fence-minecraft:planks",
		"minecraft:fire-minecraft:air",
		"minecraft:flower_pot-minecraft:hardened_clay",
		"minecraft:flowing_lava-minecraft:lava",
		"minecraft:flowing_water-minecraft:water",
		"minecraft:furnace-minecraft:wool:7",
		"minecraft:glass_pane-minecraft:air",
		"minecraft:glass-minecraft:air",
		"minecraft:gold_ore-minecraft:gold_block",
		"minecraft:golden_rail-minecraft:gold_block",
		"minecraft:hay_block-minecraft:stained_hardened_clay:4",
		"minecraft:heavy_weighted_pressure_plate-minecraft:air",
		"minecraft:hopper-minecraft:wool:7",
		"minecraft:iron_bars-minecraft:air",
		"minecraft:iron_door-minecraft:iron_block",
		"minecraft:iron_ore-minecraft:stained_hardened_clay",
		"minecraft:iron_trapdoor-minecraft:iron_block",
		"minecraft:jukebox-minecraft:hardened_clay",
		"minecraft:jungle_door-minecraft:planks:3",
		"minecraft:jungle_fence_gate-minecraft:planks:3",
		"minecraft:jungle_fence-minecraft:planks:3",
		"minecraft:jungle_stairs-minecraft:planks:3",
		"minecraft:ladder-minecraft:air",
		"minecraft:lapis_ore-minecraft:lapis_block",
		"minecraft:leaves2-minecraft:wool:13",
		"minecraft:leaves-minecraft:wool:13",
		"minecraft:lever-minecraft:stone",
		"minecraft:light_weighted_pressure_plate-minecraft:air",
		"minecraft:lit_furnace-minecraft:wool:7",
		"minecraft:lit_pumpkin-minecraft:wool:1",
		"minecraft:lit_redstone_lamp-minecraft:stained_hardened_clay:12",
		"minecraft:lit_redstone_ore-minecraft:redstone_block",
		"minecraft:melon_block-minecraft:wool:4",
		"minecraft:melon_stem-minecraft:wool:5",
		"minecraft:mob_spawner-minecraft:stained_hardened_clay:9",
		"minecraft:mossy_cobblestone-minecraft:wool:13",
		"minecraft:mycelium-minecraft:stained_hardened_clay:3",
		"minecraft:nether_brick_fence-minecraft:nether_brick",
		"minecraft:nether_brick_stairs-minecraft:nether_brick",
		"minecraft:nether_wart-minecraft:wool:14",
		"minecraft:noteblock-minecraft:hardened_clay",
		"minecraft:oak_stairs-minecraft:planks",
		"minecraft:piston_extension-minecraft:planks",
		"minecraft:piston_head-minecraft:planks",
		"minecraft:piston-minecraft:wool:7",
		"minecraft:portal-minecraft:stained_glass:10",
		"minecraft:potatoes-minecraft:wool:5",
		"minecraft:powered_comparator-minecraft:stonebrick",
		"minecraft:powered_repeater-minecraft:stonebrick",
		"minecraft:pumpkin_stem-minecraft:wool:5",
		"minecraft:pumpkin-minecraft:wool:1",
		"minecraft:quartz_ore-minecraft:quartz_block",
		"minecraft:quartz_stairs-minecraft:quartz_block",
		"minecraft:rail-minecraft:iron_block",
		"minecraft:red_flower-minecraft:wool:14",
		"minecraft:red_mushroom_block-minecraft:redstone_block",
		"minecraft:red_mushroom-minecraft:wool:14",
		"minecraft:red_sandstone_stairs-minecraft:red_sandstone",
		"minecraft:redstone_lamp-minecraft:stained_hardened_clay:12",
		"minecraft:redstone_ore-minecraft:redstone_block",
		"minecraft:redstone_torch-minecraft:redstone_block",
		"minecraft:redstone_wire-minecraft:redstone_block",
		"minecraft:reeds-minecraft:stained_hardened_clay:5",
		"minecraft:sandstone_stairs-minecraft:sandstone",
		"minecraft:sapling-minecraft:wool:13",
		"minecraft:sea_lantern-minecraft:quartz_block",
		"minecraft:skull-minecraft:air",
		"minecraft:snow_layer-minecraft:air",
		"minecraft:soul_sand-minecraft:wool:12",
		"minecraft:sponge-minecraft:wool:4",
		"minecraft:spruce_door-minecraft:planks:1",
		"minecraft:spruce_fence_gate-minecraft:planks:1",
		"minecraft:spruce_fence-minecraft:planks:1",
		"minecraft:spruce_stairs-minecraft:planks:1",
		"minecraft:stained_glass_pane-minecraft:air",
		"minecraft:standing_banner-minecraft:planks",
		"minecraft:standing_sign-minecraft:planks",
		"minecraft:sticky_piston-minecraft:wool:7",
		"minecraft:stone_brick_stairs-minecraft:stonebrick",
		"minecraft:stone_button-minecraft:stone",
		"minecraft:stone_pressure_plate-minecraft:air",
		"minecraft:stone_slab2-minecraft:red_sandstone",
		"minecraft:stone_slab-minecraft:stonebrick",
		"minecraft:stone_stairs-minecraft:cobblestone",
		"minecraft:tallgrass-minecraft:air",
		"minecraft:tnt-minecraft:wool:14",
		"minecraft:torch-minecraft:glowstone",
		"minecraft:trapdoor-minecraft:planks",
		"minecraft:trapped_chest-minecraft:stained_hardened_clay:4",
		"minecraft:tripwire_hook-minecraft:planks",
		"minecraft:tripwire-minecraft:wool",
		"minecraft:unlit_redstone_torch-minecraft:redstone_block",
		"minecraft:unpowered_comparator-minecraft:stonebrick",
		"minecraft:unpowered_repeater-minecraft:stonebrick",
		"minecraft:vine-minecraft:wool:13",
		"minecraft:wall_banner-minecraft:planks",
		"minecraft:wall_sign-minecraft:planks",
		"minecraft:waterlily-minecraft:stained_hardened_clay:5",
		"minecraft:web-minecraft:wool",
		"minecraft:wheat-minecraft:wool:5",
		"minecraft:wooden_button-minecraft:planks",
		"minecraft:wooden_door-minecraft:planks",
		"minecraft:wooden_pressure_plate-minecraft:air",
		"minecraft:wooden_slab-minecraft:planks",
		"minecraft:yellow_flower-minecraft:wool:4"
	};
	public static final String[] STATE_TO_BIT_MAP_DEFAULT_VALUES = new String[]
	{
		"minecraft:cobblestone_wall:1-minecraft:mossy_cobblestone",
		"minecraft:cocoa:10-minecraft:stained_hardened_clay:1",
		"minecraft:cocoa:11-minecraft:stained_hardened_clay:1",
		"minecraft:cocoa:4-minecraft:hardened_clay",
		"minecraft:cocoa:5-minecraft:hardened_clay",
		"minecraft:cocoa:6-minecraft:hardened_clay",
		"minecraft:cocoa:7-minecraft:hardened_clay",
		"minecraft:cocoa:8-minecraft:stained_hardened_clay:1",
		"minecraft:cocoa:9-minecraft:stained_hardened_clay:1",
		"minecraft:double_stone_slab:10-minecraft:planks",
		"minecraft:double_stone_slab:11-minecraft:cobblestone",
		"minecraft:double_stone_slab:12-minecraft:stained_hardened_clay:6",
		"minecraft:double_stone_slab:14-minecraft:nether_brick",
		"minecraft:double_stone_slab:15-minecraft:quartz_block",
		"minecraft:double_stone_slab:1-minecraft:sandstone",
		"minecraft:double_stone_slab:2-minecraft:planks",
		"minecraft:double_stone_slab:3-minecraft:cobblestone",
		"minecraft:double_stone_slab:4-minecraft:stained_hardened_clay:6",
		"minecraft:double_stone_slab:6-minecraft:nether_brick",
		"minecraft:double_stone_slab:7-minecraft:quartz_block",
		"minecraft:double_stone_slab:9-minecraft:sandstone",
		"minecraft:double_wooden_slab:1-minecraft:planks:1",
		"minecraft:double_wooden_slab:2-minecraft:planks:2",
		"minecraft:double_wooden_slab:3-minecraft:planks:3",
		"minecraft:double_wooden_slab:4-minecraft:planks:4",
		"minecraft:double_wooden_slab:5-minecraft:planks:5",
		"minecraft:red_flower:1-minecraft:wool:3",
		"minecraft:red_flower:2-minecraft:wool:10",
		"minecraft:red_flower:3-minecraft:snow",
		"minecraft:red_flower:4-minecraft:wool:14",
		"minecraft:red_flower:5-minecraft:wool:1",
		"minecraft:red_flower:6-minecraft:snow",
		"minecraft:red_flower:7-minecraft:wool:6",
		"minecraft:red_flower:8-minecraft:snow",
		"minecraft:stone_slab:10-minecraft:planks",
		"minecraft:stone_slab:11-minecraft:cobblestone",
		"minecraft:stone_slab:12-minecraft:stained_hardened_clay:6",
		"minecraft:stone_slab:14-minecraft:nether_brick",
		"minecraft:stone_slab:15-minecraft:quartz_block",
		"minecraft:stone_slab:1-minecraft:sandstone",
		"minecraft:stone_slab:2-minecraft:planks",
		"minecraft:stone_slab:3-minecraft:cobblestone",
		"minecraft:stone_slab:4-minecraft:stained_hardened_clay:6",
		"minecraft:stone_slab:6-minecraft:nether_brick",
		"minecraft:stone_slab:7-minecraft:quartz_block",
		"minecraft:stone_slab:9-minecraft:sandstone",
		"minecraft:wheat:7-minecraft:melon_block",
		"minecraft:wooden_slab:10-minecraft:planks:2",
		"minecraft:wooden_slab:11-minecraft:planks:3",
		"minecraft:wooden_slab:12-minecraft:planks:4",
		"minecraft:wooden_slab:13-minecraft:planks:5",
		"minecraft:wooden_slab:1-minecraft:planks:1",
		"minecraft:wooden_slab:2-minecraft:planks:2",
		"minecraft:wooden_slab:3-minecraft:planks:3",
		"minecraft:wooden_slab:4-minecraft:planks:4",
		"minecraft:wooden_slab:5-minecraft:planks:5",
		"minecraft:wooden_slab:9-minecraft:planks:1"
	};
	
	public static void setUpConfigs(File configDir)
	{
		configFileClient = getConfigFile(configDir, "client");
		configFileServer = getConfigFile(configDir, "server");
		configFileCommon = getConfigFile(configDir, "common");
		modelingMapConfigFile = getConfigFile(configDir, "modeling_data");
		sculptingMapConfigFile = getConfigFile(configDir, "sculpting_data");
		updateConfigs();
	}
	
	private static Configuration getConfigFile(File configDir, String suffix)
	{
		return new Configuration(new File(configDir.getAbsolutePath() + "/" + Reference.MOD_ID, suffix + ".cfg"));
	}
	
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		if (event.modID.equalsIgnoreCase(Reference.MOD_ID))
		{
			updateConfigs();
		}
	}
	
	private static void updateConfigs()
	{
		try
		{
			//MODELING TOOL MAPs
			String toolTip = "This is a list of entries of mappings of @@@ to bits for the Modeling Tool";
			Configs.modelBlockToBitMapEntryStrings = modelingMapConfigFile.getStringList(BLOCK_TO_BIT_MAP, MODELING_TOOL_MANUAL_MAPPINGS,
					BLOCK_TO_BIT_MAP_DEFAULT_VALUES, toolTip.replace("@@@", "block states"));
			
			Configs.modelStateToBitMapEntryStrings = modelingMapConfigFile.getStringList(STATE_TO_BIT_MAP, MODELING_TOOL_MANUAL_MAPPINGS,
					STATE_TO_BIT_MAP_DEFAULT_VALUES, toolTip.replace("@@@", "states"));
			
			//SCULPTING SETTINGS
			Configs.maxSemiDiameter = configFileClient.getInt("Max Semi-Diameter", SCULPTING_WRENCH_SETTINGS, 32, 1, Integer.MAX_VALUE,
					"the maximum size (in bits) of sculpting shape semi-diameter (i.e. radius if it is a sphere). (default = 5 bits)");
			
			Configs.maxWallThickness = configFileClient.getInt("Max Wall Thickness", SCULPTING_WRENCH_SETTINGS, 32, 1, Integer.MAX_VALUE,
					"the maximum size (in bits) of hollow sculpting shapes. (default = 2 bits)");
			
			Configs.displayNameDiameter = configFileClient.getBoolean("Display Name Diameter", SCULPTING_WRENCH_SETTINGS, true, 
					"If set to true, sculpting tool display names will indicate the diameter of their bit removal/addition areas. " +
					"If set to false, they will indicate the radius (default = true)");
			
			Configs.displayNameUseMeterUnits = configFileClient.getBoolean("Display Name Meter Units", SCULPTING_WRENCH_SETTINGS, false, 
					"If set to true, sculpting tool display names will indicate the size of their bit removal/addition areas in meters. " +
					"If set to false, they will be in bits (default = false)");
			
			Configs.semiDiameterPadding = configFileClient.getFloat("Semi-Diameter Padding", SCULPTING_WRENCH_SETTINGS, 0.2F, 0, 1, 
					"Distance (in bits) to add to the semi-diameter of a sculpting tool's bit removal/addition area shape. If set to zero, no padding " +
					"will be added; spheres, for example, will have single bits protruding from each cardinal direction at any size, since only those " +
					"bits of those layers will be exactly on the sphere's perimeter. If set to 1, there will effectively be no padding for the same reason, " +
					"but the radius will be one bit larger than specified. A value between 0 and 1 is suggested. (default = 0.2 bits)");
			
			Configs.placeBitsInInventory = configFileServer.getBoolean("Place Bits In Inventory", SCULPTING_WRENCH_SETTINGS, true, 
					"If set to true, when bits are removed from blocks with a sculpting tool, as many of them will be given to the player as is possible. " +
					"Any bits that cannot fit in the player's inventory will be spawned in the world. If set to false, no attempt will be made to give them " +
					"to the player; they will always be spawned in the world. (default = true)");
			
			Configs.dropBitsInBlockspace = configFileServer.getBoolean("Drop Bits In Block Space", SCULPTING_WRENCH_SETTINGS, true, 
					"If set to true, when bits removed from blocks with a sculpting tool are spawned in the world, they will be spawned at a random " +
					"point within the area that intersects the block space and the removal area bounding box (if 'Drop Bits Per Block' is true, they " +
					"will be spawned in the block they are removed from; otherwise they will be spawned at the block they player right-clicked). " +
					"If set to false, they will be spawned at the player, in the same way that items are spawned when throwing them on the ground " +
					"by pressing Q. (default = true)");
			
			Configs.bitSpawnBoxContraction = configFileServer.getFloat("Bit Spawn Box Contraction", SCULPTING_WRENCH_SETTINGS, 0.25F, 0, 0.5F, 
					"Amount in meters to contract the box that removed bits randomly spawn in (assuming they spawn in the block space as per 'Drop Bits " +
					"In Block Space') If set to 0, there will be no contraction and they will be able to spawn anywhere in the box. If set to 0.5, the " +
					"box will contract by half in all directions down to a point in the center of the original box and they will always spawn from that " +
					"central point. The default of 0.25 (which is the default behavior when spawning items with Block.spawnAsEntity) contracts the box " +
					"to half its original size. (default = 0.25 meters)");
			
			Configs.dropBitsPerBlock = configFileServer.getBoolean("Drop Bits Per Block", SCULPTING_WRENCH_SETTINGS, true, 
					"When bits are removed from blocks with a sculpting tool, all the removed bits of each type are counted and a collection of item " +
					"stacks are created of each item. For the sake of efficiency, the number of stacks generated is the minimum number necessary for " +
					"that amount (Ex: 179 bits would become 2 stacks of 64 and 1 stack of 51). If this config is set to true, the counts for each block " +
					"will be added up and spawned after each block is modified. This means that when removing bits in global mode, the bits have the " +
					"ability to spawn in the respective block spaces they are removed from. However, it also means that more stacks may be generated " +
					"than necessary if all bits from all blocks removed were to be pooled. If this config is set to false, the bits will be added up " +
					"and pooled together as they are removed from each block. Only once all blocks are modified will the entire collection of bits be " +
					"spawned in the world or given to the player. While this is more efficient, it means that the effect of bits spawning in the block " +
					"spaces they are removed from is not possible. Rather, the bits will either spawn in the space of the block clicked or spawn at " +
					"the player as per 'Drop Bits In Block Space'. (default = true)");
			
			Configs.dropBitsAsFullChiseledBlocks = configFileServer.getBoolean("Drop Bits As Full Chiseled Blocks", SCULPTING_WRENCH_SETTINGS, false, 
					"If set to true, full meter cubed blocks of bits that have all their bits removed will drop as full chiseled blocks. " +
					"If set to false, they will drop normally as item stacks of bits (64 stacks of size 64). (default = false)");
			
			Configs.oneBitTypeInversionRequirement = configFileClient.getBoolean("One Bit Type Inversion Requirement", SCULPTING_WRENCH_SETTINGS, false, 
					"If set to true, the Bit Wrench will only be able to invert blocks that are comprised of one bit type. " +
					"If set to false, any block can be inverted and the bit type that empty bits will be filled with is whichever bit is " +
					"the most prevalent in the block space. (default = false)");
			
			//MODELING TOOL SETTINGS
			Configs.saveStatesById = configFileClient.getBoolean("Save States By ID", MODELING_TOOL_SETTINGS, false,
					"If set to true, and if the 'per tool' box is checked in a given Modeling Tool (causing mappings to be read/written from/to the " +
					"NBT tag of the itemstack), manually mapped blocks and block states will be saved to the itemstack's NBT as state IDs (integers - " +
					"4 bytes each). If set to false, they will be saved as a registry name (2 strings - 1 byte per char) and metadata (1 byte). Saving " +
					"states as registry name for blocks and as registry name and metadata for block states takes up several times more space, and if " +
					"thousands of mappings are manually stored in an item (as unlikely as that is), client or server crashing may occur if that item " +
					"is sent through a packet network since the maximum payload is 32767 bytes. The benefit is that if the item is transported across " +
					"worlds, the states will remain consistent. Saving states as integers, however, takes several times less space (meaning that 10k+ " +
					"mappings would be necessary to cause a crash, which is even less likely), but the state IDs may not remain consistent if the item is " +
					"transported across worlds. Changing this config will not cause any previously mapped states to be lost; all saved mappings " +
					"will simply be read and saved in the new format the next time a state is manually mapped or unmapped.");
			Configs.replacementBitsUnchiselable = getConfigReplacementBits(UNCHISELABLE_BLOCK_STATES, "minecraft:redstone_block", true, true, false);
			Configs.replacementBitsInsufficient = getConfigReplacementBits(INSUFFICIENT_BITS, "minecraft:redstone_block", true, true, false);
			
			//BIT TOOL DATA SETTINGS
			Configs.modelAreaMode = getBitToolSettingIntFromStringArray("Area Mode", DATA_CATAGORY_MODEL,
					modelingMapConfigFile, false, true, 0, 0,
					"area mode",
					"area mode (the area is either drawn (drawn), centered on the nearest block grid vertex (centered), or the area faces away " +
					"from the player with one of the corners of the area at the nearest block grid vertex (corner)).",
					ItemModelingTool.AREA_MODE_TITLES);
			
			Configs.modelSnapMode = getBitToolSettingIntFromStringArray("Chunk-Snap Mode", DATA_CATAGORY_MODEL,
					modelingMapConfigFile, false, true, 1, 0,
					"chunk-snap mode",
					"chunk-snap mode (either the area does not snap at all (off), it snaps in the X and Z axes (Y axis is unaffected) to whichever " +
					"chunk the block the player is looking at is in (snap-to-chunk XZ), or it additionally snaps in the Y axis to the 'vertical chunk' " +
					"(as visualized by pressing F3 + G) the block the player is looking at is in (snap-to-chunk XYZ)).",
					ItemModelingTool.SNAP_MODE_TITLES);
			
			Configs.modelGuiOpen = getBitToolSettingBoolean("Open Gui Upon Read", DATA_CATAGORY_MODEL,
					modelingMapConfigFile, false, true, true,
					"whether the modeling tool GUI opens upon reading block states",
					"whether the modeling tool GUI opens upon reading block states (if true, upon reading an area of block states in the world, the " +
					"GUI that allows the player to preview the model and to manually map bits to block states will open. If set to false, it will not " +
					"do so. Regardless of this setting, the GUI can be opened by right-clicking while sneaking).");
			
			Configs.sculptMode = getBitToolSettingIntFromStringArray("Sculpting Mode", DATA_CATAGORY_SCULPT,
					sculptingMapConfigFile, false, true, 0, 0,
					"sculpting mode",
					"sculpting mode (local mode affects only the block clicked - global/drawn modes affects any bits from any blocks that intersect " +
					"the sculpting shape when a block is clicked (global) or when the mouse is released after a click and drag (drawn)).",
					ItemSculptingTool.MODE_TITLES);
			
			Configs.sculptDirection = getBitToolSettingIntFromStringArray("Direction", DATA_CATAGORY_SCULPT,
						sculptingMapConfigFile, false, true, 1, 0,
						"sculpting shape direction",
						"direction.",
						BitToolSettingsHelper.getDirectionNames());// TODO decompose to direction and rotation when triangular shapes are implemented
			
			Configs.sculptShapeTypeCurved = getBitToolSettingIntFromStringArray("Shape (curved)", DATA_CATAGORY_SCULPT,
					sculptingMapConfigFile, false, true, 0, 0,
					"curved tool sculpting shape",
					"sculpting shape.",
					Arrays.copyOfRange(Shape.SHAPE_NAMES, 0, 3));
			
			Configs.sculptShapeTypeFlat = getBitToolSettingIntFromStringArray("Shape (flat/straight)", DATA_CATAGORY_SCULPT,
					sculptingMapConfigFile, false, true, 0, 3,
					"flat/straight tool sculpting shape",
					"sculpting shape.",
					new String[]{Shape.SHAPE_NAMES[3], Shape.SHAPE_NAMES[6]});
					//Arrays.copyOfRange(Shape.SHAPE_NAMES, 3, 7) TODO
			
			Configs.sculptTargetBitGridVertexes = getBitToolSettingBoolean("Target Bit Grid Vertexes", DATA_CATAGORY_SCULPT,
					sculptingMapConfigFile, false, true, false,
					"targeting mode",
					"targeting mode (when sculpting in local/global mode either bits are targeted [the shape is centered on the center of the bit looked " +
					"- the diameter is one (the center bit) plus/minus x number of bits (semi-diameter is x + 1/2 bit)], or vertices of the bit " +
					"grid are targeted [the shape is centered on the corner (the one closest to the cursor) of the bit looked at (i.e. centered on a " +
					"vertex of the grid) - the diameter is 2x number of bits (x is a true semi-diameter)]).");
			
			Configs.sculptHollowShapeWire = getBitToolSettingBoolean("Hollow Shapes (wire)", DATA_CATAGORY_SCULPT,
					sculptingMapConfigFile, false, true, false,
					"sculpting shape hollowness of sculpting wires",
					"sculpting wire hollow property value (shape is either hollow or solid).");
			
			Configs.sculptHollowShapeSpade = getBitToolSettingBoolean("Hollow Shapes (spade)", DATA_CATAGORY_SCULPT,
					sculptingMapConfigFile, false, true, false,
					"sculpting shape hollowness of sculpting spades",
					"sculpting spade hollow property value (shape is either hollow or solid).");
			
			Configs.sculptOpenEnds = getBitToolSettingBoolean("Open Ends", DATA_CATAGORY_SCULPT,
					sculptingMapConfigFile, false, true, false,
					"hollow sculpting shape open-endedness",
					"hollow sculpting shape open-ended property value (hollow shapes, such as cylinders, pyramids, etc., can have open or closed ends).");
			
			Configs.sculptSemiDiameter = getBitToolSettingInt("Semi-Diameter", DATA_CATAGORY_SCULPT,
					sculptingMapConfigFile, true, true, 5, 0, Integer.MAX_VALUE,
					"sculpting shape semi-diameter",
					"sculpting shape semi-diameter (in bits).", "5 bits");
			
			Configs.sculptWallThickness = getBitToolSettingInt("Wall Thickness", DATA_CATAGORY_SCULPT,
					sculptingMapConfigFile, false, true, 2, 1, Integer.MAX_VALUE,
					"hollow sculpting shape wall thickness",
					"hollow sculpting shape wall thickness (in bits).", "2 bits");
			
			Configs.sculptSetBitWire = getBitToolSettingBitStack("Bit Type - Filter (wire)", DATA_CATAGORY_SCULPT,
					sculptingMapConfigFile, true, true, "minecraft:air",
					"filtered bit type",
					"filtered bit type (sculpting can remove only one bit type rather than any - this config sets the block [as specified " +
					"by 'modID:name'] of the bit type that sculpting wires remove (an empty string, an unsupported block, or any misspelling " +
					"will specify any/all bit types)).", "Any");
			Configs.sculptSetBitWire.init();
			
			Configs.sculptSetBitSpade = getBitToolSettingBitStack("Bit Type - Addition (spade)", DATA_CATAGORY_SCULPT,
					sculptingMapConfigFile, true, true, "minecraft:air",
					"addition bit type",
					"addition bit type (sets the block form [as specified by 'modID:name'] of the bit type that sculpting spades add to the " +
					"world (an empty string, an unsupported block, or any misspelling will specify no bit type - the type will have to be set " +
					"before the spade can be used)).", "None");
			Configs.sculptSetBitSpade.init();
			
			Configs.sculptOffsetShape = getBitToolSettingBoolean("Offset Shape for Placement", DATA_CATAGORY_SCULPT,
					sculptingMapConfigFile, false, true, true,
					"whether or not sculpting shapes added with spades will be offset for placement",
					"shape placement property value (shapes added with spades can be centered around the bit clicked, of offset opposite the side clicked).");
			
			//ITEM PROPERTIES
			for (Item item : Configs.itemPropertyMap.keySet())
			{
				ConfigProperty configProperty = (ConfigProperty) Configs.itemPropertyMap.get(item);
				String itemTitle = configProperty.getTitle();
				String category = itemTitle + " Properties";
				boolean isSculptingTool = item instanceof ItemSculptingTool;
				configProperty.takesDamage = getToolTakesDamage(itemTitle, category, configProperty.getTakesDamageDefault(), isSculptingTool);
				configProperty.maxDamage = getToolMaxDamage(itemTitle, category, configProperty.getMaxDamageDefault(), 1, Integer.MAX_VALUE, isSculptingTool);
				if (!isSculptingTool)
					item.setMaxDamage(configProperty.takesDamage ? configProperty.maxDamage : 0);
			}
			
			//ITEM RECIPES
			for (Item item : Configs.itemRecipeMap.keySet())
			{
				ConfigRecipe configRecipe = (ConfigRecipe) Configs.itemRecipeMap.get(item);
				String itemTitle = configRecipe.getTitle();
				String category = itemTitle + " Recipe";
				configRecipe.isEnabled = getRecipeEnabled(itemTitle, category, configRecipe.getIsEnabledDefault());
				configRecipe.isShaped = getRecipeShaped(itemTitle, category, configRecipe.getIsShapedDefault());
				configRecipe.useOreDictionary = getRecipeOreDictionary(itemTitle, category, configRecipe.getUseOreDictionaryDefault());
				configRecipe.recipe = getRecipeList(itemTitle, category, configRecipe.getRecipeDefault());
			}
			
			Configs.disableDiamondNuggetOreDict = configFileCommon.getBoolean("Disable Diamond Nugget Ore Dict", RECIPES_DISABLE, false,
					"Disables the registration of the diamond nugget with the Ore Dictionary. (This will effectively disable the 9 nuggets " +
					"to 1 diamond recipe, since it is uses the Ore Dictionary)");
			
			Configs.disableDiamondToNuggets = configFileCommon.getBoolean("Disable Diamond to Nuggets Recipe", RECIPES_DISABLE, false,
					"Disables the recipe of 1 diamond to 9 diamond nuggets (i.e. 9 instances of 'nuggetDiamond' Ore Dictionary entries).");
			
			Configs.disableNuggetsToDiamond = configFileCommon.getBoolean("Disable Nuggets to Diamond Recipe", RECIPES_DISABLE, false,
					"Disables the recipe of 9 diamond nuggets to 1 diamond.");
			
			//RENDER OVERLAYS
			Configs.disableOverlays = configFileClient.getBoolean("Disable Overlay Rendering", RENDER_OVERLAYS, false,
					"Prevents overlays from rendering. (default = false)");
			
			Configs.rotationPeriod = configFileClient.getInt("Rotation Period", RENDER_OVERLAYS, 3000, 1, Integer.MAX_VALUE,
					"Number of milliseconds over which the cyclic arrow overlay used in block/texture rotation will complete one rotation. " +
					"(default = 3 seconds)");
			
			Configs.mirrorPeriod = configFileClient.getInt("Mirror Oscillation Period", RENDER_OVERLAYS, 830, 1, Integer.MAX_VALUE,
					"Number of milliseconds over which the bidirectional arrow overlay used in block/texture mirroring will complete one oscillation. " +
					"(default = 0.83 seconds)");
			
			Configs.mirrorAmplitude = getDouble(configFileClient, "Mirror Oscillation Amplitude", RENDER_OVERLAYS, 0.1, 0, Double.MAX_VALUE,
					"Half the total travel distance of the bidirectional arrow overlay used in block/texture mirroring as measured from the center of " +
					"the block face the player is looking at. If this is set to the minimum value of 0, no oscillation will occur. (default = 0.1 meters)");
			
			Configs.translationScalePeriod = configFileClient.getInt("Translation Scale Period", RENDER_OVERLAYS, 1300, 1, Integer.MAX_VALUE,
					"Number of milliseconds over which the circle overlay used in block translation will complete one cycle of scaling from a point to " +
					"full-sized or vice versa. (default = 1.3 seconds)");
			
			Configs.translationDistance = getDouble(configFileClient, "Arrow Movement Distance", RENDER_OVERLAYS, 0.75, 0, Double.MAX_VALUE,
					"Total travel distance of the arrowhead overlay used in block/texture translation/rotation as measured from the center of " +
					"the block face the player is looking at. If this is set to the minimum value of 0, only one arrow head will be rendered and " +
					"no movement will occur. (default = 0.75 meters)");
			
			Configs.translationOffsetDistance = getDouble(configFileClient, "Arrow Spacing", RENDER_OVERLAYS, 0.25, 0, Double.MAX_VALUE,
					"Distance between the three moving arrowhead overlays used in block/texture translation/rotation. If this is set to the minimum " +
					"value of 0, only one arrow head will be rendered. (default = 1/3 of the default distance of 0.75 meters, i.e. evenly spaced)");
			
			Configs.translationFadeDistance = getDouble(configFileClient, "Arrow Fade Distance", RENDER_OVERLAYS, 0.3, 0, Double.MAX_VALUE,
					"Distance over which the arrowhead overlay used in block/texture translation/rotation will fade in (as well as out) as it moves. " +
					"If this is set to the minimum value of 0, no fading will occur. (default = 0.3 meters)");
			
			Configs.translationMovementPeriod = configFileClient.getInt("Arrow Movement Period", RENDER_OVERLAYS, 2000, 1, Integer.MAX_VALUE,
					"Number of milliseconds over which the arrowhead overlay used in block/texture translation/rotation will travel from one end to the " +
					"other of the distance specified by 'Arrow Movement Distance'. (default = 2 seconds)");
			
			//RENDER SCULPTING TOOL SHAPES
			for (int i = 0; i < Configs.itemShapes.length; i++)
			{
				ConfigShapeRender configShapeRender = Configs.itemShapes[i];
				String category = configShapeRender.getTitle();
				configShapeRender.renderInnerShape = getShapeRender(category, true, configShapeRender.getRenderInnerShapeDefault());
				configShapeRender.renderOuterShape = getShapeRender(category, false, configShapeRender.getRenderOuterShapeDefault());
				configShapeRender.innerShapeAlpha = getShapeAlpha(category, true, configShapeRender.getInnerShapeAlphaDefault());
				configShapeRender.outerShapeAlpha = getShapeAlpha(category, false, configShapeRender.getOuterShapeAlphaDefault());
				configShapeRender.red = getShapeColor(category, 0, configShapeRender.getRedDefault());
				configShapeRender.green = getShapeColor(category, 1, configShapeRender.getGreenDefault());
				configShapeRender.blue = getShapeColor(category, 2, configShapeRender.getBlueDefault());
				configShapeRender.lineWidth = getShapeLineWidth(category, configShapeRender.getLineWidthDefault());
			}
		}
		catch (Exception e)
		{
			FMLLog.log(Reference.MOD_NAME, Level.ERROR, " configurations failed to update.");
			e.printStackTrace();
		}
		finally
		{
			saveConfigFile(configFileClient);
			saveConfigFile(configFileServer);
			saveConfigFile(configFileCommon);
			saveConfigFile(modelingMapConfigFile);
			saveConfigFile(sculptingMapConfigFile);
		}
	}
	
	private static void saveConfigFile(Configuration configFile)
	{
		if (configFile.hasChanged())
			configFile.save();
	}
	
	private static ConfigReplacementBits getConfigReplacementBits(String category, String defaultBlockName, boolean useDefaultReplacementBitDefault,
			boolean useAnyBitsAsReplacementsDefault, boolean useAirAsReplacementDefault)
	{
		String condition = "If " + (category == UNCHISELABLE_BLOCK_STATES ? "an unchiselable blockstate is encountered"
				: "the player has insufficient bits in their inventory for a chiselable blockstate") + ", an attempt will be made to find a replacement bit. ";
		ConfigBitStack defaultReplacementBit = new ConfigBitStack("", BitIOHelper.getStateFromString(configFileClient.getString("Default Replacement Bit",
				category, defaultBlockName, condition + "If the 'Use Default Replacement Bit' config is also set to 'true', then an attempt will first " +
				"be made to use the bit version of this block as a replacement.")), BitIOHelper.getStateFromString(defaultBlockName), defaultBlockName, null);
		String textIfTrue = ", if this is set to 'true', ";
		String textIfFalse = ". If this is set to 'false', this step will be skipped";
		boolean useDefaultReplacementBit = configFileClient.getBoolean("1st Check: Use Default Replacement Bit", category,
				useDefaultReplacementBitDefault, condition + "First" + textIfTrue + "an attempt will be made to use the bit specified " +
						"by the 'Default Replacement Bit' config" + textIfFalse);
		boolean useAnyBitsAsReplacements = configFileClient.getBoolean("2nd Check: Use Any Bit As Replacement", category,
				useAnyBitsAsReplacementsDefault, condition + "Second" + textIfTrue + "any/all bits in the player's inventory will " +
						"be used, from the most numerous to the least numerous" + textIfFalse);
		boolean useAirAsReplacement = configFileClient.getBoolean("3rd Check: Use Air As Replacement", category, useAirAsReplacementDefault,
				condition + "Third" + textIfTrue + "air bits will be used, i.e. the bits will simply be left empty" + textIfFalse);
		ConfigReplacementBits replacementBitsConfig = new ConfigReplacementBits(defaultReplacementBit,
				useDefaultReplacementBit, useAnyBitsAsReplacements, useAirAsReplacement);
		replacementBitsConfig.initDefaultReplacementBit();
		return replacementBitsConfig;
	}
	
	private static ConfigBitToolSettingBoolean getBitToolSettingBoolean(String name, String catagoryEnding, Configuration dataConfigFile,
			boolean defaultPerTool, boolean defaultDisplayInChat, boolean defaultValue, String toolTipSecondary, String toolTipDefaultValue)
	{
		boolean perTool = getPerTool(name, catagoryEnding, defaultPerTool, toolTipSecondary);
		boolean displayInChat = getDisplayInChat(name, catagoryEnding, defaultDisplayInChat, toolTipSecondary);
		boolean defaultBoolean = configFileClient.getBoolean(name, BIT_TOOL_DEFAULT_VALUES + " " + catagoryEnding, defaultValue,
				getToolTipBitToolSetting(toolTipDefaultValue, Boolean.toString(defaultValue)));
		boolean value = dataConfigFile.getBoolean(name, catagoryEnding, defaultValue, "");
		return new ConfigBitToolSettingBoolean(name, perTool, displayInChat, defaultBoolean, value);
	}
	
	private static ConfigBitToolSettingInt getBitToolSettingInt(String name, String catagoryEnding, Configuration dataConfigFile,
			boolean defaultPerTool, boolean defaultDisplayInChat, int defaultValue, int minValue, int maxValue,
			String toolTipSecondary, String toolTipDefaultValue, String toolTipDefaultValueDefault)
	{
		boolean perTool = getPerTool(name, catagoryEnding, defaultPerTool, toolTipSecondary);
		boolean displayInChat = getDisplayInChat(name, catagoryEnding, defaultDisplayInChat, toolTipSecondary);
		int defaultInt = configFileClient.getInt(name, BIT_TOOL_DEFAULT_VALUES + " " + catagoryEnding, defaultValue, minValue, maxValue,
				getToolTipBitToolSetting(toolTipDefaultValue, toolTipDefaultValueDefault));
		int value = dataConfigFile.getInt(name, catagoryEnding, defaultValue, minValue, maxValue, "");
		return new ConfigBitToolSettingInt(name, perTool, displayInChat, defaultInt, value);
	}
	
	private static ConfigBitToolSettingInt getBitToolSettingIntFromStringArray(String name, String catagoryEnding,
			Configuration dataConfigFile, boolean defaultPerTool, boolean defaultDisplayInChat, int defaultValue,
			int offset, String toolTipSecondary, String toolTipDefaultValue, String ... validValues)
	{
		boolean perTool = getPerTool(name, catagoryEnding, defaultPerTool, toolTipSecondary);
		boolean displayInChat = getDisplayInChat(name, catagoryEnding, defaultDisplayInChat, toolTipSecondary);
		String defaultInt = validValues[defaultValue];
		String entry = configFileClient.getString(name, BIT_TOOL_DEFAULT_VALUES + " " + catagoryEnding, defaultInt,
				getToolTipBitToolSetting(toolTipDefaultValue, defaultInt), validValues);
		for (int i = 0; i < validValues.length; i++)
		{
			if (entry.equals(validValues[i]))
				defaultValue = i + offset;
		}
		int value = dataConfigFile.getInt(name, catagoryEnding, defaultValue, 0, validValues.length - 1, "");
		return new ConfigBitToolSettingInt(name, perTool, displayInChat, defaultValue, value);
	}
	
	private static ConfigBitStack getBitToolSettingBitStack(String name, String catagoryEnding, Configuration dataConfigFile, boolean defaultPerTool,
			boolean defaultDisplayInChat, String defaultValue, String toolTipSecondary, String toolTipDefaultValue, String toolTipDefaultValueDefault)
	{
		boolean perTool = getPerTool(name, catagoryEnding, defaultPerTool, toolTipSecondary);
		boolean displayInChat = getDisplayInChat(name, catagoryEnding, defaultDisplayInChat, toolTipSecondary);
		IBlockState defaultState = BitIOHelper.getStateFromString(configFileClient.getString(name, BIT_TOOL_DEFAULT_VALUES + " " + catagoryEnding, defaultValue,
				getToolTipBitToolSetting(toolTipDefaultValue, toolTipDefaultValueDefault)));
		IBlockState valueDefault = BitIOHelper.getStateFromString(dataConfigFile.getString(name, catagoryEnding, defaultValue, ""));
		return new ConfigBitStack(name, perTool, displayInChat, defaultState, BitIOHelper.getStateFromString(defaultValue), defaultValue, valueDefault);
	}
	
	private static String getToolTipBitToolSetting(String toolTipDefaultValue, String toolTipDefaultValueDefault)
	{
		return "Players and sculpting tools will initialize with this " + toolTipDefaultValue + " (default = " + toolTipDefaultValueDefault + ")";
	}
	
	private static boolean getPerTool(String name, String catagoryEnding, boolean defaultPerTool, String toolTipPerTool)
	{
		return configFileClient.getBoolean(name, BIT_TOOL_PER_TOOL_OR_PER_CLIENT + " " + catagoryEnding, defaultPerTool,
				"If set to true, " + toolTipPerTool + " will be set/stored in each individual sculpting tool and apply only to that tool. " +
				"If set to false, it will be stored in the client config file called 'modeling_data' and will apply to all tools. Regardless " +
				"of this setting, the client and tools will still initialize with data, but this setting determines which is considered for use. " +
				getReferralString(toolTipPerTool) + " (default = " + defaultPerTool + ")");
	}
	
	private static boolean getDisplayInChat(String name, String catagoryEnding, boolean defaultDisplayInChat, String toolTipDisplayInChat)
	{
		return configFileClient.getBoolean(name, BIT_TOOL_DISPLAY_IN_CHAT + " " + catagoryEnding, defaultDisplayInChat,
				"If set to true, whenever " + toolTipDisplayInChat + " is changed, a message will be added to chat indicating the change. " +
				"This will not fill chat with messages, since any pre-existing messages from this mod will be deleted before adding the next." +
				getReferralString(toolTipDisplayInChat) + " (default = " + defaultDisplayInChat + ")");
	}
	
	private static String getReferralString(String settingString)
	{
		return "See 'Default Value' config for a description of " + settingString + ".";
	}
	
	private static boolean getShapeRender(String category, boolean inner, boolean defaultValue)
	{
		String shape = getShape(category);
		return configFileClient.getBoolean("Render " + (inner ? "Inner " : "Outer ") + shape, category, defaultValue,
				"Causes " + getSidedShapeText(shape, inner) + " to be rendered. (default = " + defaultValue + ")");
	}
	
	private static float getShapeAlpha(String category, boolean inner, int defaultValue)
	{
		String shape = getShape(category);
		return configFileClient.getInt("Alpha " + (inner ? "Inner " : "Outer ") + shape, category, defaultValue, 0, 255,
				"Sets the alpha value of " + getSidedShapeText(shape, inner) + ". (default = " + defaultValue + ")") / 255F;
	}
	
	private static String getSidedShapeText(String shape, boolean inner)
	{
		return "the portion of the " + shape.toLowerCase() + " that is " + (inner ? "behind" : "in front of") + " other textures";
	}
	
	private static float getShapeColor(String category, int colorFlag, int defaultValue)
	{
		String name = COLOR_NAMES[colorFlag];
		return configFileClient.getInt("Color - " + name, category, defaultValue, 0, 255,
				"Sets the " + name.toLowerCase() + " value of the " + getShape(category).toLowerCase() + ". (default = " + defaultValue + ")") / 255F;
	}
	
	private static float getShapeLineWidth(String category, float defaultValue)
	{
		return configFileClient.getFloat("Line Width", category, defaultValue, 0, Float.MAX_VALUE, 
				"Sets the line width of the " + getShape(category).toLowerCase() + ". (default = " + defaultValue + ")");
	}
	
	private static String getShape(String category)
	{
		return category.substring(category.lastIndexOf(" ") + 1, category.length());
	}
	
	private static int getToolMaxDamage(String name, String category, int defaultValue, int min, int max, boolean perBit)
	{
		return configFileCommon.getInt("Max Damage", category, defaultValue, min, max,
				"The " + name + " will " + (perBit ? "be able to add/remove this many bits " : "have this many uses ") +
				"if it is configured to take damage. (default = " + defaultValue + ")");
	}
	
	private static boolean getToolTakesDamage(String name, String category, boolean defaultValue, boolean perBit)
	{
		return configFileCommon.getBoolean("Takes Damage", category, defaultValue,
				"Causes the " + name + " to take a point of damage " + (perBit ? " for every bit added/removed " : "") +
				"when used. (default = " + defaultValue + ")");
	}
	
	private static boolean getRecipeEnabled(String name, String category, boolean defaultValue)
	{
		return configFileCommon.getBoolean("Is Enabled", category, defaultValue,
				"If set to true, the " + name + " will be craftable, otherwise it will not be. (default = " + defaultValue + ")");
	}
	
	private static boolean getRecipeShaped(String name, String category, boolean defaultValue)
	{
		return configFileCommon.getBoolean("Is Shaped", category, defaultValue,
				"If set to true, the recipe for the " + name + " will be shaped, and thus depend on the order/number of elements." +
				". If set to false, it will be shapeless and will be order-independent. (default = " + defaultValue + ")");
	}
	
	private static boolean getRecipeOreDictionary(String name, String category, boolean defaultValue)
	{
		return configFileCommon.getBoolean("Use Ore Dictionary", category, defaultValue,
				"If set to true, the string names given for the " + name + " recipe will be used to look up entries in the Ore Dictionary. " +
				"If set to false, they will be used to look up Items by name or ID. (default = " + defaultValue + ")");
	}
	
	private static String[] getRecipeList(String name, String category, String[] defaultValue)
	{
		return configFileCommon.getStringList("Recipe", category, defaultValue,
				"The Ore Dictionary names or Item names/IDs of components of the crafting recipe for the " + name + ". The elements of the list " +
				"correspond to the slots of the crafting grid (left to right / top to bottom). If the recipe shaped, the list must have 4 " +
				"elements to be a 2x2 recipe, 9 elements to be a 3x3 recipe, etc (i.e. must make a whole grid; root n elements for an n by n " +
				"grid). Inputting an incorrect number of elements will result in use of the default recipe. Empty strings denote empty slots " +
				"in the recipe. If the recipe shapeless, order is not important, and duplicates or empty strings will be ignored. Whether the " +
				"recipe is shaped or shapeless, strings that are not found in the Ore Dictionary or are not valid item names/IDs will be replaced " +
				"with empty spaces. The default recipe will be used if none of the provided strings are found.");
	}
	
	private static double getDouble(Configuration configFile, String name, String category,
			double defaultValue, double minValue, double maxValue, String comment)
	{
		Property prop = configFile.get(category, name, Double.toString(defaultValue), name);
		prop.setLanguageKey(name);
		prop.comment = comment + " [range: " + minValue + " ~ " + maxValue + ", default: " + defaultValue + "]";
		prop.setMinValue(minValue);
		prop.setMaxValue(maxValue);
		try
		{
			return Double.parseDouble(prop.getString()) < minValue ? minValue
					: (Double.parseDouble(prop.getString()) > maxValue ? maxValue : Double.parseDouble(prop.getString()));
		}
		catch (Exception e)
		{
			FMLLog.log(Reference.MOD_NAME, Level.ERROR, "Configuration '" +
		name + "' could not be parsed to a double. Default value of " + defaultValue + " was restored and used instead.");
		}
		return defaultValue;
	}
	
}