package com.phylogeny.extrabitmanipulation.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerModelingTool extends Container
{
	
	public ContainerModelingTool(IInventory playerInventory)
	{
		for (int k = 0; k < 3; ++k)
		{
			for (int l = 0; l < 9; ++l)
			{
				addSlotToContainer(new Slot(playerInventory, l + k * 9 + 9, 36 + l * 18 + 24, 137 + k * 18));
			}
		}
		
		for (int i1 = 0; i1 < 9; ++i1)
		{
			addSlotToContainer(new Slot(playerInventory, i1, 36 + i1 * 18 + 24, 195));
		}
	}
	
	@Override
	@Nullable
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
	{
		return slotId - 27 == player.inventory.currentItem ? null : super.slotClick(slotId, dragType, clickTypeIn, player);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}
	
	@Override
	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		return null;
	}
	
}