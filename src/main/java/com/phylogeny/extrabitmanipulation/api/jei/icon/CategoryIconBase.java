package com.phylogeny.extrabitmanipulation.api.jei.icon;

import mezz.jei.api.gui.IDrawableStatic;
import net.minecraft.client.Minecraft;

public abstract class CategoryIconBase implements IDrawableStatic
{
	protected int width, height;
	
	public CategoryIconBase(int width, int height)
	{
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
	
}