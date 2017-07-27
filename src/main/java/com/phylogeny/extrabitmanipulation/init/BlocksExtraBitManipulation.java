package com.phylogeny.extrabitmanipulation.init;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.phylogeny.extrabitmanipulation.block.BlockBodyPartTemplate;

public class BlocksExtraBitManipulation
{
	public static Block bodyPartTemplate;
	
	public static void blocksInit()
	{
		bodyPartTemplate = new BlockBodyPartTemplate("bodypart_template");
		registerBlock(bodyPartTemplate);
	}
	
	public static void registerBlock(Block block)
	{
		GameRegistry.register(block);
		GameRegistry.register((new ItemBlock(block)).setRegistryName(block.getRegistryName()));
	}
	
}