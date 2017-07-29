package com.phylogeny.extrabitmanipulation.item;

import net.minecraft.item.Item;

import com.phylogeny.extrabitmanipulation.client.CreativeTabExtraBitManipulation;

public class ItemExtraBitManipulationBase extends Item
{
	private String name;
	
	@SuppressWarnings("null")
	public ItemExtraBitManipulationBase(String name)
	{
		this.name = name;
		setRegistryName(name);
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(CreativeTabExtraBitManipulation.CREATIVE_TAB);
	}
	
	public String getName()
	{
		return name;
	}
	
}