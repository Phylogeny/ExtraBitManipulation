package com.phylogeny.extrabitmanipulation.init;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemArmor.ArmorMaterial;

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
	chiseledHelmetDiamond, chiseledChestplateDiamond, chiseledLeggingsDiamond, chiseledBootsDiamond,
	chiseledHelmetIron, chiseledChestplateIron, chiseledLeggingsIron, chiseledBootsIron;
	
	public static void itemsInit()
	{
		diamondNugget = new ItemExtraBitManipulationBase("diamond_nugget");
		bitWrench = new ItemBitWrench("bit_wrench");
		sculptingLoop = new ItemSculptingTool(true, true, "sculpting_loop");
		sculptingSquare = new ItemSculptingTool(false, true, "sculpting_square");
		sculptingSpadeCurved = new ItemSculptingTool(true, false, "sculpting_spade_curved");
		sculptingSpadeSquared = new ItemSculptingTool(false, false, "sculpting_spade_squared");
		modelingTool = new ItemModelingTool("modeling_tool");
		modelingToolHead = new ItemExtraBitManipulationBase("modeling_tool_head"); 
		bitWrenchHead = new ItemExtraBitManipulationBase("bit_wrench_head"); 
		sculptingLoopHead = new ItemExtraBitManipulationBase("sculpting_loop_head");
		sculptingSquareHead = new ItemExtraBitManipulationBase("sculpting_square_head");
		sculptingSpadeCurvedHead = new ItemExtraBitManipulationBase("sculpting_spade_curved_head");
		sculptingSpadeSquaredHead = new ItemExtraBitManipulationBase("sculpting_spade_squared_head");
		chiseledHelmetDiamond = new ItemChiseledArmor("chiseled_helmet", ArmorMaterial.DIAMOND, ArmorType.HELMET, ArmorMovingPart.HEAD);
		chiseledChestplateDiamond = new ItemChiseledArmor("chiseled_chestplate", ArmorMaterial.DIAMOND, ArmorType.CHESTPLATE,
				ArmorMovingPart.TORSO, ArmorMovingPart.ARM_RIGHT, ArmorMovingPart.ARM_LEFT);
		chiseledLeggingsDiamond = new ItemChiseledArmor("chiseled_leggings", ArmorMaterial.DIAMOND, ArmorType.LEGGINGS,
				ArmorMovingPart.PELVIS, ArmorMovingPart.LEG_RIGHT, ArmorMovingPart.LEG_LEFT);
		chiseledBootsDiamond = new ItemChiseledArmor("chiseled_boots", ArmorMaterial.DIAMOND,
				ArmorType.BOOTS, ArmorMovingPart.FOOT_RIGHT, ArmorMovingPart.FOOT_LEFT);
		chiseledHelmetIron = new ItemChiseledArmor("chiseled_helmet_iron", ArmorMaterial.IRON, ArmorType.HELMET, ArmorMovingPart.HEAD);
		chiseledChestplateIron = new ItemChiseledArmor("chiseled_chestplate_iron", ArmorMaterial.IRON, ArmorType.CHESTPLATE,
				ArmorMovingPart.TORSO, ArmorMovingPart.ARM_RIGHT, ArmorMovingPart.ARM_LEFT);
		chiseledLeggingsIron = new ItemChiseledArmor("chiseled_leggings_iron", ArmorMaterial.IRON, ArmorType.LEGGINGS,
				ArmorMovingPart.PELVIS, ArmorMovingPart.LEG_RIGHT, ArmorMovingPart.LEG_LEFT);
		chiseledBootsIron = new ItemChiseledArmor("chiseled_boots_iron", ArmorMaterial.IRON,
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
		GameRegistry.register(chiseledHelmetDiamond);
		GameRegistry.register(chiseledChestplateDiamond);
		GameRegistry.register(chiseledLeggingsDiamond);
		GameRegistry.register(chiseledBootsDiamond);
		GameRegistry.register(chiseledHelmetIron);
		GameRegistry.register(chiseledChestplateIron);
		GameRegistry.register(chiseledLeggingsIron);
		GameRegistry.register(chiseledBootsIron);
		registerDefaultRecipe(Item.getItemFromBlock(BlocksExtraBitManipulation.bodyPartTemplate), "Bodypart Template", false, false, "minecraft:cobblestone");
	}
	
	private static void registerItemAndDefaultRecipe(Item item, String itemTitle,
			boolean isShapedDefault, boolean oreDictionaryDefault, String... recipeDefault)
	{
		String itemName = ((ItemExtraBitManipulationBase) item).getName();
		if (recipeDefault.length == 0)
			recipeDefault = new String[]{"", Reference.MOD_ID + ":" + itemName + "_head", "minecraft:iron_ingot", ""};
		
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