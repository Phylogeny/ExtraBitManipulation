package com.phylogeny.extrabitmanipulation.api.jei.icon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public abstract class CategoryIconResourceBase extends CategoryIconBase
{
	private int textureWidth, textureHeight, u, v;
	
	public CategoryIconResourceBase(int u, int v, int width, int height, int textureWidth, int textureHeight)
	{
		super(width, height);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.u = u;
		this.v = v;
	}
	
	protected abstract void bindTexture(Minecraft minecraft);
	
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