package com.phylogeny.extrabitmanipulation.client.config;

import java.io.File;

import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class ConfigHandlerExtraBitManipulation
{
	public static Configuration configFile;
	public static final String BIT_WRENCH_PROPERTIES = "Bit Wrench Properties";
	public static final String SCULPTING_LOOP_PROPERTIES = "Sculpting Loop Properties";
	public static final String RECIPE_BIT_WRENCH = "Bit Wrench Recipe";
	public static final String RECIPE_SCULPTING_LOOP = "Sculpting Loop Recipe";
	public static final String RENDER_OVERLAYS = "Bit Wrench Overlays";
	public static final String RENDER_SPHERE = "Sculpting Loop Shpere";
	public static final String RENDER_BOX = "Sculpting Loop Box";
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
			//ITEM PROPERTIES
			Configs.TAKES_DAMAGE_BIT_WRENCH = getToolTakesDamage(BIT_WRENCH_PROPERTIES, BIT_WRENCH_PROPERTIES, true);
			getToolMaxDamage(RECIPE_BIT_WRENCH, BIT_WRENCH_PROPERTIES, 5000, 1, Integer.MAX_VALUE, ItemsExtraBitManipulation.BitWrench);
			
			Configs.TAKES_DAMAGE_SCULPTING_LOOP = getToolTakesDamage(SCULPTING_LOOP_PROPERTIES, SCULPTING_LOOP_PROPERTIES, true);
			getToolMaxDamage(SCULPTING_LOOP_PROPERTIES, SCULPTING_LOOP_PROPERTIES, 5000, 1, Integer.MAX_VALUE, ItemsExtraBitManipulation.SculptingLoop);
			
			Configs.DEFAULT_REMOVAL_RADIUS = configFile.getInt("Default Removal Radius", SCULPTING_LOOP_PROPERTIES, 5, 0, Integer.MAX_VALUE,
					"The radius of the removal sphere (Ex: 0 = only the bit clicked - diameter = 1; 1 = bit clicked +- 1 - diameter = 3, etc). (default = 5)");
			
			Configs.MAX_REMOVAL_RADIUS = configFile.getInt("Max Removal Radius", SCULPTING_LOOP_PROPERTIES, 32, 0, Integer.MAX_VALUE,
					"The maximum radius of the removal sphere (continual increasing/decreasing of radius will cause cycling from 0 to this number). (default = 2 meters)");
			
			//RECIPES
			Configs.RECIPE_BIT_WRENCH_IS_ENABLED = getRecipeEnabled(RECIPE_BIT_WRENCH, true);
			Configs.RECIPE_BIT_WRENCH_IS_SHAPED = getRecipeShaped(RECIPE_BIT_WRENCH, true);
			Configs.RECIPE_BIT_WRENCH_ORE_DICTIONARY = getRecipeOreDictionary(RECIPE_BIT_WRENCH, true);
			Configs.RECIPE_BIT_WRENCH = getRecipeList(RECIPE_BIT_WRENCH, Configs.RECIPE_BIT_WRENCH_DEFAULT);
			
			Configs.RECIPE_SCULPTING_LOOP_IS_ENABLED = getRecipeEnabled(RECIPE_SCULPTING_LOOP, true);
			Configs.RECIPE_SCULPTING_LOOP_IS_SHAPED = getRecipeShaped(RECIPE_SCULPTING_LOOP, true);
			Configs.RECIPE_SCULPTING_LOOP_ORE_DICTIONARY = getRecipeOreDictionary(RECIPE_SCULPTING_LOOP, true);
			Configs.RECIPE_SCULPTING_LOOP = getRecipeList(RECIPE_SCULPTING_LOOP, Configs.RECIPE_SCULPTING_LOOP_DEFAULT);
			
			//RENDER_OVERLAYS
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
			
			//RENDER_SCULPTING_LOOP_SPHERE
			Configs.RENDER_INNER_SPHERE = getShapeRender(RENDER_SPHERE, true, true);
			Configs.RENDER_OUTER_SPHERE = getShapeRender(RENDER_SPHERE, false, false);
			Configs.INNER_SPHERE_ALPHA = getShapeAlpha(RENDER_SPHERE, true, 115);
			Configs.OUTER_SPHERE_ALPHA = getShapeAlpha(RENDER_SPHERE, false, 38);
			Configs.SPHERE_RED = getShapeColor(RENDER_SPHERE, 0, 0);
			Configs.SPHERE_GREEN = getShapeColor(RENDER_SPHERE, 1, 0);
			Configs.SPHERE_BLUE = getShapeColor(RENDER_SPHERE, 2, 255);
			Configs.SPHERE_LINE_WIDTH = getShapeLineWidth(RENDER_SPHERE, 2.0F);
			
			//RENDER_SCULPTING_LOOP_BOX
			Configs.RENDER_INNER_BOX = getShapeRender(RENDER_BOX, true, true);
			Configs.RENDER_OUTER_BOX = getShapeRender(RENDER_BOX, false, true);
			Configs.INNER_BOX_ALPHA = getShapeAlpha(RENDER_BOX, true, 28);
			Configs.OUTER_BOX_ALPHA = getShapeAlpha(RENDER_BOX, false, 115);
			Configs.BOX_RED = getShapeColor(RENDER_BOX, 0, 0);
			Configs.BOX_GREEN = getShapeColor(RENDER_BOX, 1, 0);
			Configs.BOX_BLUE = getShapeColor(RENDER_BOX, 2, 0);
			Configs.BOX_LINE_WIDTH = getShapeLineWidth(RENDER_BOX, 2.0F);
			
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
		return category == RENDER_SPHERE ? "Sphere" : "Box";
	}

	private static int getToolMaxDamage(String name, String category, int defaultValue, int min, int max, Item item)
	{
		int returnValue = configFile.getInt("Max Damage", category, defaultValue, min, max,
				"The " + name + " will have this many uses if it is configured to take damage. (default = " + defaultValue + ")");
		item.setMaxDamage(returnValue);
		return returnValue;
	}

	private static boolean getToolTakesDamage(String name, String category, boolean defaultValue)
	{
		return configFile.getBoolean("Takes Damage", category, defaultValue,
				"Causes the " + name + " to take a point of damage when used. (default = " + defaultValue + ")");
	}
	
	private static boolean getRecipeEnabled(String name, boolean defaultValue)
	{
		return configFile.getBoolean("Is Enabled", name, defaultValue,
				"If set to true, the " + name + " will be craftable, otherwise it will not be. (default = " + defaultValue + ")");
	}

	private static boolean getRecipeShaped(String name, boolean defaultValue)
	{
		return configFile.getBoolean("Is Shaped", name, defaultValue,
				"If set to true, the recipe for the " + name + " will be shaped, and thus depend on the order/number of elements." +
				". If set to false, it will be shapeless and will be order-independent. (default = " + defaultValue + ")");
	}
	
	private static boolean getRecipeOreDictionary(String name, boolean defaultValue)
	{
		return configFile.getBoolean("Use Ore Dictionary", name, defaultValue,
				"If set to true, the string names given for the " + name + " recipe will be used to look up entries in the Ore Dictionary. " +
				"If set to false, they will be used to look up Items by name or ID. (default = " + defaultValue + ")");
	}

	private static String[] getRecipeList(String name, String[] defaultValue)
	{
		return configFile.getStringList("Recipe", name, defaultValue,
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
