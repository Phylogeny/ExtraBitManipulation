package com.phylogeny.extrabitmanipulation.client.gui;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.client.gui.GuiBitToolSettingsMenu.GuiSliderSetting;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.reference.Configs;

public class SliderSetting
{
	private GuiSliderSetting slider;
	private GuiButton plus, minus;
	
	public SliderSetting() {}
	
	public int getMaxValue()
	{
		return 0;
	}
	
	public GuiSliderSetting getSlider()
	{
		return slider;
	}
	
	public void createElements(GuiSliderSetting slider)
	{
		this.slider = slider;
		plus = createIncrementButton(slider, -1, "+");
		minus = createIncrementButton(slider, 1, "-");
	}
	
	private GuiButton createIncrementButton(GuiSliderSetting slider, int offsetX, String text)
	{
		return new GuiButtonExt(slider.id * 100, slider.xPosition - (offsetX > 0 ? slider.height : -slider.width)
				+ offsetX, slider.yPosition, slider.height, slider.height, text);
	}
	
	public void addAllElements(List<GuiButton> buttonList)
	{
		buttonList.add(slider);
		buttonList.add(plus);
		buttonList.add(minus);
	}
	
	public void increment(GuiButton button)
	{
		boolean add = button == plus;
		if (!add && button != minus)
			return;
		
		int value = getValue();
		if (add ? value < getMaxValue() : value > 0)
		{
			slider.setValue(value + (add ? 1 : -1));
			slider.updateSlider();
		}
	}
	
	protected int getValue()
	{
		return 0;
	}
	
	@SuppressWarnings("unused")
	protected void setValue(EntityPlayer player, int value) {}
	
	public void setValueIfDiffrent()
	{
		int value = slider.getValueInt();
		if (value != getValue())
			setValue(ClientHelper.getPlayer(), value);
	}
	
	protected NBTTagCompound getHeldStackNBT()
	{
		return ItemStackHelper.getNBTOrNew(ClientHelper.getHeldItemMainhand());
	}
	
	public static class SemiDiameter extends SliderSetting
	{
		
		@Override
		public int getMaxValue()
		{
			return Configs.maxSemiDiameter;
		}
		
		@Override
		protected int getValue()
		{
			return BitToolSettingsHelper.getSemiDiameter(getHeldStackNBT());
		}
		
		@Override
		protected void setValue(EntityPlayer player, int value)
		{
			BitToolSettingsHelper.setSemiDiameter(player, player.getHeldItemMainhand(), value, Configs.sculptSemiDiameter);
		}
		
	}
	
	public static class WallThickness extends SliderSetting
	{
		
		@Override
		public int getMaxValue()
		{
			return Configs.maxWallThickness;
		}
		
		@Override
		protected int getValue()
		{
			return BitToolSettingsHelper.getWallThickness(getHeldStackNBT());
		}
		
		@Override
		protected void setValue(EntityPlayer player, int value)
		{
			BitToolSettingsHelper.setWallThickness(player, player.getHeldItemMainhand(), value, Configs.sculptWallThickness);
		}
		
	}
	
}