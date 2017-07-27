package com.phylogeny.extrabitmanipulation.api.jei.icon;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import com.phylogeny.extrabitmanipulation.api.jei.CycleTimer;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class CategoryIconList extends CategoryIconResourceBase
{
	private static List<ResourceLocation> images = new ArrayList<ResourceLocation>();
	private final int itemCycleOffset = (int) (Math.random() * 1000);
	private CycleTimer cycleTimer;
	
	public CategoryIconList(int u, int v, int width, int height, int textureWidth, int textureHeight, String imagePath, String... imageNames)
	{
		super(u, v, width, height, textureWidth, textureHeight);
		for (String path : imageNames)
			images.add(new ResourceLocation(Reference.MOD_ID, imagePath + path + ".png"));
		
		cycleTimer = new CycleTimer(itemCycleOffset);
	}
	
	@Override
	protected void bindTexture(Minecraft minecraft)
	{
		cycleTimer.onDraw();
		ClientHelper.bindTexture(cycleTimer.getCycledItem(images));
	}
	
}