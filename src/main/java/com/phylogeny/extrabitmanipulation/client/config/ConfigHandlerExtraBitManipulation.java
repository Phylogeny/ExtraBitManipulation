package com.phylogeny.extrabitmanipulation.client.config;

import java.io.File;

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
	public static final String RENDER_OVERLAYS = "Render Overlays";
	public static final String RECIPE_BIT_WRENCH = "Bit Wrench";
	public static final String BALANCE = "Balance";
	
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
			//BALANCE
			Configs.TAKES_DAMAGE_BIT_WRENCH = configFile.getBoolean("Bit Wrench Takes Damage", BALANCE, true,
					"Causes the Bit Wrench to take a point of damage when used. (default = true)");
			
			Configs.MAX_DAMAGE_BIT_WRENCH = configFile.getInt("Bit Wrench Max Damage", BALANCE, 1500, 1, Integer.MAX_VALUE,
					"The Bit Wrench will have this many uses if it is configured to take damage. (default = 1500)");
			ItemsExtraBitManipulation.BitWrench.setMaxDamage(Configs.MAX_DAMAGE_BIT_WRENCH);
			
			//RECIPES
			Configs.RECIPE_BIT_WRENCH_IS_ENABLED = getRecipeEnabled(RECIPE_BIT_WRENCH, true);
			Configs.RECIPE_BIT_WRENCH_IS_SHAPED = getRecipeShaped(RECIPE_BIT_WRENCH, false);
			Configs.RECIPE_BIT_WRENCH_ORE_DICTIONARY = getRecipeOreDictionary(RECIPE_BIT_WRENCH, true);
			Configs.RECIPE_BIT_WRENCH = getRecipeList(RECIPE_BIT_WRENCH, Configs.RECIPE_BIT_WRENCH_DEFAULT);
			
			//RENDER_OVERLAYS
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
