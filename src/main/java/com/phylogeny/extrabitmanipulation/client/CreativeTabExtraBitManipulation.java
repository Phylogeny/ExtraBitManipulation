package com.phylogeny.extrabitmanipulation.client;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class CreativeTabExtraBitManipulation
{
	
	public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(Reference.MOD_ID)
	{
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem()
		{
			return ItemsExtraBitManipulation.sculptingLoop;
		}
	};
	
}