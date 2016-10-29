package com.phylogeny.extrabitmanipulation.client.gui;

public class GuiButtonSelect extends GuiButtonCustom
{
	private int colorFirst, colorSecond;
	
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
		drawRect(xPosition, yPosition, xPosition + width, yPosition + height, selected ? colorFirst : colorSecond);
	}
	
}