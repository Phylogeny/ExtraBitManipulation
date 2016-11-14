package com.phylogeny.extrabitmanipulation.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class GuiButtonTab extends GuiButtonBase
{
	private ItemStack iconStack;
	private float u, v;
	private int uWidth, vHeight;
	
	public GuiButtonTab(int buttonId, int x, int y, int widthIn, int heightIn, String hoverText,
			ItemStack iconStack, float u, float v, int uWidth, int vHeight)
	{
		super(buttonId, x, y, widthIn, heightIn, "", hoverText);
		this.u = u;
		this.v = v;
		this.uWidth = uWidth;
		this.vHeight = vHeight;
		this.iconStack = iconStack;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (!visible)
			return;
		
		super.drawButton(mc, mouseX, mouseY);
		
		if (uWidth > 0 && vHeight > 0)
		{
			mc.getTextureManager().bindTexture(GuiBitMapping.GUI_TEXTURE);
			drawScaledCustomSizeModalRect(xPosition + 4 + getOffsetX(), yPosition + 4, u, v, uWidth, vHeight, 19, 18, 256, 256);
		}
		if (selected)
		{
			mc.getTextureManager().bindTexture(GuiBitMapping.GUI_TEXTURE);
			GlStateManager.color(1, 1, 1);
			drawTexturedModalRect(xPosition - 2, yPosition, 67, 219, 29, 26);
		}
	}

	private int getOffsetX()
	{
		return selected ? -2 : 0;
	}
	
	public ItemStack getIconStack()
	{
		return iconStack;
	}
	
	public void setIconStack(ItemStack iconStack)
	{
		this.iconStack = iconStack;
	}
	
	public void renderIconStack()
	{
		if (iconStack != null)
		{
			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5, 0, 0);
			Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(iconStack, xPosition + 5 + getOffsetX(), yPosition + 5);
			GlStateManager.popMatrix();
			RenderHelper.disableStandardItemLighting();
		}
	}
	
}