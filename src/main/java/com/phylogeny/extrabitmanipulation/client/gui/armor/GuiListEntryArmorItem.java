package com.phylogeny.extrabitmanipulation.client.gui.armor;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;

import com.phylogeny.extrabitmanipulation.armor.ArmorItem;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;

public class GuiListEntryArmorItem extends GuiListEntryChiseledArmor<ArmorItem>
{
	private boolean slotHovered;
	private Slot slot;
	
	public GuiListEntryArmorItem(GuiListChiseledArmor<ArmorItem> listChiseledArmor, ArmorItem armorItem, int slotNumber)
	{
		super(listChiseledArmor, armorItem);
		slot = listChiseledArmor.guiChiseledArmor.inventorySlots.getSlot(slotNumber);
	}
	
	public Slot getSlot()
	{
		return slot;
	}
	
	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
	{
		slotHovered = mouseX > x + 4 && mouseX < x + 23 && mouseY > y && mouseY < y + 19;
		x += 5;
		y += 1;
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		ClientHelper.bindTexture(GuiChiseledArmor.TEXTURE_GUI);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 230, 18, 18, 512, 512);
		mc.fontRendererObj.drawString("" + (slotIndex + 1), x + 21, y + 5, -1);
		slot.xDisplayPosition = x - listChiseledArmor.left + 39;
		slot.yDisplayPosition = y - listChiseledArmor.top + 25;
	}
	
	@Override
	public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY)
	{
		super.mousePressed(slotIndex, mouseX, mouseY, mouseEvent, relativeX, relativeY);
		return slotClicked(relativeX, relativeY);
	}
	
	private boolean slotClicked(int relativeX, int relativeY)
	{
		return relativeX > 4 && relativeX < 23 && relativeY > 0 && relativeY < 19;
	}
	
	public boolean isSlotHovered()
	{
		return slotHovered;
	}
	
}