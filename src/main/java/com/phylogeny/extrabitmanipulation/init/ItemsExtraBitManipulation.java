package com.phylogeny.extrabitmanipulation.init;

import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingLoop;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemsExtraBitManipulation
{
	public static Item BitWrench;
	public static Item SculptingLoop;
	
	public static void itemsInit()
	{
		BitWrench = new ItemBitWrench();
		SculptingLoop = new ItemSculptingLoop();
		GameRegistry.registerItem(BitWrench, ((ItemBitWrench) BitWrench).getName());
		GameRegistry.registerItem(SculptingLoop, ((ItemSculptingLoop) SculptingLoop).getName());
	}
	
}