package com.phylogeny.extrabitmanipulation.client.gui.button;

public class GuiButtonGradient extends GuiButtonCustom
{
	
	public GuiButtonGradient(int buttonId, int x, int y, int widthIn, int heightIn, String text, String hoverText)
	{
		super(buttonId, x, y, widthIn, heightIn, text, hoverText);
	}
	
	@Override
	protected void drawCustomRect()
	{
		drawGradientRect(x, y, x + width, y + height, enabled ? -2631721 : -11513776, enabled ? -8289919 : -11513776);
	}
	
}