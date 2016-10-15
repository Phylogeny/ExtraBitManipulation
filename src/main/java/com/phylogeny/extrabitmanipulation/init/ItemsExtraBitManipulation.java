package com.phylogeny.extrabitmanipulation.init;

import com.phylogeny.extrabitmanipulation.config.ConfigProperty;
import com.phylogeny.extrabitmanipulation.config.ConfigRecipe;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRender;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRenderPair;
import com.phylogeny.extrabitmanipulation.item.ItemBitToolBase;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.item.ItemExtraBitManipulationBase;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingTool;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemsExtraBitManipulation
{
	public static Item diamondNugget, bitWrench, sculptingLoop, sculptingSquare, sculptingSpadeCurved, sculptingSpadeSquared, modelingTool,
	modelingToolHead, bitWrenchHead, sculptingLoopHead, sculptingSquareHead, sculptingSpadeCurvedHead, sculptingSpadeSquaredHead;
	
	public static void itemsInit()
	{
		diamondNugget = new ItemExtraBitManipulationBase("DiamondNugget"); 
		bitWrench = new ItemBitWrench("BitWrench"); 
		sculptingLoop = new ItemSculptingTool(true, true, "SculptingLoop");
		sculptingSquare = new ItemSculptingTool(false, true, "SculptingSquare");
		sculptingSpadeCurved = new ItemSculptingTool(true, false, "SculptingSpadeCurved");
		sculptingSpadeSquared = new ItemSculptingTool(false, false, "SculptingSpadeSquared");
		modelingTool = new ItemModelingTool("ModelingTool");
		modelingToolHead = new ItemExtraBitManipulationBase("ModelingToolHead"); 
		bitWrenchHead = new ItemExtraBitManipulationBase("BitWrenchHead"); 
		sculptingLoopHead = new ItemExtraBitManipulationBase("SculptingLoopHead");
		sculptingSquareHead = new ItemExtraBitManipulationBase("SculptingSquareHead");
		sculptingSpadeCurvedHead = new ItemExtraBitManipulationBase("SculptingSpadeCurvedHead");
		sculptingSpadeSquaredHead = new ItemExtraBitManipulationBase("SculptingSpadeSquaredHead");
		registerItemAndDefaultRecipe(bitWrench, "Bit Wrench", true, false);
		registerItemAndDefaultRecipe(sculptingLoop, "Curved Sculpting Wire", true, false);
		registerItemAndDefaultRecipe(sculptingSquare, "Straight Sculpting Wire", true, false);
		registerItemAndDefaultRecipe(sculptingSpadeCurved, "Curved Sculpting Spade", true, false);
		registerItemAndDefaultRecipe(sculptingSpadeSquared, "Flat Sculpting Spade", true, false);
		registerItemAndDefaultRecipe(modelingTool, "Modeling Tool", true, false);
		registerItemAndDefaultRecipe(modelingToolHead, "Modeling Tool Head", true, true,
				"", "", "", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "");
		registerItemAndDefaultRecipe(bitWrenchHead, "Bit Wrench Head", true, true,
				"nuggetDiamond", "", "nuggetDiamond", "nuggetDiamond", "", "nuggetDiamond", "", "nuggetDiamond", "");
		registerItemAndDefaultRecipe(sculptingLoopHead, "Curved Sculpting Wire Head", true, true,
				"", "nuggetDiamond", "", "nuggetDiamond", "", "nuggetDiamond", "", "nuggetDiamond", "");
		registerItemAndDefaultRecipe(sculptingSquareHead, "Straight Sculpting Wire Head", true, true,
				"nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "", "nuggetDiamond", "", "nuggetDiamond", "");
		registerItemAndDefaultRecipe(sculptingSpadeCurvedHead, "Curved Sculpting Spade Head", true, true,
				"", "nuggetDiamond", "", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "", "nuggetDiamond", "");
		registerItemAndDefaultRecipe(sculptingSpadeSquaredHead, "Flat Sculpting Spade Head", true, true,
				"nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "nuggetDiamond", "", "nuggetDiamond", "");
		GameRegistry.registerItem(diamondNugget, ((ItemExtraBitManipulationBase) diamondNugget).getName());
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
		Configs.itemRecipeMap.put(item, new ConfigRecipe(itemTitle, true, isShapedDefault, oreDictionaryDefault, recipeDefault));
		if (item instanceof ItemBitToolBase)
		{
			boolean isSculptingTool = item instanceof ItemSculptingTool;
			Configs.itemPropertyMap.put(item, new ConfigProperty(itemTitle, true, isSculptingTool ? 2000000 : (item instanceof ItemBitWrench ? 5000 : 1000)));
			if (isSculptingTool)
			{
				ItemSculptingTool itemTool = (ItemSculptingTool) item;
				ConfigShapeRender boundingBox = itemTool.removeBits() ? Configs.itemShapes[0] : Configs.itemShapes[1];
				Configs.itemShapeMap.put(item, new ConfigShapeRenderPair(boundingBox, itemTool.removeBits() ? Configs.itemShapes[2] : Configs.itemShapes[3]));
			}
		}
	}
	
}