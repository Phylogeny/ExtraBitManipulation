package com.phylogeny.extrabitmanipulation.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.client.GuiHelper;

public class GuiButtonSelectTextured extends GuiButtonSelect
{
	private ResourceLocation texture;
	private float rightOffsetX;
	private boolean shiftRight;
	private static int scaleFactor;
	
	public GuiButtonSelectTextured(int buttonId, int x, int y, int width, int height, String text,
			String hoverText, int colorFirst, int colorSecond, ResourceLocation texture)
	{
		super(buttonId, x, y, width, height, text, hoverText, colorFirst, colorSecond);
		this.texture = texture;
		scaleFactor = GuiHelper.getScaleFactor();
	}
	
	public void setRightOffsetX(float textureOffsetX, boolean shiftRight)
	{
		this.rightOffsetX = textureOffsetX;
		this.shiftRight = shiftRight;
	}
	
	private void shiftRight()
	{
		if (shiftRight)
			GlStateManager.translate(rightOffsetX, 0, 0);
	}
	
	@Override
	protected void drawCustomRect()
	{
		GlStateManager.pushMatrix();
		shiftRight();
		GuiHelper.drawRect(x, y, x + width + rightOffsetX, y + height, selected ? colorFirst : colorSecond);
		GlStateManager.popMatrix();
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
	{
		super.drawButton(mc, mouseX, mouseY, partialTicks);
		if (!visible)
			return;
		
		GlStateManager.pushMatrix();
		ClientHelper.bindTexture(texture);
		if (hovered)
			GlStateManager.color(1, 1, 160 / 255.0F, 1);
		else
			GlStateManager.color(1, 1, 1, 1);
		
		shiftRight();
		double iconX = x;
		double iconY = y;
		double iconWidth = width + rightOffsetX;
		double iconHieght = height;
		if (scaleFactor == 3)
		{
			iconX += iconWidth / 6.0F;
			iconY += iconHieght / 6.0F;
			iconWidth /= 1.5F;
			iconHieght /= 1.5F;
		}
		GuiHelper.drawTexturedRect(iconX, iconY, iconX + iconWidth, iconY + iconHieght);
		GlStateManager.popMatrix();
	}
	
}