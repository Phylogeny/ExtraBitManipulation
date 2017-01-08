package com.phylogeny.extrabitmanipulation.api.jei.icon;

import mezz.jei.api.gui.IDrawableStatic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class CategoryIconBase implements IDrawableStatic
{
	private int textureWidth, textureHeight, u, v, width, height;
	
	public CategoryIconBase(int u, int v, int width, int height, int textureWidth, int textureHeight)
	{
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public int getWidth()
	{
		return width;
	}
	
	@Override
	public int getHeight()
	{
		return height;
	}
	
	@Override
	public void draw(Minecraft minecraft)
	{
		draw(minecraft, 0, 0);
	}
	
	@Override
	public void draw(Minecraft minecraft, int xOffset, int yOffset)
	{
		draw(minecraft, xOffset, yOffset, 0, 0, 0, 0);
	}
	
	protected void bindTexture(@SuppressWarnings("unused") Minecraft minecraft) {}
	
	@Override
	public void draw(Minecraft minecraft, int xOffset, int yOffset, int maskTop, int maskBottom, int maskLeft, int maskRight)
	{
		bindTexture(minecraft);
		int x = xOffset + maskLeft;
		int y = yOffset + maskTop;
		int u = this.u + maskLeft;
		int v = this.v + maskTop;
		int width = this.width - maskRight - maskLeft;
		int height = this.height - maskBottom - maskTop;
		Gui.drawScaledCustomSizeModalRect(x, y, u, v, textureWidth, textureHeight, width, height, textureWidth, textureHeight);
	}
	
}