package com.phylogeny.extrabitmanipulation.client.config;

import java.util.ArrayList;
import java.util.List;

import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiConfigExtraBitManipulation extends GuiConfig
{

	public GuiConfigExtraBitManipulation(GuiScreen parentScreen)
	{
		super(parentScreen, getConfigElements(), Reference.MOD_ID, false, false,
				GuiConfig.getAbridgedConfigPath(ConfigHandlerExtraBitManipulation.configFile.toString()));
	}
	
	private static List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> configElements = new ArrayList<IConfigElement>();
		
		addElementsToSubCategory(configElements, false,
				ConfigHandlerExtraBitManipulation.BIT_WRENCH_PROPERTIES,
				"Configures the damage characteristics of the Bit Wrench",
				ConfigHandlerExtraBitManipulation.SCULPTING_LOOP_PROPERTIES,
				"Configures the damage characteristics and default data of the Sculpting Loop",
				"Item Properties", "Configures the damage characteristics and default data of the Sculpting Loop and Bit Wrench");
		addElementsToSubCategory(configElements, true,
				ConfigHandlerExtraBitManipulation.RECIPE_BIT_WRENCH,
				ConfigHandlerExtraBitManipulation.RECIPE_SCULPTING_LOOP,
				"Recipies", "Configures the recipie for the Bit Wrench");
		String sculptingLoppText1 = "Configures the color/alpha/line-width of the ";
		String sculptingLoppText2 = "Sculpting Loop's removal area, as well as which portions of it are rendered";
		addElementsToSubCategory(configElements, false,
				ConfigHandlerExtraBitManipulation.RENDER_OVERLAYS,
				"Configures the way the Bit Wrench overlays are rendered",
				ConfigHandlerExtraBitManipulation.RENDER_SPHERE,
				sculptingLoppText1 + sculptingLoppText2,
				ConfigHandlerExtraBitManipulation.RENDER_BOX,
				sculptingLoppText1 + "box around the " + sculptingLoppText2,
				"Rendering", "Configures the rendering of the Bit Wrench's overlays and the Sculpting Loop's removal sphere/box");
		return configElements;
	}
	
	private static void addElementsToSubCategory(List<IConfigElement> configElements, boolean isRecipe, String... names)
	{
		addElementsToSubCategory(configElements, new ArrayList<IConfigElement>(), isRecipe, names);
	}
	
	private static void addElementsToSubCategory(List<IConfigElement> configElements, List<IConfigElement> childElements, boolean isRecipe, String... names)
	{
		int len = names.length;
		if (len >= 2)
		{
			int inc = isRecipe ? 1 : 2;
			for (int i = 0; i < names.length - 2; i += inc)
			{
				if (isRecipe)
				{
					addRecipeElements(names[i], childElements);
				}
				else
				{
					addElements(names[i], names[i + 1], childElements);
				}
			}
			addElements(names[len - 2], names[len - 1], configElements, childElements);
		}
	}
	
	private static void addRecipeElements(String name, List<IConfigElement> configElements)
	{
		addElements(name, "Configures the recipe type and configuration for the " + name, configElements);
	}
	
	private static void addElements(String text, String toolTip, List<IConfigElement> configElements, List<IConfigElement> childElements)
	{
		configElements.add(new DummyConfigElement.DummyCategoryElement(text, toolTip, childElements));
	}
	
	private static void addElements(String text, String toolTip, List<IConfigElement> configElements)
	{
		addElements(text, toolTip, configElements, new ConfigElement(ConfigHandlerExtraBitManipulation.configFile.getCategory(text.toLowerCase())).getChildElements());
	}
	
}