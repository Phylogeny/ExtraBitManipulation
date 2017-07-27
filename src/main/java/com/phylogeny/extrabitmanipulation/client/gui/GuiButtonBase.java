package com.phylogeny.extrabitmanipulation.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.SoundEvent;

import com.phylogeny.extrabitmanipulation.init.SoundsExtraBitManipulation;

public class GuiButtonBase extends GuiButton
{
	public boolean selected;
	private boolean silent, helpMode;
	private String hoverText, hoverTextSelected, hoverHelpText;
	private SoundEvent soundSelect, soundDeselect;
	
	public GuiButtonBase(int buttonId, int x, int y, int width, int height, String text, String hoverText)
	{
		this(buttonId, x, y, width, height, text, hoverText, null, null);
	}
	
	public GuiButtonBase(int buttonId, int x, int y, int widthIn, int heightIn,
			String text, String hoverText, SoundEvent soundSelect, SoundEvent soundDeselect)
	{
		super(buttonId, x, y, widthIn, heightIn, text);
		this.hoverText = hoverTextSelected = hoverText;
		this.soundSelect = soundSelect;
		this.soundDeselect = soundDeselect;
	}
	
	public void setSilent(boolean silent)
	{
		this.silent = silent;
	}
	
	@Override
	public void playPressSound(SoundHandler soundHandler)
	{
		if (silent)
			return;
		
		if (soundSelect != null)
		{
			SoundsExtraBitManipulation.playSound(selected ? soundDeselect : soundSelect);
			return;
		}
		super.playPressSound(soundHandler);
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
	{
		hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
		mouseDragged(mc, mouseX, mouseY);
	}
	
	public String getHoverText()
	{
		return helpMode ? hoverHelpText : (selected ? hoverTextSelected : hoverText);
	}
	
	public void setHoverText(String text)
	{
		hoverText = text;
	}
	
	public void setHoverTextSelected(String text)
	{
		hoverTextSelected = text;
	}
	
	public void setHoverHelpText(String text)
	{
		hoverHelpText = text;
	}
	
	public void setHelpMode(boolean helpMode)
	{
		this.helpMode = helpMode;
	}
	
	public GuiButtonBase setSoundSelect(SoundEvent sound)
	{
		soundSelect = sound;
		return this;
	}
	
	public GuiButtonBase setSoundDeselect(SoundEvent sound)
	{
		soundDeselect = sound;
		return this;
	}
	
}