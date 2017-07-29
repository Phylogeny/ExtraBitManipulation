package com.phylogeny.extrabitmanipulation.client.gui.armor;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.armor.ArmorItem;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.packet.PacketCursorStack;

public class GuiListEntryArmorItem extends GuiListEntryChiseledArmor<ArmorItem>
{
	private boolean slotHovered;
	
	public GuiListEntryArmorItem(GuiListChiseledArmor<ArmorItem> listChiseledArmor, ArmorItem armorItem)
	{
		super(listChiseledArmor, armorItem);
	}
	
	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
	{
		slotHovered = mouseX > x + 4 && mouseX < x + 23 && mouseY > y && mouseY < y + 19;
		x += 5;
		y += 1;
		RenderHelper.enableGUIStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		ClientHelper.bindTexture(GuiChiseledArmor.TEXTURE_GUI);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 230, 18, 18, 512, 512);
		mc.fontRendererObj.drawString("" + (slotIndex + 1), x + 21, y + 5, -1);
	}
	
	@Override
	public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY)
	{
		super.mousePressed(slotIndex, mouseX, mouseY, mouseEvent, relativeX, relativeY);
		if (slotClicked(relativeX, relativeY))
		{
			ItemStack stack = mc.player.inventory.getItemStack();
			if (stack.isEmpty() && mouseEvent == 2 && mc.player.capabilities.isCreativeMode)
			{
				ItemStack stack2 = entryObject.getStack().copy();
				if (!stack2.isEmpty())
				{
					mc.player.inventory.setItemStack(stack2);
					ExtraBitManipulation.packetNetwork.sendToServer(new PacketCursorStack(stack2));
				}
			}
			else
			{
				boolean shift = listChiseledArmor.guiChiseledArmor.isShiftKeyDown();
				if (!stack.isEmpty() || shift)
					listChiseledArmor.guiChiseledArmor.modifyArmorItemListData(slotIndex, shift ? ItemStack.EMPTY : stack);
			}
		}
		return false;
	}
	
	private boolean slotClicked(int relativeX, int relativeY)
	{
		return relativeX > 4 && relativeX < 23 && relativeY > 0 && relativeY < 19;
	}
	
	public boolean isSlotHovered()
	{
		return slotHovered;
	}
	
	public ItemStack getStack()
	{
		return entryObject.getStack();
	}
	
}