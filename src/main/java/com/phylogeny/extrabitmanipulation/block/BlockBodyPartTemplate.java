package com.phylogeny.extrabitmanipulation.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBodyPartTemplate extends BlockExtraBitManipulationBase
{
	
	public BlockBodyPartTemplate(String name)
	{
		super(Material.GROUND, name);
		setHardness(0.2F);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		tooltip.add("The bits of this block are used as bodypart placeholders in the creation of chiseled armor.");
	}
	
}