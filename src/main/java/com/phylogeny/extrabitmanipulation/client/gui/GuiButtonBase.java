package com.phylogeny.extrabitmanipulation.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonBase extends GuiButton
{
	public boolean selected;
	private String hoverText;
	
	public GuiButtonBase(int buttonId, int x, int y, int widthIn, int heightIn, String text, String hoverText)
	{
		super(buttonId, x, y, widthIn, heightIn, text);
		this.hoverText = hoverText;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
		mouseDragged(mc, mouseX, mouseY);
	}
	
	public String getHoverText()
	{
		return hoverText;
	}
	
}