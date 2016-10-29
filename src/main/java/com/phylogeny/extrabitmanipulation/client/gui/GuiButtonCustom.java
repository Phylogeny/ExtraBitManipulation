package com.phylogeny.extrabitmanipulation.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonCustom extends GuiButtonBase
{
	
	public GuiButtonCustom(int buttonId, int x, int y, int widthIn, int heightIn, String text, String hoverText)
	{
		super(buttonId, x, y, widthIn, heightIn, text, hoverText);
	}
	
	protected void drawCustomRect() {}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (!visible)
			return;
		
		super.drawButton(mc, mouseX, mouseY);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		drawCustomRect();
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