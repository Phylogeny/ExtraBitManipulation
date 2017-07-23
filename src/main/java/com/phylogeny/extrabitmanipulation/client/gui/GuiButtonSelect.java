package com.phylogeny.extrabitmanipulation.client.gui;

public class GuiButtonSelect extends GuiButtonCustom
{
	protected int colorFirst, colorSecond;
	
	public GuiButtonSelect(int buttonId, int x, int y, int widthIn, int heightIn, String text,
			String hoverText, int colorFirst, int colorSecond)
	{
		super(buttonId, x, y, widthIn, heightIn, text, hoverText);
		this.colorFirst = colorFirst;
		this.colorSecond = colorSecond;
	}
	
	@Override
	protected void drawCustomRect()
	{
		drawRect(x, y, x + width, y + height, selected ? colorFirst : colorSecond);
	}
	
}