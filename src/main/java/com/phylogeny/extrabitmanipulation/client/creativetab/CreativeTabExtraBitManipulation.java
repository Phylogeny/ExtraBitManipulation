package com.phylogeny.extrabitmanipulation.client.creativetab;

import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabExtraBitManipulation
{
	
	public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(Reference.MOD_ID)
	{
		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem()
		{
			return new ItemStack(ItemsExtraBitManipulation.sculptingLoop);
		}
	};
	
}