package com.phylogeny.extrabitmanipulation.config;

import java.io.File;

import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class ConfigHandlerExtraBitManipulation
{
	public static Configuration configFile;
	public static final String VERSION = "Version";
	public static final String SCULPTING_SETTINGS = "Sculpting Settings";
	public static final String RENDER_OVERLAYS = "Bit Wrench Overlays";
	private static final String[] COLOR_NAMES = new String[]{"Red", "Green", "Blue"};
	
	public static void setUpConfigs(File file)
	{
		configFile = new Configuration(file);
		updateConfigs();
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
			String version = getVersion(VERSION);
			if (!version.equals(Reference.VERSION))
			{
				removeCategory("bit wrench recipe");
			}
			removeCategory(VERSION);
			getVersion(Reference.VERSION);
			
			//SCULPTING SETTINGS
			Configs.BIT_TYPE_IN_CHAT = configFile.getBoolean("Chat Message For Bit Type Change", SCULPTING_SETTINGS, true, 
					"If set to true, changing the set bit type of a sculpting tool (for adding bits with spades or filtering bits with wires)" +
					"will add the change to chat (does so without generating spam by deleting previous entry of the same type). " +
					"If set to false, it will not be added. Either way, the bit type is displayed in the tooltip. (default = true)");
			
			Configs.DISPLAY_NAME_DIAMETER = configFile.getBoolean("Display Name Diameter", SCULPTING_SETTINGS, true, 
					"If set to true, sculpting tool display names will indicate the diameter of their bit removal/addition areas. " +
					"If set to false, they will indicate the radius (default = true)");
			
			Configs.DISPLAY_NAME_USE_METER_UNITS = configFile.getBoolean("Display Name Meter Units", SCULPTING_SETTINGS, false, 
					"If set to true, sculpting tool display names will indicate the size of their bit removal/addition areas in meters. " +
					"If set to false, they will be in bits (default = false)");
			
			Configs.SEMI_DIAMETER_PADDING = configFile.getFloat("Semi-Diameter Padding", SCULPTING_SETTINGS, 0.2F, 0, 1, 
					"Distance (in bits) to add to the semi-diameter of a sculpting tool's bit removal/addition area shape. If set to zero, no padding " +
					"will be added; spheres, for example, will have single bits protruding from each cardinal direction at any size, since only those " +
					"bits of those layers will be exactly on the sphere's perimeter. If set to 1, there will effectively be no padding for the same reason, " +
					"but the radius will be one bit larger than specified. A value between 0 and 1 is suggested. (default = 0.2 bits)");
			
			Configs.PLACE_BITS_IN_INVENTORY = configFile.getBoolean("Place Bits In Inventory", SCULPTING_SETTINGS, true, 
					"If set to true, when bits are removed from blocks with a sculpting tool, as many of them will be given to the player as is possible. " +
					"Any bits that cannot fit in the player's inventory will be spawned in the world. If set to false, no attempt will be made to give them " +
					"to the player; they will always be spawned in the world. (default = true)");
			
			Configs.DROP_BITS_IN_BLOCKSPACE = configFile.getBoolean("Drop Bits In Block Space", SCULPTING_SETTINGS, true, 
					"If set to true, when bits removed from blocks with a sculpting tool are spawned in the world, they will be spawned at a random " +
					"point within the area that intersects the block space and the removal area bounding box (if 'Drop Bits Per Block' is true, they " +
					"will be spawned in the block they are removed from; otherwise they will be spawned at the block they player right-clicked). " +
					"If set to false, they will be spawned at the player, in the same way that items are spawned when throwing them on the ground " +
					"by pressing Q. (default = true)");
			
			Configs.BIT_SPAWN_BOX_CONTRACTION = configFile.getFloat("Bit Spawn Box Contraction", SCULPTING_SETTINGS, 0.25F, 0, 0.5F, 
					"Amount in meters to contract the box that removed bits randomly spawn in (assuming they spawn in the block space as per 'Drop Bits In Block Space') " +
					"If set to 0, there will be no contraction and they will be able to spawn anywhere in the box. If set to 0.5, the box will contract by half in all " +
					"directions down to a point in the center of the original box and they will always spawn from that central point. The default of 0.25 (which is the " +
					"default behavior when spawning items with Block.spawnAsEntity) contracts the box to half its original size. (default = 0.25 meters)");
			
			Configs.DROP_BITS_PER_BLOCK = configFile.getBoolean("Drop Bits Per Block", SCULPTING_SETTINGS, true, 
					"When bits are removed from blocks with a sculpting tool, all the removed bits of each type are counted and a collection of item stacks are created " +
					"of each item. For the sake of efficiency, the number of stacks generated is the minimum number necessary for that amount (Ex: 179 bits would become " +
					"2 stacks of 64 and 1 stack of 51). If this config is set to true, the counts for each block will be added up and spawned after each block is modified. " +
					"This means that when removing bits in global mode, the bits have the ability to spawn in the respective block spaces they are removed from. However, " +
					"it also means that more stacks may be generated than necessary if all bits from all blocks removed were to be pooled. If this config is set to false, " +
					"the bits will be added up and pooled together as they are removed from each block. Only once all blocks are modified will the entire collection of bits " +
					"be spawned in the world or given to the player. While this is more efficient, it means that the effect of bits spawning in the block spaces they are removed " +
					"from is not possible. Rather, the bits will either spawn in the space of the block clicked or spawn at the player as per 'Drop Bits In Block Space'. " +
					"(default = true)");
			
			Configs.DROP_BITS_AS_FULL_CHISELED_BLOCKS = configFile.getBoolean("Drop Bits As Full Chiseled Blocks", SCULPTING_SETTINGS, false, 
					"If set to true, full meter cubed blocks of bits that have all their bits removed will drop as full chiseled blocks. " +
					"If set to false, they will drop normally as item stacks of bits (64 stacks of size 64). (default = false)");
			
			//ITEM PROPERTIES
			for (Item item : Configs.itemPropertyMap.keySet())
			{
				ConfigProperty configProperty = (ConfigProperty) Configs.itemPropertyMap.get(item);
				String itemTitle = configProperty.getTitle();
				String category = itemTitle + " Properties";
				boolean isWrench = item instanceof ItemBitWrench;
				configProperty.takesDamage = getToolTakesDamage(itemTitle, category, configProperty.getTakesDamageDefault(), !isWrench);
				configProperty.maxDamage = getToolMaxDamage(itemTitle, category, configProperty.getMaxDamageDefault(), 1, Integer.MAX_VALUE, !isWrench);
				if (isWrench)
				{
					ItemsExtraBitManipulation.BitWrench.setMaxDamage(configProperty.takesDamage ? configProperty.maxDamage : 0);
				}
				else
				{
					configProperty.defaultRemovalSemiDiameter = configFile.getInt("Default Removal Semi-Diameter",
							category, configProperty.getDefaultRemovalSemiDiameterDefault(), 0, Integer.MAX_VALUE,
							"The semi-diameter (i.e. radius if it is a sphere) of the " + itemTitle + " removal shape " +
									"(Ex: 0 = only the bit clicked - diameter = 1; 1 = bit clicked +- 1 - diameter = 3, etc). (default = 5)");
					configProperty.maxRemovalSemiDiameter = configFile.getInt("Max Removal Semi-Diameter", category,
							configProperty.getMaxRemovalSemiDiameterDefault(), 0, Integer.MAX_VALUE,
							"The maximum semi-diameter (i.e. radius if it is a sphere) of the " + itemTitle + " removal shape " +
									"(continual increasing/decreasing of radius will cause cycling from 0 to this number). (default = 2 meters)");
				}
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
			
			//RENDER OVERLAYS
			Configs.DISABLE_OVERLAYS = configFile.getBoolean("Disable Overlay Rendering", RENDER_OVERLAYS, false,
					"Prevents overlays from rendering. (default = false)");
			
			Configs.ROTATION_PERIOD = getDouble(configFile, "Rotation Period", RENDER_OVERLAYS, 180, 1, Double.MAX_VALUE,
					"Number of frames over which the cyclical arrow overlay used in block/texture rotation will complete one rotation. If this is " +
					"set to the minimum value of 1, no rotation will occur. (default = 3 seconds at 60 fps)");
			
			Configs.MIRROR_PERIOD = getDouble(configFile, "Mirror Oscillation Period", RENDER_OVERLAYS, 50, 1, Double.MAX_VALUE,
					"Number of frames over which the bidirectional arrow overlay used in block/texture mirroring will complete one oscillation. If this is " +
					"set to the minimum value of 1, no oscillation will occur. (default = 0.83 seconds at 60 fps)");
			
			Configs.MIRROR_AMPLITUDE = getDouble(configFile, "Mirror Oscillation Amplitude", RENDER_OVERLAYS, 0.1, 0, Double.MAX_VALUE,
					"Half the total travel distance of the bidirectional arrow overlay used in block/texture mirroring as measured from the center of " +
					"the block face the player is looking at. If this is set to the minimum value of 0, no oscillation will occur. (default = 0.1 meters)");
			
			Configs.TRANSLATION_SCALE_PERIOD = getDouble(configFile, "Translation Scale Period", RENDER_OVERLAYS, 80, 1, Double.MAX_VALUE,
					"Number of frames over which the circle overlay used in block translation will complete one cycle of scaling from a point to " +
					"full-sized or vice versa. If this is set to the minimum value of 1, no scaling will occur. (default = 1.33 seconds at 60 fps)");
			
			Configs.TRANSLATION_DISTANCE = getDouble(configFile, "Arrow Movement Distance", RENDER_OVERLAYS, 0.75, 0, Double.MAX_VALUE,
					"Total travel distance of the arrowhead overlay used in block/texture translation/rotation as measured from the center of " +
					"the block face the player is looking at. If this is set to the minimum value of 0, only one arrow head will be rendered and " +
					"no movement will occur. (default = 0.75 meters)");
			
			Configs.TRANSLATION_OFFSET_DISTANCE = getDouble(configFile, "Arrow Spacing", RENDER_OVERLAYS, 0.25, 0, Double.MAX_VALUE,
					"Distance between the three moving arrowhead overlays used in block/texture translation/rotation. If this is set to the minimum " +
					"value of 0, only one arrow head will be rendered. (default = 1/3 of the default distance of 0.75 meters, i.e. evenly spaced)");
			
			Configs.TRANSLATION_FADE_DISTANCE = getDouble(configFile, "Arrow Fade Distance", RENDER_OVERLAYS, 0.3, 0, Double.MAX_VALUE,
					"Distance over which the arrowhead overlay used in block/texture translation/rotation will fade in (as well as out) as it moves. " +
					"If this is set to the minimum value of 0, no fading will occur. (default = 0.3 meters)");
			
			Configs.TRANSLATION_MOVEMENT_PERIOD = getDouble(configFile, "Arrow Movement Period", RENDER_OVERLAYS, 120, 1, Double.MAX_VALUE,
					"Number of frames over which the arrowhead overlay used in block/texture translation/rotation will travel from one end to the " +
					"other of the distance specified by 'Arrow Movement Distance'. If this is set to the minimum value of 1, no movement will occur. " +
					"(default = 2 seconds at 60 fps)");
			
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
			System.out.println(Reference.MOD_NAME + " configurations failed to update.");
			e.printStackTrace();
		}
		finally
		{
			if (configFile.hasChanged())
			{
				configFile.save();
			}
		}
	}

	private static void removeCategory(String category)
	{
		configFile.removeCategory(configFile.getCategory(category.toLowerCase()));
	}

	private static String getVersion(String defaultValue)
	{
		return configFile.getString(VERSION, VERSION, defaultValue.toLowerCase(), "Used for cofig updating when updating mod version. Do not change.");
	}
	
	private static boolean getShapeRender(String category, boolean inner, boolean defaultValue)
	{
		String shape = getShape(category);
		return configFile.getBoolean("Render " + (inner ? "Inner " : "Outer ") + shape, category, defaultValue,
				"Causes " + getSidedShapeText(shape, inner) + " to be rendered. (default = " + defaultValue + ")");
	}
	
	private static float getShapeAlpha(String category, boolean inner, int defaultValue)
	{
		String shape = getShape(category);
		return configFile.getInt("Alpha " + (inner ? "Inner " : "Outer ") + shape, category, defaultValue, 0, 255,
				"Sets the alpha value of " + getSidedShapeText(shape, inner) + ". (default = " + defaultValue + ")") / 255F;
	}
	
	private static String getSidedShapeText(String shape, boolean inner)
	{
		return "the portion of the " + shape.toLowerCase() + " that is " + (inner ? "behind" : "in front of") + " other textures";
	}
	
	private static float getShapeColor(String category, int colorFlag, int defaultValue)
	{
		String name = COLOR_NAMES[colorFlag];
		return configFile.getInt("Color - " + name, category, defaultValue, 0, 255,
				"Sets the " + name.toLowerCase() + " value of the " + getShape(category).toLowerCase() + ". (default = " + defaultValue + ")") / 255F;
	}
	
	private static float getShapeLineWidth(String category, float defaultValue)
	{
		return configFile.getFloat("Line Width", category, defaultValue, 0, Float.MAX_VALUE, 
				"Sets the line width of the " + getShape(category).toLowerCase() + ". (default = " + defaultValue + ")");
	}
	
	private static String getShape(String category)
	{
		return category.substring(category.lastIndexOf(" ") + 1, category.length());
	}

	private static int getToolMaxDamage(String name, String category, int defaultValue, int min, int max, boolean perBit)
	{
		return configFile.getInt("Max Damage", category, defaultValue, min, max,
				"The " + name + " will " + (perBit ? "be able to add/remove this many bits " : "have this many uses ")
				+ "if it is configured to take damage. (default = " + defaultValue + ")");
	}

	private static boolean getToolTakesDamage(String name, String category, boolean defaultValue, boolean perBit)
	{
		return configFile.getBoolean("Takes Damage", category, defaultValue,
				"Causes the " + name + " to take a point of damage " + (perBit ? " for every bit added/removed " : "")
				+ "when used. (default = " + defaultValue + ")");
	}
	
	private static boolean getRecipeEnabled(String name, String category, boolean defaultValue)
	{
		return configFile.getBoolean("Is Enabled", category, defaultValue,
				"If set to true, the " + name + " will be craftable, otherwise it will not be. (default = " + defaultValue + ")");
	}

	private static boolean getRecipeShaped(String name, String category, boolean defaultValue)
	{
		return configFile.getBoolean("Is Shaped", category, defaultValue,
				"If set to true, the recipe for the " + name + " will be shaped, and thus depend on the order/number of elements." +
				". If set to false, it will be shapeless and will be order-independent. (default = " + defaultValue + ")");
	}
	
	private static boolean getRecipeOreDictionary(String name, String category, boolean defaultValue)
	{
		return configFile.getBoolean("Use Ore Dictionary", category, defaultValue,
				"If set to true, the string names given for the " + name + " recipe will be used to look up entries in the Ore Dictionary. " +
				"If set to false, they will be used to look up Items by name or ID. (default = " + defaultValue + ")");
	}

	private static String[] getRecipeList(String name, String category, String[] defaultValue)
	{
		return configFile.getStringList("Recipe", category, defaultValue,
				"The Ore Dictionary names or Item names/IDs of components of the crafting recipe for the " + name + ". The elements of the list " +
				"correspond to the slots of the crafting grid (left to right / top to bottom). If the recipe shaped, the list must have 4 " +
				"elements to be a 2x2 recipe, 9 elements to be a 3x3 recipe, etc (i.e. must make a whole grid; root n elements for an n by n " +
				"grid). Inputting an incorrect number of elements will result in use of the default recipe. Empty strings denote empty slots " +
				"in the recipe. If the recipe shapeless, order is not important, and duplicates or empty strings will be ignored. Whether the " +
				"recipe is shaped or shapeless, strings that are not found in the Ore Dictionary or are not valid item names/IDs will be replaced " +
				"with empty spaces. The default recipe will be used if none of the provided strings are found.");
	}
	
	private static double getDouble(Configuration configFile, String name, String category, double defaultValue, double minValue, double maxValue, String comment)
    {
        Property prop = configFile.get(category, name, Double.toString(defaultValue), name);
        prop.setLanguageKey(name);
        prop.comment = comment + " [range: " + minValue + " ~ " + maxValue + ", default: " + defaultValue + "]";
        prop.setMinValue(minValue);
        prop.setMaxValue(maxValue);
        try
        {
            return Double.parseDouble(prop.getString()) < minValue ? minValue : (Double.parseDouble(prop.getString()) > maxValue ? maxValue : Double.parseDouble(prop.getString()));
        }
        catch (Exception e)
        {
        	System.out.println("The " + Reference.MOD_NAME + " configuration '" + name
        			+ "' could not be parsed to a double. Default value of " + defaultValue + " was restored and used instead.");
        }
        return defaultValue;
    }
	
}
