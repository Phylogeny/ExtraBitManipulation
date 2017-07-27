package com.phylogeny.extrabitmanipulation.init;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.phylogeny.extrabitmanipulation.block.BlockBodyPartTemplate;

public class BlocksExtraBitManipulation
{
	public static Block bodyPartTemplate;
	
	public static void blocksInit()
	{
		bodyPartTemplate = new BlockBodyPartTemplate("bodypart_template");
	}
	
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(bodyPartTemplate);
	}
	
}