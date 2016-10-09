package com.phylogeny.extrabitmanipulation.client.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.phylogeny.extrabitmanipulation.config.ConfigNamed;
import com.phylogeny.extrabitmanipulation.config.ConfigHandlerExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRender;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
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
		
		List<IConfigElement> configElementsModelMaker = new ArrayList<IConfigElement>();
		List<IConfigElement> configElementsTooSettings = new ArrayList<IConfigElement>();
		String textReplacementBits = "Configures the procedures for finding replacement bits ";
		String textUnchiselable = "when a blockstate is unchiselable";
		String textInsufficient = "when the player has insufficient bits for a chiselable blockstate";
		addChildElementsToDummyElement(ConfigHandlerExtraBitManipulation.UNCHISELABLE_BLOCK_STATES,
				textReplacementBits + textUnchiselable, configElementsModelMaker);
		addChildElementsToDummyElement(ConfigHandlerExtraBitManipulation.INSUFFICIENT_BITS,
				textReplacementBits + textInsufficient, configElementsModelMaker);
		addElementsToDummyElement("Model Maker Settings", textReplacementBits + textUnchiselable + " or " + textInsufficient,
				configElementsTooSettings, configElementsModelMaker);
		addChildElementsToDummyElement(ConfigHandlerExtraBitManipulation.SCULPTING_WRENCH_SETTINGS,
				"Configures sculpting dimensions, wrench inversion mode, the way bits are handled when removed from the world, " +
				"and the way bit removal/addition areas are displayed. (applies to all sculpting tools -- see 'Item Properties' " +
				"menu for item-specific settings)", configElementsTooSettings);
		addElementsToDummyElement("Tool Settings", "Configures sculpting dimensions, wrench inversion mode, the way bits are " +
				"handled when removed from the world, and the way bit removal/addition areas are displayed. (applies to all sculpting " +
				"tools -- see 'Item Properties' menu for item-specific settings), as well as the way the Model Maker finds replacement bits",
				configElements, configElementsTooSettings);
		
		addDummyElementsOfChildElementSetsToDummyElement(configElements, false,
				ConfigHandlerExtraBitManipulation.SCULPTING_DEFAULT_VALUES, "Configures sculpting data default values.",
				ConfigHandlerExtraBitManipulation.SCULPTING_PER_TOOL_OR_PER_PLAYER, "Configures whether sculpting data " +
						"is stored/assessed on/from individual tools or on/from the player.",
				ConfigHandlerExtraBitManipulation.SCULPTING_DISPLAY_IN_CHAT, "Configures whether changes to sculpting data are displayed in chat.",
				"Sculpting Data Settings", "Configures sculpting data storage/assesses, default values, and chat notifications upon change. " +
				"(applies to all sculpting tools -- see 'Item Properties' menu for item-specific settings)");
		addDummyElementsOfProcessedChildElementSetsToDummyElement(configElements, Configs.itemPropertyMap, "Item Properties",
				"Configures the damage characteristics and default data of the Bit Wrench and Sculpting Tools", false);
		addDummyElementsOfProcessedChildElementSetsToDummyElement(configElements, Configs.itemRecipeMap,
				"Recipes", "Configures the recipe for the Bit Wrench", true);
		addDummyElementsOfProcessedChildElementSetsToDummyElement(configElements, Configs.itemShapes,
				ConfigHandlerExtraBitManipulation.RENDER_OVERLAYS, "Configures the way the Bit Wrench overlays are rendered",
				"Sculpting Tool Shapes", "Configures the Sculpting Tools' bit removal/addition shapes/boxes",
				"Rendering", "Configures the rendering of the Bit Wrench's overlays and the Sculpting Tools' bit removal/addition shapes/boxes");
		return configElements;
	}
	
	private static void addDummyElementsOfProcessedChildElementSetsToDummyElement(List<IConfigElement> configElements,
			ConfigShapeRender[] config, String... names)
	{
		List<IConfigElement> childElements = new ArrayList<IConfigElement>();
		int startLen = names.length;
		for (int i = 0; i < startLen - 4; i += 2)
		{
			addChildElementsToDummyElement(names[i], names[i + 1], childElements);
		}
		int endLen = config.length * 2 + 2;
		String[] processedNames = new String[endLen];
		for (int i = 0; i < endLen - 2; i++)
		{
			int shapeIndex = i / 2;
			if (i % 2 == 0)
			{
				processedNames[i] = config[shapeIndex].getTitle();
			}
			else
			{
				processedNames[i] = "Configures the color/alpha/line-width of the " + getInsertText(shapeIndex) +
						(shapeIndex < 2 ? (" and the bounding box around the " + getInsertText(shapeIndex + 2)) : "")
						+ ", as well as which portions of it are rendered";
			}
		}
		processedNames[endLen - 2] = names[startLen - 4];
		processedNames[endLen - 1] = names[startLen - 3];
		addDummyElementsOfChildElementSetsToDummyElement(childElements, false, processedNames);
		addElementsToDummyElement(names[startLen - 2], names[startLen - 1], configElements, childElements);
	}
	
	private static String getInsertText(int shapeIndex)
	{
		boolean removeBits = shapeIndex % 2 == 0;
		return (shapeIndex == 0 ? "Straight" : (shapeIndex == 1 ? "Flat" : "Curved")) + " Sculpting " + (removeBits ? "Wire's " : "Spade's ") +
				(shapeIndex > 1 ? "spherical/ellipsoidal" : "cubic/cuboidal") + " bit " + (removeBits ? "removal" : "addition") + " area";
	}
	
	private static void addDummyElementsOfProcessedChildElementSetsToDummyElement(List<IConfigElement> configElements,
			Map<Item, ConfigNamed> configs, String name, String toolTip, boolean isRecipe)
	{
		int len = configs.size();
		if (!isRecipe)
			len *= 2;
		
		len += 2;
		String[] processedNames = new String[len];
		int i = 0;
		for (Item item : configs.keySet())
		{
			ConfigNamed config = configs.get(item);
			String itemTitle = config.getTitle();
			processedNames[i++] = itemTitle + (isRecipe ? " Recipe" : " Properties");
			if (!isRecipe)
				processedNames[i++] = "Configures the damage characteristics of the " + itemTitle;
		}
		processedNames[len - 2] = name;
		processedNames[len - 1] = toolTip;
		addDummyElementsOfChildElementSetsToDummyElement(configElements, isRecipe, processedNames);
	}
	
	private static void addDummyElementsOfChildElementSetsToDummyElement(List<IConfigElement> configElements, boolean isRecipe, String... names)
	{
		List<IConfigElement> childElements = new ArrayList<IConfigElement>();
		int len = names.length;
		if (len >= 2)
		{
			int inc = isRecipe ? 1 : 2;
			for (int i = 0; i < names.length - 2; i += inc)
			{
				if (isRecipe)
				{
					addRecipeChildElementsToDummyElement(names[i], childElements);
				}
				else
				{
					addChildElementsToDummyElement(names[i], names[i + 1], childElements);
				}
			}
			addElementsToDummyElement(names[len - 2], names[len - 1], configElements, childElements);
		}
	}
	
	private static void addRecipeChildElementsToDummyElement(String name, List<IConfigElement> configElements)
	{
		addChildElementsToDummyElement(name, "Configures the recipe type and configuration for the " + name, configElements);
	}
	
	private static void addElementsToDummyElement(String text, String toolTip, List<IConfigElement> configElements, List<IConfigElement> childElements)
	{
		configElements.add(new DummyConfigElement.DummyCategoryElement(text, toolTip, childElements));
	}
	
	private static void addChildElementsToDummyElement(String text, String toolTip, List<IConfigElement> configElements)
	{
		addElementsToDummyElement(text, toolTip, configElements, new ConfigElement(
				ConfigHandlerExtraBitManipulation.configFile.getCategory(text.toLowerCase())).getChildElements());
	}
	
}