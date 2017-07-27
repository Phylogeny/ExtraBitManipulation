package com.phylogeny.extrabitmanipulation.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.client.GuiHelper;

public class GuiButtonTextured extends GuiButtonBase
{
	private ResourceLocation selectedTexture, deselectedTexture;
	
	public GuiButtonTextured(int buttonId, int x, int y, int widthIn, int heightIn, String hoverText, ResourceLocation selectedTexture,
			ResourceLocation deselectedTexture, SoundEvent boxCheck, SoundEvent boxUncheck)
	{
		super(buttonId, x, y, widthIn, heightIn, "", hoverText, boxCheck, boxUncheck);
		this.selectedTexture = selectedTexture;
		this.deselectedTexture = deselectedTexture;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
	{
		super.drawButton(mc, mouseX, mouseY, partialTicks);
		if (!visible)
			return;
		
		GlStateManager.pushMatrix();
		GlStateManager.color(1, 1, 1, 1);
		ClientHelper.bindTexture(selected ? selectedTexture : deselectedTexture);
		int offset = 0;
		if (hovered)
			offset = 1;
		
		double y = this.y - 0.5;
		GuiHelper.drawTexturedRect(x - offset, y - offset, x + width + offset, y + height + offset);
		GlStateManager.popMatrix();
	}
	
}