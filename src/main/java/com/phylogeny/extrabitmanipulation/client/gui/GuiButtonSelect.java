package com.phylogeny.extrabitmanipulation.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonSelect extends GuiButtonBase
{
	private int colorSelected, colorDeselected;
	
	public GuiButtonSelect(int buttonId, int x, int y, int widthIn, int heightIn, String text, String hoverText, int colorSelected, int colorDeselected)
	{
		super(buttonId, x, y, widthIn, heightIn, text, hoverText);
		this.colorSelected = colorSelected;
		this.colorDeselected = colorDeselected;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (!visible)
			return;
		
		super.drawButton(mc, mouseX, mouseY);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		drawRect(xPosition, yPosition, xPosition + width, yPosition + height, selected ? colorSelected : colorDeselected);
		int colorText = -1;
		if (packedFGColour != 0)
		{
			colorText = packedFGColour;
		}
		else if (!enabled)
		{
			colorText = 10526880;
		}
		else if (hovered)
		{
			colorText = 16777120;
		}
		mc.fontRendererObj.drawString(displayString, xPosition + width / 2 - mc.fontRendererObj.getStringWidth(displayString) / 2,
				yPosition + (height - 8) / 2, colorText);
	}
	
}