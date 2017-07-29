package com.phylogeny.extrabitmanipulation.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPlayerInventory extends Container
{
	
	public ContainerPlayerInventory(EntityPlayer player, int startX, int startY)
	{
		for (int i1 = 0; i1 < 9; ++i1)
		{
			addSlotToContainer(new Slot(player.inventory, i1, startX + i1 * 18, startY + 58));
		}
		for (int k = 0; k < 3; ++k)
		{
			for (int l = 0; l < 9; ++l)
			{
				addSlotToContainer(new Slot(player.inventory, l + k * 9 + 9, startX + l * 18, startY + k * 18));
			}
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}
	
	@Override
	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack stack = null;
		Slot slot = inventorySlots.get(index);
		if (slot != null && slot.getHasStack())
		{
			ItemStack stack2 = slot.getStack();
			stack = stack2 == null ? null : stack2.copy();
			if (index < 9)
			{
				if (!mergeItemStack(stack2, 9, 36, false))
					return null;
			}
			else if (!mergeItemStack(stack2, 0, 9, true))
			{
				return null;
			}
			if (stack2 == null)
				slot.putStack(null);
			else
				slot.onSlotChanged();
		}
		return stack;
	}
	
}