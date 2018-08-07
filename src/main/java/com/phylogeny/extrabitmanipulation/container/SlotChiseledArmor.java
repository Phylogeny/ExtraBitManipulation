package com.phylogeny.extrabitmanipulation.container;

import com.phylogeny.extrabitmanipulation.armor.capability.IChiseledArmorSlotsHandler;

import net.minecraftforge.items.SlotItemHandler;

public class SlotChiseledArmor extends SlotItemHandler
{
	private final int index;
	private boolean disabled;
	
	public SlotChiseledArmor(IChiseledArmorSlotsHandler itemHandler, int index, int xPosition, int yPosition)
	{
		super(itemHandler, index, xPosition, yPosition);
		this.index = index;
	}
	
	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}
	
	@Override
	public boolean getHasStack()
	{
		return disabled ? false : super.getHasStack();
	}
	
	@Override
	public void onSlotChanged()
	{
		((IChiseledArmorSlotsHandler) getItemHandler()).markSlotDirty(index);
	}
	
}