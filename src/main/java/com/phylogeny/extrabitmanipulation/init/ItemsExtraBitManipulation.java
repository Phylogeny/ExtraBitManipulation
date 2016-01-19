package com.phylogeny.extrabitmanipulation.init;

import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemsExtraBitManipulation
{
	public static Item BitWrench;
	
	public static void itemsInit()
	{
		BitWrench = new ItemBitWrench();
		GameRegistry.registerItem(BitWrench, ((ItemBitWrench) BitWrench).getName());
	}
	
}