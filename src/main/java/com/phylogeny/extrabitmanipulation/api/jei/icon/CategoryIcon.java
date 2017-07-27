package com.phylogeny.extrabitmanipulation.api.jei.icon;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class CategoryIcon extends CategoryIconResourceBase
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
		ClientHelper.bindTexture(image);
	}
	
}