package com.phylogeny.extrabitmanipulation.container;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotChiseledArmor extends SlotItemHandler
{
	private boolean disabled;
	
	public SlotChiseledArmor(IItemHandler itemHandler, int index, int xPosition, int yPosition)
	{
		super(itemHandler, index, xPosition, yPosition);
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
	
}