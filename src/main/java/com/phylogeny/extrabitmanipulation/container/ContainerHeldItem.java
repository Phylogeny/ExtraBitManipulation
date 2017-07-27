package com.phylogeny.extrabitmanipulation.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

public class ContainerHeldItem extends ContainerPlayerInventory
{
	
	public ContainerHeldItem(EntityPlayer player, int startX, int startY)
	{
		super(player, startX, startY);
	}
	
	@Override
	@Nullable
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
	{
		return slotId == player.inventory.currentItem ? ItemStack.EMPTY : super.slotClick(slotId, dragType, clickTypeIn, player);
	}
	
}