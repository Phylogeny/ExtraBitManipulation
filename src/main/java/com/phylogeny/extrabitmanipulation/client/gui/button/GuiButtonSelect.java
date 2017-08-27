package com.phylogeny.extrabitmanipulation.client.gui.button;

public class GuiButtonSelect extends GuiButtonCustom
{
	protected int colorFirst, colorSecond;
	
	public GuiButtonSelect(int buttonId, int x, int y, int width, int height, String text, String hoverText, int colorFirst, int colorSecond)
	{
		super(buttonId, x, y, width, height, text, hoverText);
		this.colorFirst = colorFirst;
		this.colorSecond = colorSecond;
	}
	
	@Override
	protected void drawCustomRect()
	{
		drawRect(x, y, x + width, y + height, selected ? colorFirst : colorSecond);
	}
	
}