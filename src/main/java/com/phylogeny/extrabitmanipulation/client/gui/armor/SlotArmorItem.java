package com.phylogeny.extrabitmanipulation.client.gui.armor;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;

import com.phylogeny.extrabitmanipulation.armor.ItemStackHandlerArmorItem;

public class SlotArmorItem extends SlotItemHandler
{
	private boolean disabled;
	
	public SlotArmorItem(ItemStackHandlerArmorItem itemHandler, int index, int xPosition, int yPosition)
	{
		super(itemHandler, index, xPosition, yPosition);
	}
	
	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isEnabled()
	{
		return !disabled;
	}
	
	@Override
	public void onSlotChanged()
	{
		super.onSlotChanged();
		((ItemStackHandlerArmorItem) getItemHandler()).setData(getSlotIndex());
	}
	
}