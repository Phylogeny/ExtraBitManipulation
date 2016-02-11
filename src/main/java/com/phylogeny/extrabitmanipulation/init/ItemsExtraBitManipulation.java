package com.phylogeny.extrabitmanipulation.init;

import com.phylogeny.extrabitmanipulation.config.ConfigProperty;
import com.phylogeny.extrabitmanipulation.config.ConfigRecipe;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRender;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRenderPair;
import com.phylogeny.extrabitmanipulation.item.ItemBitToolBase;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.item.ItemExtraBitManipulationBase;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingTool;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemsExtraBitManipulation
{
	public static Item DiamondNugget, BitWrench, SculptingLoop, SculptingSquare, SculptingSpadeCurved, SculptingSpadeSquared,
		BitWrenchHead, SculptingLoopHead, SculptingSquareHead, SculptingSpadeCurvedHead, SculptingSpadeSquaredHead;
	
	public static void itemsInit()
	{
		DiamondNugget = new ItemExtraBitManipulationBase("DiamondNugget"); 
		BitWrench = new ItemBitWrench("BitWrench"); 
		SculptingLoop = new ItemSculptingTool(true, true, "SculptingLoop");
		SculptingSquare = new ItemSculptingTool(false, true, "SculptingSquare");
		SculptingSpadeCurved = new ItemSculptingTool(true, false, "SculptingSpadeCurved");
		SculptingSpadeSquared = new ItemSculptingTool(false, false, "SculptingSpadeSquared");
		BitWrenchHead = new ItemExtraBitManipulationBase("BitWrenchHead"); 
		SculptingLoopHead = new ItemExtraBitManipulationBase("SculptingLoopHead");
		SculptingSquareHead = new ItemExtraBitManipulationBase("SculptingSquareHead");
		SculptingSpadeCurvedHead = new ItemExtraBitManipulationBase("SculptingSpadeCurvedHead");
		SculptingSpadeSquaredHead = new ItemExtraBitManipulationBase("SculptingSpadeSquaredHead");
		registerItemAndDefaultRecipe(BitWrench, "Bit Wrench", true, false);
		registerItemAndDefaultRecipe(SculptingLoop, "Curved Sculpting Wire", true, false);
		registerItemAndDefaultRecipe(SculptingSquare, "Straight Sculpting Wire", true, false);
		registerItemAndDefaultRecipe(SculptingSpadeCurved, "Curved Sculpting Spade", true, false);
		registerItemAndDefaultRecipe(SculptingSpadeSquared, "Flat Sculpting Spade", true, false);
		registerItemAndDefaultRecipe(BitWrenchHead, "Bit Wrench Head", true, true,
				"nuggetDiamond", "", "nuggetDiamond", "nuggetDiamond", "", "nuggetDiamond", "", "nuggetDiamond", "");
		registerItemAndDefaultRecipe(SculptingLoopHead, "Curved Sculpting Wire Head", true, true,
				"", "nuggetDiamond", "", "nuggetDiamond", "", "nuggetDiamond", "", "nuggetDiamond", "");
		registerItemAndDefaultRecipe(SculptingSquareHead, "Straight Sculpting Wire Head", true, true,
				"nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "", "nuggetDiamond", "", "nuggetDiamond", "");
		registerItemAndDefaultRecipe(SculptingSpadeCurvedHead, "Curved Sculpting Spade Head", true, true,
				"", "nuggetDiamond", "", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "", "nuggetDiamond", "");
		registerItemAndDefaultRecipe(SculptingSpadeSquaredHead, "Flat Sculpting Spade Head", true, true,
				"nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "", "nuggetDiamond", "");
		registerItemAndDefaultRecipe(DiamondNugget, "Diamond Nugget", false, true,
				"nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond",
				"nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond");
	}

	private static void registerItemAndDefaultRecipe(Item item, String itemTitle, boolean isShapedDefault,
			boolean oreDictionaryDefault, String... recipeDefault)
	{
		String itemName = ((ItemExtraBitManipulationBase) item).getName();
		if (recipeDefault.length == 0)
		{
			recipeDefault = new String[9];
			for (int i = 0; i < recipeDefault.length; i++)
			{
				String entry = "";
				if (i == 2)
				{
					entry = Reference.MOD_ID + ":" + itemName + "Head";
				}
				else if (i == 4)
				{
					entry = "minecraft:iron_ingot";
				}
				recipeDefault[i] = entry;
			}
		}
		GameRegistry.registerItem(item, itemName);
		Configs.itemRecipeMap.put(item, new ConfigRecipe(itemTitle,
				true, isShapedDefault, oreDictionaryDefault, recipeDefault));
		if (item instanceof ItemBitToolBase)
		{
			ConfigProperty config;
			if (item instanceof ItemBitWrench)
			{
				config = new ConfigProperty(itemTitle, true, 5000);
			}
			else
			{
				config = new ConfigProperty(itemTitle, true, 2000000, 5, 32);
			}
			Configs.itemPropertyMap.put(item, config);
			if (item instanceof ItemSculptingTool)
			{
				ItemSculptingTool itemTool = (ItemSculptingTool) item;
				ConfigShapeRender boundingBox = itemTool.removeBits() ? Configs.itemShapes[0] : Configs.itemShapes[1];
				ConfigShapeRender envelopedShape = null;
				if (itemTool.isCurved())
				{
					envelopedShape = itemTool.removeBits() ? Configs.itemShapes[2] : Configs.itemShapes[3];
				}
				Configs.itemShapeMap.put(item, new ConfigShapeRenderPair(boundingBox, envelopedShape));
			}
		}
	}
	
}