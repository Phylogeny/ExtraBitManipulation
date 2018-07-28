package com.phylogeny.extrabitmanipulation.init;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.phylogeny.extrabitmanipulation.config.ConfigProperty;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRender;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRenderPair;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorMovingPart;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;
import com.phylogeny.extrabitmanipulation.item.ItemExtraBitManipulationBase;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingTool;
import com.phylogeny.extrabitmanipulation.reference.Configs;

public class ItemsExtraBitManipulation
{
	private static boolean clientSide;
	public static Item diamondNugget, bitWrench, sculptingLoop, sculptingSquare, sculptingSpadeCurved, sculptingSpadeSquared, modelingTool,
						modelingToolHead, bitWrenchHead, sculptingLoopHead, sculptingSquareHead, sculptingSpadeCurvedHead,
						sculptingSpadeSquaredHead, chiseledHelmetDiamond, chiseledChestplateDiamond, chiseledLeggingsDiamond, chiseledBootsDiamond,
						chiseledHelmetIron, chiseledChestplateIron, chiseledLeggingsIron, chiseledBootsIron;
	
	public static void itemsInit(FMLPreInitializationEvent event)
	{
		chiseledHelmetDiamond = new ItemChiseledArmor("chiseled_helmet", ArmorMaterial.DIAMOND, EntityEquipmentSlot.HEAD, ArmorType.HELMET, ArmorMovingPart.HEAD);
		chiseledChestplateDiamond = new ItemChiseledArmor("chiseled_chestplate", ArmorMaterial.DIAMOND, EntityEquipmentSlot.CHEST, ArmorType.CHESTPLATE,
				ArmorMovingPart.TORSO, ArmorMovingPart.ARM_RIGHT, ArmorMovingPart.ARM_LEFT);
		chiseledLeggingsDiamond = new ItemChiseledArmor("chiseled_leggings", ArmorMaterial.DIAMOND, EntityEquipmentSlot.LEGS, ArmorType.LEGGINGS,
				ArmorMovingPart.PELVIS, ArmorMovingPart.LEG_RIGHT, ArmorMovingPart.LEG_LEFT);
		chiseledBootsDiamond = new ItemChiseledArmor("chiseled_boots", ArmorMaterial.DIAMOND, EntityEquipmentSlot.FEET,
				ArmorType.BOOTS, ArmorMovingPart.FOOT_RIGHT, ArmorMovingPart.FOOT_LEFT);
		chiseledHelmetIron = new ItemChiseledArmor("chiseled_helmet", ArmorMaterial.IRON, EntityEquipmentSlot.HEAD, ArmorType.HELMET, ArmorMovingPart.HEAD);
		chiseledChestplateIron = new ItemChiseledArmor("chiseled_chestplate", ArmorMaterial.IRON, EntityEquipmentSlot.CHEST, ArmorType.CHESTPLATE,
				ArmorMovingPart.TORSO, ArmorMovingPart.ARM_RIGHT, ArmorMovingPart.ARM_LEFT);
		chiseledLeggingsIron = new ItemChiseledArmor("chiseled_leggings", ArmorMaterial.IRON, EntityEquipmentSlot.LEGS, ArmorType.LEGGINGS,
				ArmorMovingPart.PELVIS, ArmorMovingPart.LEG_RIGHT, ArmorMovingPart.LEG_LEFT);
		chiseledBootsIron = new ItemChiseledArmor("chiseled_boots", ArmorMaterial.IRON, EntityEquipmentSlot.FEET,
				ArmorType.BOOTS, ArmorMovingPart.FOOT_RIGHT, ArmorMovingPart.FOOT_LEFT);
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
		initBitToolProperties(bitWrench, "Bit Wrench");
		initBitToolProperties(sculptingLoop, "Curved Sculpting Wire");
		initBitToolProperties(sculptingSquare, "Straight Sculpting Wire");
		initBitToolProperties(sculptingSpadeCurved, "Curved Sculpting Spade");
		initBitToolProperties(sculptingSpadeSquared, "Flat Sculpting Spade");
		initBitToolProperties(modelingTool, "Modeling Tool");
		clientSide = event.getSide() == Side.CLIENT;
	}
	
	private static void initBitToolProperties(Item item, String itemTitle)
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
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(diamondNugget, bitWrench, sculptingLoop, sculptingSquare, sculptingSpadeCurved,
				sculptingSpadeSquared, modelingTool, modelingToolHead, bitWrenchHead, sculptingLoopHead, sculptingSquareHead,
				sculptingSpadeCurvedHead, sculptingSpadeSquaredHead, chiseledHelmetDiamond, chiseledChestplateDiamond, chiseledLeggingsDiamond,
				chiseledBootsDiamond, chiseledHelmetIron, chiseledChestplateIron, chiseledLeggingsIron, chiseledBootsIron,
				(new ItemBlock(BlocksExtraBitManipulation.bodyPartTemplate)).setRegistryName(BlocksExtraBitManipulation.bodyPartTemplate.getRegistryName()));
		
		if (clientSide)
			ModelRegistration.registerItemModels();
	}
	
}