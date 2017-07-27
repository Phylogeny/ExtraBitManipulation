package com.phylogeny.extrabitmanipulation.armor;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.ItemStackHandler;

import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.init.RenderLayersExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

public class ItemStackHandlerArmorItem extends ItemStackHandler
{
	private int partIndex;
	private NBTTagCompound data;
	private boolean clientSide;
	
	public ItemStackHandlerArmorItem(NonNullList<ItemStack> stacks, NBTTagCompound nbt, int partIndex, boolean clientSide)
	{
		super(stacks);
		data = nbt.getCompoundTag(NBTKeys.ARMOR_DATA);
		this.partIndex = partIndex;
		this.clientSide = clientSide;
	}
	
	@Nonnull
	public ItemStackHandlerArmorItem addSlot()
	{
		int size = this.stacks.size();
		NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(size + 1, ItemStack.EMPTY);
		for (int i = 0; i < size; i++)
		{
			stacks.set(i, this.stacks.get(i));
		}
		stacks.set(size, ItemStack.EMPTY);
		this.stacks = stacks;
		return this;
	}
	
	@Nonnull
	public ItemStack removeSlot(int slotIndex)
	{
		ItemStack stack = getStackInSlot(slotIndex);
		int size = this.stacks.size();
		NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(size - 1, ItemStack.EMPTY);
		for (int i = 0; i < size; i++)
		{
			if (slotIndex != i)
				stacks.set(i > slotIndex ? i - 1 : i, this.stacks.get(i));
		}
		this.stacks = stacks;
		return stack;
	}
	
	@Override
	public int getSlotLimit(int slotIndex)
	{
		return 1;
	}
	
	@Override
	protected void onContentsChanged(int slotIndex)
	{
		setData(slotIndex);
	}
	
	public void setData(int slotIndex)
	{
		NBTTagList movingParts = data.getTagList(NBTKeys.ARMOR_PART_DATA, NBT.TAG_LIST);
		NBTBase nbtBase = movingParts.get(partIndex);
		if (nbtBase.getId() == NBT.TAG_LIST)
		{
			if (clientSide)
				RenderLayersExtraBitManipulation.removeFromDisplayListsMaps(data);
			
			NBTTagList itemList = (NBTTagList) nbtBase;
			NBTTagCompound armorItemNbt = itemList.getCompoundTagAt(slotIndex);
			ItemStackHelper.saveStackToNBT(armorItemNbt, getStackInSlot(slotIndex), NBTKeys.ARMOR_ITEM);
			itemList.set(slotIndex, armorItemNbt);
			DataChiseledArmorPiece.setPartData(data, movingParts);
		}
	}
	
}