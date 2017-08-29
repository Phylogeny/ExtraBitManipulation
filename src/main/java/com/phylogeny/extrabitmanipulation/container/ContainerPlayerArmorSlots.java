package com.phylogeny.extrabitmanipulation.container;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import com.phylogeny.extrabitmanipulation.armor.capability.ChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.armor.capability.IChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;

public class ContainerPlayerArmorSlots extends ContainerPlayer
{
	
	public ContainerPlayerArmorSlots(InventoryPlayer playerInventory, boolean localWorld, EntityPlayer player)
	{
		super(playerInventory, localWorld, player);
		inventorySlots.get(inventorySlots.size() - 1).xPos += 18;
		IChiseledArmorSlotsHandler cap = ChiseledArmorSlotsHandler.getCapability(player);
		for (int i = 0; i < ArmorType.values().length; i++)
		{
			addSlotToContainer(new SlotItemHandler(cap, i, 77, 8 + i * 18));
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		Slot slot = inventorySlots.get(index);
		if (slot != null && slot.getHasStack())
		{
			ItemStack stack = slot.getStack();
			if (index > 8 && index < 45)
			{
				int i = 3 - EntityLiving.getSlotForItemStack(stack).getIndex();
				if (ChiseledArmorSlotsHandler.isItemValid(i, stack))
				{
					if (mergeItemStack(stack, i += 46, i + 1, false))
						slot.onTake(player, stack);
				}
			}
			else if (index > 45)
			{
				if (mergeItemStack(stack, 9, 45, false))
					slot.onTake(player, stack);
			}
		}
		return super.transferStackInSlot(player, index);
	}
	
}