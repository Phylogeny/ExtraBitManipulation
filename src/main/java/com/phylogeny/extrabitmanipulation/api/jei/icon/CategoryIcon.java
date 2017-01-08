package com.phylogeny.extrabitmanipulation.api.jei.icon;

import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class CategoryIcon extends CategoryIconBase
{
	private static ResourceLocation image;
	
	public CategoryIcon(int u, int v, int width, int height, int textureWidth, int textureHeight, String imagePath)
	{
		super(u, v, width, height, textureWidth, textureHeight);
		image = new ResourceLocation(Reference.MOD_ID, imagePath + ".png");
	}
	
	@Override
	protected void bindTexture(Minecraft minecraft)
	{
		minecraft.getTextureManager().bindTexture(image);
	}
	
}