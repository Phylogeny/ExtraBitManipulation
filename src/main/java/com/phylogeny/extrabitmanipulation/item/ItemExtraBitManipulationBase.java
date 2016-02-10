package com.phylogeny.extrabitmanipulation.item;

import com.phylogeny.extrabitmanipulation.client.creativetab.CreativeTabExtraBitManipulation;

import net.minecraft.item.Item;

public class ItemExtraBitManipulationBase extends Item
{
	private String name;
	
	public ItemExtraBitManipulationBase(String name)
	{
		this.name = name;
		setUnlocalizedName(name);
		setCreativeTab(CreativeTabExtraBitManipulation.creativeTab);
	}
	
	public String getName()
	{
		return name;
	}
	
}