package com.phylogeny.extrabitmanipulation.container;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

import com.phylogeny.extrabitmanipulation.armor.ArmorItem;
import com.phylogeny.extrabitmanipulation.armor.DataChiseledArmorPiece;
import com.phylogeny.extrabitmanipulation.armor.ItemStackHandlerArmorItem;
import com.phylogeny.extrabitmanipulation.client.gui.armor.SlotArmorItem;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;

public class ContainerChiseledArmor extends ContainerPlayerInventory
{
	private ItemStackHandlerArmorItem[][] armorItemInventories = new ItemStackHandlerArmorItem[4][3];
	
	public ContainerChiseledArmor(EntityPlayer player, int startX, int startY)
	{
		super(player, startX, startY);
		for (int i = 0; i < 4; i++)
		{
			ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.values()[5 - i]);
			if (!ItemStackHelper.isChiseledArmorStack(stack))
				continue;
			
			DataChiseledArmorPiece armorPiece = new DataChiseledArmorPiece(ItemStackHelper.getNBTOrNew(stack), ArmorType.values()[i]);
			if (!stack.hasTagCompound())
			{
				NBTTagCompound nbt = new NBTTagCompound();
				armorPiece.saveToNBT(nbt);
				stack.setTagCompound(nbt);
			}
			for (int partIndex = 0; partIndex < ((ItemChiseledArmor) stack.getItem()).MOVING_PARTS.length; partIndex++)
			{
				List<ArmorItem> armorItems = armorPiece.getArmorItemsForPart(partIndex);
				NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(armorItems.size(), ItemStack.EMPTY);
				for (int j = 0; j < armorItems.size(); j++)
				{
					stacks.set(j, armorItems.get(j).getStack());
				}
				armorItemInventories[i][partIndex] = new ItemStackHandlerArmorItem(stacks, ItemStackHelper.getNBT(stack), partIndex, player.world.isRemote);
				for (int j = 0; j < armorItems.size(); j++)
				{
					addSlotToContainer(new SlotArmorItem(armorItemInventories[i][partIndex], j, 0, 0));
				}
			}
		}
	}
	
	public void addSlot(int stackIndex, int partIndex, int slotNumber, int slotIndex)
	{
		SlotArmorItem slot = new SlotArmorItem(armorItemInventories[stackIndex][partIndex].addSlot(), slotIndex, 0, 0);
		inventorySlots.add(slotNumber, slot);
		for (int i = 0; i < inventorySlots.size(); i++)
		{
			inventorySlots.get(i).slotNumber = i;
		}
		int size = inventoryItemStacks.size();
		NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(size + 1, ItemStack.EMPTY);
		for (int i = 0; i < size; i++)
		{
			stacks.set(i >= slotNumber ? i + 1 : i, inventoryItemStacks.get(i));
		}
		stacks.set(slotNumber, ItemStack.EMPTY);
		inventoryItemStacks = stacks;
	}
	
	public ItemStack removeSlot(int stackIndex, int partIndex, int slotNumber, int slotIndex)
	{
		ItemStackHandlerArmorItem itemHandler = armorItemInventories[stackIndex][partIndex];
		ItemStack stack = itemHandler.removeSlot(slotIndex);
		int size = inventoryItemStacks.size();
		NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(size - 1, ItemStack.EMPTY);
		for (int i = 0; i < size; i++)
		{
			if (slotNumber != i)
				stacks.set(i > slotNumber ? i - 1 : i, inventoryItemStacks.get(i));
		}
		inventoryItemStacks = stacks;
		inventorySlots.remove(slotNumber);
		int offset = itemHandler.getSlots() - slotIndex;
		for (int i = slotNumber; i < slotNumber + offset; i++)
		{
			SlotArmorItem slot = new SlotArmorItem(itemHandler, i - slotNumber + slotIndex, 0, 0);
			slot.slotNumber = i;
			inventorySlots.set(i, slot);
		}
		return stack;
	}
	
	@Override
	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);
		if (slot != null && slot.getHasStack())
		{
			ItemStack stack2 = slot.getStack();
			stack = stack2.copy();
			if (index < 9)
			{
				if (!mergeItemStack(stack2, 9, SIZE_MAIN_INVENTORY, false))
					return ItemStack.EMPTY;
			}
			if (index >= SIZE_MAIN_INVENTORY)
			{
				if (!mergeItemStack(stack2, 0, SIZE_MAIN_INVENTORY, false))
					return ItemStack.EMPTY;
			}
			else if (!mergeItemStack(stack2, 0, 9, true))
			{
				return ItemStack.EMPTY;
			}
			if (stack2.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}
		return stack;
	}
	
}