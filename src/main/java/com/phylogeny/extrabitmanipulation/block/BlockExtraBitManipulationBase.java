package com.phylogeny.extrabitmanipulation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import com.phylogeny.extrabitmanipulation.client.CreativeTabExtraBitManipulation;

public class BlockExtraBitManipulationBase extends Block
{
	private String name;
	
	@SuppressWarnings("null")
	public BlockExtraBitManipulationBase(Material material, String name)
	{
		super(material);
		this.name = name;
		setRegistryName(name);
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(CreativeTabExtraBitManipulation.CREATIVE_TAB);
		setHardness(0.5F);
	}
	
	public String getName()
	{
		return name;
	}
	
}