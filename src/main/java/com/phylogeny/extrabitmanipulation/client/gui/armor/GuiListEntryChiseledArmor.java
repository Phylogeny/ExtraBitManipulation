package com.phylogeny.extrabitmanipulation.client.gui.armor;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;

public abstract class GuiListEntryChiseledArmor<E> implements GuiListExtended.IGuiListEntry
{
	protected final Minecraft mc;
	protected final GuiListChiseledArmor<E> listChiseledArmor;
	protected E entryObject;
	
	public GuiListEntryChiseledArmor(GuiListChiseledArmor<E> listChiseledArmor, E entryObject)
	{
		this.listChiseledArmor = listChiseledArmor;
		mc = listChiseledArmor.getParentGui().mc;
		this.entryObject = entryObject;
	}
	
	@SuppressWarnings("unused")
	public void updateScreen(boolean isSelected) {}
	
	@SuppressWarnings("unused")
	public void keyTyped(char typedChar, int keyCode) {}
	
	@Override
	public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY)
	{
		listChiseledArmor.selectListEntry(slotIndex);
		return false;
	}
	
	@Override
	public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {}
	
	@Override
	public void setSelected(int entryID, int insideLeft, int yPos) {}
	
}