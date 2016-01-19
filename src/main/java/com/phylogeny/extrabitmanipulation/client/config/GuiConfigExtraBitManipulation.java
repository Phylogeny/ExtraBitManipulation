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
		addElements(ConfigHandlerExtraBitManipulation.BALANCE,
				"Configures the damage characteristics of the Bit Wrench", configElements);
		addElementsToSubCategory(configElements, 1,
				ConfigHandlerExtraBitManipulation.RECIPE_BIT_WRENCH,
				"Recipies", "Configures the recipie for the Bit Wrench");
		addElements(ConfigHandlerExtraBitManipulation.RENDER_OVERLAYS,
				"Configures the way wrench overlays are rendered", configElements);
		return configElements;
	}
	
	private static void addElementsToSubCategory(List<IConfigElement> configElements, int type, String... names)
	{
		addElementsToSubCategory(configElements, new ArrayList<IConfigElement>(), type, names);
	}
	
	private static void addElementsToSubCategory(List<IConfigElement> configElements, List<IConfigElement> childElements, int type, String... names)
	{
		int len = names.length;
		if (len >= 2)
		{
			int inc = type == 1 ? 1 : 2;
			for (int i = 0; i < names.length - 2; i += inc)
			{
				switch(type)
				{
					case 0: addElements(names[i], names[i + 1], childElements); break;
					case 1: addRecipeElements(names[i], childElements); break;
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