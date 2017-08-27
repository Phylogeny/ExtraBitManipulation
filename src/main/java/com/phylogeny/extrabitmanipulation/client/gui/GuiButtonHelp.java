package com.phylogeny.extrabitmanipulation.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import org.lwjgl.opengl.GL11;

public class GuiButtonHelp extends GuiButtonCustom
{
	List<GuiButton> buttonList;
	
	public GuiButtonHelp(int buttonId, List<GuiButton> buttonList, int x, int y, String hoverText, String hoverTextSelected)
	{
		super(buttonId, x, y, 12, 12, "?", hoverText);
		this.buttonList = buttonList;
		setHoverTextSelected(hoverTextSelected);
		setTextOffsetX(0.5F);
		setTextOffsetY(0.5F);
	}
	
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
	{
		boolean pressed = super.mousePressed(mc, mouseX, mouseY);
		if (pressed)
		{
			boolean helpMode = !selected;
			selected = helpMode;
			for (GuiButton button : buttonList)
			{
				if (button != this && button instanceof GuiButtonBase)
					((GuiButtonBase) button).setHelpMode(helpMode);
			}
		}
		return pressed;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		int x = this.xPosition + 6;
		int y = this.yPosition + 6;
		double radius = 6;
		int red, green, blue;
		if (selected)
		{
			red = blue = 0;
			green = 200;
		}
		else
		{
			red = green = blue = 120;
		}
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(x, y, 0).color(red, green, blue, 255).endVertex();
		double s = 30;
		for(int k = 0; k <= s; k++) 
		{
			double angle = (Math.PI * 2 * k / s) + Math.toRadians(180);
			buffer.pos(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, 0).color(red, green, blue, 255).endVertex();
		}
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		super.drawButton(mc, mouseX, mouseY);
	}
	
}