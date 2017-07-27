package com.phylogeny.extrabitmanipulation.api.jei.icon;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import com.phylogeny.extrabitmanipulation.api.jei.CycleTimer;

public class CategoryIconStackList extends CategoryIconBase
{
	private static List<ItemStack> stacks = new ArrayList<ItemStack>();
	private final int itemCycleOffset = (int) (Math.random() * 1000);
	private CycleTimer cycleTimer;
	
	public CategoryIconStackList(int width, int height, List<ItemStack> stacks)
	{
		super(width, height);
		this.stacks = stacks;
		cycleTimer = new CycleTimer(itemCycleOffset);
	}
	
	@Override
	public void draw(Minecraft minecraft, int xOffset, int yOffset, int maskTop, int maskBottom, int maskLeft, int maskRight)
	{
		cycleTimer.onDraw();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableDepth();
		GlStateManager.enableRescaleNormal();
		RenderHelper.enableGUIStandardItemLighting();
		Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(cycleTimer.getCycledItem(stacks), xOffset + maskLeft, yOffset + maskTop);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
	}
	
}