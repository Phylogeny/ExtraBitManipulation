package com.phylogeny.extrabitmanipulation.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GuiButtonTextured extends GuiButtonBase
{
	private ResourceLocation selectedTexture, deselectedTexture;
	private String selectedHoverText;
	
	public GuiButtonTextured(int buttonId, int x, int y, int widthIn, int heightIn, String hoverText,
			ResourceLocation selectedTexture, ResourceLocation deselectedTexture)
	{
		this(buttonId, x, y, widthIn, heightIn, hoverText, hoverText, selectedTexture, deselectedTexture);
	}
	
	public GuiButtonTextured(int buttonId, int x, int y, int widthIn, int heightIn, String hoverText, String selectedHoverText,
			ResourceLocation selectedTexture, ResourceLocation deselectedTexture)
	{
		super(buttonId, x, y, widthIn, heightIn, "", hoverText);
		this.selectedHoverText = selectedHoverText;
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
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.blendFunc(770, 771);
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(selected ? selectedTexture : deselectedTexture);
		Tessellator t = Tessellator.getInstance();
		WorldRenderer wr = t.getWorldRenderer();
		int offset = 0;
		if (hovered)
			offset = 1;
		
		wr.begin(7, DefaultVertexFormats.POSITION_TEX);
		double yPosition = this.yPosition - 0.5;
		wr.pos(xPosition - offset, yPosition - offset, 0).tex(0, 0).endVertex();
		wr.pos(xPosition - offset, yPosition + height + offset, 0).tex(0, 1).endVertex();
		wr.pos(xPosition + width + offset, yPosition + height + offset, 0).tex(1, 1).endVertex();
		wr.pos(xPosition + width + offset, yPosition - offset, 0).tex(1, 0).endVertex();
		t.draw();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
	
	public String getSelectedHoverText()
	{
		return selectedHoverText;
	}
	
}