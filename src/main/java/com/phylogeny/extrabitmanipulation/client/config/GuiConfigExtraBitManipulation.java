package com.phylogeny.extrabitmanipulation.client.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

import com.phylogeny.extrabitmanipulation.config.ConfigHandlerExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.config.ConfigNamed;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRender;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class GuiConfigExtraBitManipulation extends GuiConfig
{
	
	public GuiConfigExtraBitManipulation(GuiScreen parentScreen)
	{
		super(parentScreen, getConfigElements(), Reference.MOD_ID, false, false, getAbridgedConfigPath());
	}
	
	private static String getAbridgedConfigPath()
	{
		String path = GuiConfig.getAbridgedConfigPath(ConfigHandlerExtraBitManipulation.configFileClient.toString());
		return path.substring(0, path.lastIndexOf("/") + 1);
	}
	
	private static List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> configElements = new ArrayList<IConfigElement>();
		List<IConfigElement> configElementsClient = new ArrayList<IConfigElement>();
		List<IConfigElement> configElementsServer = new ArrayList<IConfigElement>();
		List<IConfigElement> configElementsCommon = new ArrayList<IConfigElement>();
		
		addToolSettingsDummyElements(ConfigHandlerExtraBitManipulation.configFileClient, configElementsClient);
		addToolSettingsDummyElements(ConfigHandlerExtraBitManipulation.configFileServer, configElementsServer);
		
		addChildElementsToDummyElement(ConfigHandlerExtraBitManipulation.configFileServer, ConfigHandlerExtraBitManipulation.THROWN_BIT_PROPERTIES,
				"Configures the damage amount, disabling of damage, entity burn time (for lava bits), velocity, and inaccuracy of thrown bits",
				configElementsServer, ServerEntry.class);
		
		List<IConfigElement> configElementsToolData = new ArrayList<IConfigElement>();
		String hoverText = "Configures @@@ data storage/access, default values, and " +
				"chat notifications upon change. (applies to all tools -- see 'Item Properties' menu for item-specific settings)";
		addToolDataDummyElements(ConfigHandlerExtraBitManipulation.configFileClient, configElementsToolData,
				ConfigHandlerExtraBitManipulation.DATA_CATAGORY_ARMOR, hoverText);
		addToolDataDummyElements(ConfigHandlerExtraBitManipulation.configFileClient, configElementsToolData,
				ConfigHandlerExtraBitManipulation.DATA_CATAGORY_MODEL, hoverText);
		addToolDataDummyElements(ConfigHandlerExtraBitManipulation.configFileClient, configElementsToolData,
				ConfigHandlerExtraBitManipulation.DATA_CATAGORY_SCULPT, hoverText);
		addElementsToDummyElement("Tool Data Settings", hoverText.replace("@@@", ConfigHandlerExtraBitManipulation.DATA_CATAGORY_MODEL.toLowerCase() +
				" and " + ConfigHandlerExtraBitManipulation.DATA_CATAGORY_SCULPT.toLowerCase()),
				configElementsClient, configElementsToolData, ClientEntry.class);
		
		addChildElementsToDummyElement(ConfigHandlerExtraBitManipulation.configFileCommon, ConfigHandlerExtraBitManipulation.THROWN_BIT_PROPERTIES,
				"Configures the disabling of igniting/extinguishing entities/blocks when hit with thrown lava/water bits", configElementsCommon, CommonEntry.class);
		addDummyElementsOfProcessedChildElementSetsToDummyElement(ConfigHandlerExtraBitManipulation.configFileCommon,
				configElementsCommon, Configs.itemPropertyMap, "Item Properties",
				"Configures the damage characteristics and default data of the Bit Tools", CommonEntry.class);
		addDummyElementsOfProcessedChildElementSetsToDummyElement(ConfigHandlerExtraBitManipulation.configFileCommon,
				configElementsCommon, Configs.itemRecipeMap,
				"Recipes", "Configures the recipe for the Bit Tools and the disabling of diamond nugget recipes / Ore Dictionary registration", RecipeEntry.class);
		addDummyElementsOfProcessedChildElementSetsToDummyElement(ConfigHandlerExtraBitManipulation.configFileClient, configElementsClient, Configs.itemShapes,
				ClientEntry.class, ConfigHandlerExtraBitManipulation.RENDER_OVERLAYS, "Configures the way the Bit Wrench overlays are rendered",
				"Sculpting Tool Shapes", "Configures the Sculpting Tools' bit removal/addition shapes/boxes",
				"Rendering", "Configures the rendering of the Bit Wrench's overlays and the Sculpting Tools' bit removal/addition shapes/boxes");
		
		addElementsToDummyElement("Client", "Contains configs only accessed by the client", configElements, configElementsClient, null);
		addElementsToDummyElement("Server", "Contains configs only accessed by the server", configElements, configElementsServer, null);
		addElementsToDummyElement("Common", "Contains configs accessed by both the server and the client (problems will occur " +
				"if these are not the same on both client and server)", configElements, configElementsCommon, null);
		
		return configElements;
	}
	
	private static void addToolSettingsDummyElements(Configuration configFile, List<IConfigElement> configElements)
	{
		List<IConfigElement> configElementsToolSettings = new ArrayList<IConfigElement>();
		boolean isClient = configFile.equals(ConfigHandlerExtraBitManipulation.configFileClient);
		Class configClass = isClient ? ClientEntry.class : ServerEntry.class;
		addChildElementsToDummyElement(configFile, ConfigHandlerExtraBitManipulation.ARMOR_SETTINGS,
				isClient ? "Configures the z-fighting buffer scale amount for Chiseled Armor pieces"
						: "Configures what to do with a Chiseled Armor GUI slot's itemstack when that slot is removed",
				configElementsToolSettings, configClass);
		if (isClient)
		{
			List<IConfigElement> configElementsModelingTool = new ArrayList<IConfigElement>();
			String textStorage = "the the way block states are stored, and ";
			String textReplacementBits = "Configures" + textStorage + "the procedures for finding replacement bits ";
			String textUnchiselable = "when a blockstate is unchiselable";
			String textInsufficient = "when the player has insufficient bits for a chiselable blockstate";
			configElementsModelingTool.addAll(getChildElements(ConfigHandlerExtraBitManipulation.configFileClient,
					ConfigHandlerExtraBitManipulation.MODELING_TOOL_SETTINGS));
			addChildElementsToDummyElement(ConfigHandlerExtraBitManipulation.configFileClient, ConfigHandlerExtraBitManipulation.UNCHISELABLE_BLOCK_STATES,
					textReplacementBits.replace(textStorage, " ") + textUnchiselable, configElementsModelingTool, ClientEntry.class);
			addChildElementsToDummyElement(ConfigHandlerExtraBitManipulation.configFileClient, ConfigHandlerExtraBitManipulation.INSUFFICIENT_BITS,
					textReplacementBits.replace(textStorage, " ") + textInsufficient, configElementsModelingTool, ClientEntry.class);
			addElementsToDummyElement("Modeling Tool Settings", textReplacementBits + textUnchiselable + " or " + textInsufficient,
					configElementsToolSettings, configElementsModelingTool, ClientEntry.class);
		}
		addChildElementsToDummyElement(configFile, ConfigHandlerExtraBitManipulation.SCULPTING_WRENCH_SETTINGS,
				"Configures sculpting dimensions, wrench inversion mode, the way bits are handled when removed from the world, and the way bit " +
				"removal/addition areas are displayed. (applies to all sculpting tools -- see 'Item Properties' menu for item-specific settings)",
				configElementsToolSettings, configClass);
		addElementsToDummyElement("Tool Settings", "Configures sculpting dimensions, wrench inversion mode, the way bits are handled when removed " +
				"from the world, and the way bit removal/addition areas are displayed. (applies to all sculpting tools -- see 'Item Properties' " +
				"menu for item-specific settings)" + (isClient ? ", as well as the way the Modeling Tool stores block states and finds replacement bits" : ""),
				configElements, configElementsToolSettings, configClass);
	}
	
	private static void addToolDataDummyElements(Configuration configFile, List<IConfigElement> configElementsToolData, String dataCatagory, String hoverText)
	{
		String dataCatagoryLower = dataCatagory.toLowerCase();
		List<IConfigElement> childElements = new ArrayList<IConfigElement>();
		String defaults = ConfigHandlerExtraBitManipulation.BIT_TOOL_DEFAULT_VALUES;
		String storageLoc = ConfigHandlerExtraBitManipulation.BIT_TOOL_PER_TOOL_OR_PER_CLIENT;
		String inChat = ConfigHandlerExtraBitManipulation.BIT_TOOL_DISPLAY_IN_CHAT;
		addChildElementsToDummyElement(configFile, defaults, defaults + " " + dataCatagory,
				"Configures " + dataCatagoryLower + " data default values.", childElements, ClientEntry.class);
		addChildElementsToDummyElement(configFile, storageLoc, storageLoc + " " + dataCatagory, "Configures whether " + dataCatagoryLower +
				" data is stored/assessed on/from individual tools or on/from the modeling data client config file.", childElements, ClientEntry.class);
		addChildElementsToDummyElement(configFile, inChat, inChat + " " + dataCatagory, "Configures whether changes to " +
				dataCatagoryLower + " data are displayed in chat.", childElements, ClientEntry.class);
		addElementsToDummyElement(dataCatagory + " Data Settings", hoverText.replace("@@@", dataCatagoryLower),
				configElementsToolData, childElements, ClientEntry.class);
	}
	
	private static void addDummyElementsOfProcessedChildElementSetsToDummyElement(Configuration configFile, List<IConfigElement> configElements,
			ConfigShapeRender[] config, Class configClass, String... names)
	{
		List<IConfigElement> childElements = new ArrayList<IConfigElement>();
		int startLen = names.length;
		for (int i = 0; i < startLen - 4; i += 2)
		{
			addChildElementsToDummyElement(configFile, names[i], names[i + 1], childElements, configClass);
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
		addDummyElementsOfChildElementSetsToDummyElement(configFile, childElements, configClass, processedNames);
		addElementsToDummyElement(names[startLen - 2], names[startLen - 1], configElements, childElements, configClass);
	}
	
	private static String getInsertText(int shapeIndex)
	{
		boolean removeBits = shapeIndex % 2 == 0;
		return (shapeIndex == 0 ? "Straight" : (shapeIndex == 1 ? "Flat" : "Curved")) + " Sculpting " + (removeBits ? "Wire's " : "Spade's ") +
				(shapeIndex > 1 ? "spherical/ellipsoidal" : "cubic/cuboidal") + " bit " + (removeBits ? "removal" : "addition") + " area";
	}
	
	private static void addDummyElementsOfProcessedChildElementSetsToDummyElement(Configuration configFile, List<IConfigElement> configElements,
			Map<Item, ConfigNamed> configs, String name, String toolTip, Class configClass)
	{
		int len = configs.size();
		boolean isRecipe = configClass == RecipeEntry.class;
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
		addDummyElementsOfChildElementSetsToDummyElement(configFile, configElements, configClass, processedNames);
	}
	
	private static void addDummyElementsOfChildElementSetsToDummyElement(Configuration configFile,
			List<IConfigElement> configElements, Class configClass, String... names)
	{
		List<IConfigElement> childElements = new ArrayList<IConfigElement>();
		int len = names.length;
		if (len < 2)
			return;
		
		boolean isRecipe = configClass == RecipeEntry.class;
		if (isRecipe)
			childElements.addAll(getChildElements(configFile, ConfigHandlerExtraBitManipulation.RECIPES_DISABLE));
		
		int inc = isRecipe ? 1 : 2;
		for (int i = 0; i < names.length - 2; i += inc)
		{
			if (isRecipe)
			{
				addRecipeChildElementsToDummyElement(names[i], childElements, configClass);
			}
			else
			{
				addChildElementsToDummyElement(configFile, names[i], names[i + 1], childElements, configClass);
			}
		}
		addElementsToDummyElement(names[len - 2], names[len - 1], configElements, childElements, configClass);
	}
	
	private static void addRecipeChildElementsToDummyElement(String name, List<IConfigElement> configElements, Class configClass)
	{
		addChildElementsToDummyElement(ConfigHandlerExtraBitManipulation.configFileCommon,
				name, "Configures the recipe type and configuration for the " + name, configElements, configClass);
	}
	
	private static void addElementsToDummyElement(String text, String toolTip,
			List<IConfigElement> configElements, List<IConfigElement> childElements, Class configClass)
	{
		DummyCategoryElement dummyElement;
		if (configClass == null)
		{
			dummyElement = new DummyCategoryElement(text, toolTip, childElements);
		}
		else
		{
			dummyElement = new DummyCategoryElement(text, toolTip, childElements, configClass);
			if (configClass == RecipeEntry.class)
				dummyElement.setRequiresMcRestart(true);
		}
		configElements.add(dummyElement);
	}
	
	private static void addChildElementsToDummyElement(Configuration configFile, String text,
			String toolTip, List<IConfigElement> configElements, Class configClass)
	{
		addChildElementsToDummyElement(configFile, text, text.toLowerCase(), toolTip, configElements, configClass);
	}
	
	private static void addChildElementsToDummyElement(Configuration configFile, String text,
			String catagory, String toolTip, List<IConfigElement> configElements, Class configClass)
	{
		addElementsToDummyElement(text, toolTip, configElements, getChildElements(configFile, catagory), configClass);
	}
	
	private static List<IConfigElement> getChildElements(Configuration configFile, String key)
	{
		return new ConfigElement(configFile.getCategory(key.toLowerCase())).getChildElements();
	}
	
	public static class FileEntry extends CategoryEntry
	{
		
		public FileEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
		{
			super(owningScreen, owningEntryList, prop);
		}
		
		protected String getFileName()
		{
			return "";
		}
		
		@Override
		protected GuiScreen buildChildScreen()
		{
			if (owningScreen.title.endsWith("/"))
				owningScreen.title += getFileName() + ".cfg";
			
			return super.buildChildScreen();
		}
		
	}
	
	public static class ClientEntry extends FileEntry
	{
		
		public ClientEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
		{
			super(owningScreen, owningEntryList, prop);
		}
		
		@Override
		protected String getFileName()
		{
			return "client";
		}
		
	}
	
	public static class ServerEntry extends FileEntry
	{
		
		public ServerEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
		{
			super(owningScreen, owningEntryList, prop);
		}
		
		@Override
		protected String getFileName()
		{
			return "server";
		}
		
	}
	
	public static class CommonEntry extends FileEntry
	{
		
		public CommonEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
		{
			super(owningScreen, owningEntryList, prop);
		}
		
		@Override
		protected String getFileName()
		{
			return "common";
		}
		
	}
	
	public static class RecipeEntry extends CommonEntry
	{
		
		public RecipeEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
		{
			super(owningScreen, owningEntryList, prop);
		}
		
	}
	
}