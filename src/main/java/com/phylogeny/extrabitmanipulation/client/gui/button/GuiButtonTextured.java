package com.phylogeny.extrabitmanipulation.client.gui.button;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.client.GuiHelper;

public class GuiButtonTextured extends GuiButtonBase
{
	private ResourceLocation selectedTexture, deselectedTexture;
	
	public GuiButtonTextured(int buttonId, int x, int y, int width, int height, String hoverText, ResourceLocation selectedTexture,
			ResourceLocation deselectedTexture, @Nullable SoundEvent boxCheck, @Nullable SoundEvent boxUncheck)
	{
		super(buttonId, x, y, width, height, "", hoverText, boxCheck, boxUncheck);
		this.selectedTexture = selectedTexture;
		this.deselectedTexture = deselectedTexture;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		super.drawButton(mc, mouseX, mouseY);
		if (!visible)
			return;
		
		GlStateManager.pushMatrix();
		GlStateManager.color(1, 1, 1, 1);
		ClientHelper.bindTexture(selected ? selectedTexture : deselectedTexture);
		int offset = 0;
		if (hovered)
			offset = 1;
		
		double yPosition = this.yPosition - 0.5;
		GuiHelper.drawTexturedRect(xPosition - offset, yPosition - offset, xPosition + width + offset, yPosition + height + offset);
		GlStateManager.popMatrix();
	}
	
}