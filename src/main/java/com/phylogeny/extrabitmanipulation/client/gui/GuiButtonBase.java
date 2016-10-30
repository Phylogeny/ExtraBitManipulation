package com.phylogeny.extrabitmanipulation.client.gui;

import com.phylogeny.extrabitmanipulation.init.SoundsExtraBitManipulation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiButtonBase extends GuiButton
{
	public boolean selected;
	private String hoverText;
	private ResourceLocation soundSelect, soundDeselect;
	
	public GuiButtonBase(int buttonId, int x, int y, int widthIn, int heightIn, String text, String hoverText)
	{
		this(buttonId, x, y, widthIn, heightIn, text, hoverText, null, null);
	}
	
	public GuiButtonBase(int buttonId, int x, int y, int widthIn, int heightIn, String text,
			String hoverText, ResourceLocation soundSelect, ResourceLocation soundDeselect)
	{
		super(buttonId, x, y, widthIn, heightIn, text);
		this.hoverText = hoverText;
		this.soundSelect = soundSelect;
		this.soundDeselect = soundDeselect;
	}
	
	@Override
	public void playPressSound(SoundHandler soundHandlerIn)
	{
		if (soundSelect != null && !selected)
		{
			SoundsExtraBitManipulation.playSound(soundSelect);
			return;
		}
		if (soundDeselect != null && selected)
		{
			SoundsExtraBitManipulation.playSound(soundDeselect);
			return;
		}
		super.playPressSound(soundHandlerIn);
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
	
	public GuiButtonBase setSoundSelect(ResourceLocation sound)
	{
		soundSelect = sound;
		return this;
	}
	
	public GuiButtonBase setSoundDeselect(ResourceLocation sound)
	{
		soundDeselect = sound;
		return this;
	}
	
}