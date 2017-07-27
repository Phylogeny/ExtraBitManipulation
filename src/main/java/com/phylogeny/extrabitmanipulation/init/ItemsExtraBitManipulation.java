package com.phylogeny.extrabitmanipulation.init;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.phylogeny.extrabitmanipulation.config.ConfigProperty;
import com.phylogeny.extrabitmanipulation.config.ConfigRecipe;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRender;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRenderPair;
import com.phylogeny.extrabitmanipulation.item.ItemBitToolBase;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorMovingPart;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;
import com.phylogeny.extrabitmanipulation.item.ItemExtraBitManipulationBase;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingTool;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class ItemsExtraBitManipulation
{
	public static Item diamondNugget, bitWrench, sculptingLoop, sculptingSquare, sculptingSpadeCurved, sculptingSpadeSquared, modelingTool,
	modelingToolHead, bitWrenchHead, sculptingLoopHead, sculptingSquareHead, sculptingSpadeCurvedHead, sculptingSpadeSquaredHead,
	chiseledHelmet, chiseledChestplate, chiseledLeggings, chiseledBoots;
	
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
		chiseledHelmet = new ItemChiseledArmor("chiseled_helmet", EntityEquipmentSlot.HEAD, ArmorType.HELMET, ArmorMovingPart.HEAD);
		chiseledChestplate = new ItemChiseledArmor("chiseled_chestplate", EntityEquipmentSlot.CHEST, ArmorType.CHESTPLATE,
				ArmorMovingPart.TORSO, ArmorMovingPart.ARM_RIGHT, ArmorMovingPart.ARM_LEFT);
		chiseledLeggings = new ItemChiseledArmor("chiseled_leggings", EntityEquipmentSlot.LEGS, ArmorType.LEGGINGS,
				ArmorMovingPart.PELVIS, ArmorMovingPart.LEG_RIGHT, ArmorMovingPart.LEG_LEFT);
		chiseledBoots = new ItemChiseledArmor("chiseled_boots", EntityEquipmentSlot.FEET,
				ArmorType.BOOTS, ArmorMovingPart.FOOT_RIGHT, ArmorMovingPart.FOOT_LEFT);
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
		GameRegistry.register(diamondNugget);
		GameRegistry.register(chiseledHelmet);
		GameRegistry.register(chiseledChestplate);
		GameRegistry.register(chiseledLeggings);
		GameRegistry.register(chiseledBoots);
		registerDefaultRecipe(Item.getItemFromBlock(BlocksExtraBitManipulation.bodyPartTemplate), "Bodypart Template", false, false, "minecraft:cobblestone");
	}
	
	private static void registerItemAndDefaultRecipe(Item item, String itemTitle,
			boolean isShapedDefault, boolean oreDictionaryDefault, String... recipeDefault)
	{
		String itemName = ((ItemExtraBitManipulationBase) item).getName();
		if (recipeDefault.length == 0)
			recipeDefault = new String[]{"", Reference.MOD_ID + ":" + itemName + "Head", "minecraft:iron_ingot", ""};
		
		GameRegistry.register(item);
		registerDefaultRecipe(item, itemTitle, isShapedDefault, oreDictionaryDefault, recipeDefault);
	}
	
	private static void registerDefaultRecipe(Item item, String itemTitle, boolean isShapedDefault, boolean oreDictionaryDefault, String... recipeDefault)
	{
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