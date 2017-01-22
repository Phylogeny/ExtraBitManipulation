package com.phylogeny.extrabitmanipulation.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.client.gui.GuiBitToolSettingsMenu.GuiButtonSetting;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingTool;
import com.phylogeny.extrabitmanipulation.packet.PacketSetWrechMode;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

public abstract class ButtonsSetting
{
	protected List<GuiButtonSetting> buttons;
	
	public ButtonsSetting()
	{
		buttons = new ArrayList<GuiButtonSetting>();
	}
	
	public List<GuiButtonSetting> getButtons()
	{
		return buttons;
	}
	
	public void addButton(GuiButtonSetting button)
	{
		button.selected = buttons.size() == getValue();
		buttons.add(button);
	}
	
	public void initButtons()
	{
		for (GuiButtonSetting button : buttons)
		{
			button.setButtonList(buttons);
		}
	}
	
	protected int getValue()
	{
		return 0;
	}
	
	protected abstract void setValue(EntityPlayer player, int value);
	
	public void setValueIfDiffrent()
	{
		int value = buttons.indexOf(getTargetButton());
		if (value != getValue())
			setValue(ClientHelper.getPlayer(), value);
	}
	
	protected GuiButtonSetting getTargetButton()
	{
		GuiButtonSetting buttonTarget = null;
		for (GuiButtonSetting button : buttons)
		{
			if (button.isMouseOver())
				buttonTarget = button;
		}
		if (buttonTarget == null)
		{
			for (GuiButtonSetting button : buttons)
			{
				if (button.selected)
					buttonTarget = button;
			}
		}
		return buttonTarget;
	}
	
	protected NBTTagCompound getHeldStackNBT()
	{
		return ItemStackHelper.getNBTOrNew(ClientHelper.getHeldItemMainhand());
	}
	
	private static ItemSculptingTool getSculptingTool()
	{
		ItemStack stack = ClientHelper.getHeldItemMainhand();
		return stack == null ? null : (ItemSculptingTool) stack.getItem();
	}
	
	public static class WrenchMode extends ButtonsSetting
	{
		
		@Override
		protected int getValue()
		{
			return getHeldStackNBT().getInteger(NBTKeys.WRENCH_MODE);
		}
		
		@Override
		protected void setValue(EntityPlayer player, int value)
		{
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetWrechMode(value));
		}
		
	}
	
	public static class ModelAreaMode extends ButtonsSetting
	{
		
		@Override
		protected int getValue()
		{
			return BitToolSettingsHelper.getModelAreaMode(getHeldStackNBT());
		}
		
		@Override
		protected void setValue(EntityPlayer player, int value)
		{
			BitToolSettingsHelper.setModelAreaMode(player, player.getHeldItemMainhand(), value, Configs.modelAreaMode);
		}
		
	}
	
	public static class ModelSnapMode extends ButtonsSetting
	{
		
		@Override
		protected int getValue()
		{
			return BitToolSettingsHelper.getModelSnapMode(getHeldStackNBT());
		}
		
		@Override
		protected void setValue(EntityPlayer player, int value)
		{
			BitToolSettingsHelper.setModelSnapMode(player, player.getHeldItemMainhand(), value, Configs.modelSnapMode);
		}
		
	}
	
	public static class ModelGuiOpen extends ButtonsSetting
	{
		
		@Override
		protected int getValue()
		{
			return BitToolSettingsHelper.getModelGuiOpen(getHeldStackNBT()) ? 0 : 1;
		}
		
		@Override
		protected void setValue(EntityPlayer player, int value)
		{
			BitToolSettingsHelper.setModelGuiOpen(player, player.getHeldItemMainhand(), value == 0, Configs.modelGuiOpen);
		}
		
	}
	
	public static class SculptMode extends ButtonsSetting
	{
		
		@Override
		protected int getValue()
		{
			return BitToolSettingsHelper.getSculptMode(getHeldStackNBT());
		}
		
		@Override
		protected void setValue(EntityPlayer player, int value)
		{
			BitToolSettingsHelper.setSculptMode(player, player.getHeldItemMainhand(), value, Configs.sculptMode);
		}
		
	}
	
	public static class Direction extends ButtonsSetting // TODO decompose to direction and rotation when triangular shapes are implemented
	{
		
		@Override
		protected int getValue()
		{
			return BitToolSettingsHelper.getDirection(getHeldStackNBT()) % 6;
		}
		
		@Override
		protected void setValue(EntityPlayer player, int value)
		{
			BitToolSettingsHelper.setDirection(player, player.getHeldItemMainhand(), value % 6, Configs.sculptDirection);
		}
		
	}
	
	public static class ShapeType extends ButtonsSetting
	{
		
		@Override
		public void setValueIfDiffrent()//TODO remove method when triangular shapes are implemented
		{
			ItemSculptingTool tool = getSculptingTool();
			if (tool == null)
				return;
			
			int value = buttons.indexOf(getTargetButton());
			if (!tool.isCurved())
				value = value * 3 + 3;
			
			if (value != getValue())
				setValue(ClientHelper.getPlayer(), value);
		}
		
		@Override
		protected int getValue()
		{
			ItemSculptingTool tool = getSculptingTool();
//			return tool == null ? 0 : BitToolSettingsHelper.getShapeType(getHeldStackNBT(), tool.isCurved()); TODO
			if (tool == null)
				return 0;
			
			int shapeType = BitToolSettingsHelper.getShapeType(getHeldStackNBT(), tool.isCurved());
			return tool.isCurved() ? shapeType : shapeType / 3 - 1;

		}
		
		@Override
		protected void setValue(EntityPlayer player, int value)
		{
			ItemSculptingTool tool = getSculptingTool();
			if (tool != null)
				BitToolSettingsHelper.setShapeType(player, player.getHeldItemMainhand(), tool.isCurved(), value,
						tool.isCurved() ? Configs.sculptShapeTypeCurved : Configs.sculptShapeTypeFlat);
		}
		
	}
	
	public static class BitGridTargeted extends ButtonsSetting
	{
		
		@Override
		protected int getValue()
		{
			return BitToolSettingsHelper.isBitGridTargeted(getHeldStackNBT()) ? 1 : 0;
		}
		
		@Override
		protected void setValue(EntityPlayer player, int value)
		{
			BitToolSettingsHelper.setBitGridTargeted(player, player.getHeldItemMainhand(), value == 1, Configs.sculptTargetBitGridVertexes);
		}
		
	}
	
	public static class HollowShape extends ButtonsSetting
	{
		
		@Override
		protected int getValue()
		{
			ItemSculptingTool tool = getSculptingTool();
			return tool == null ? 0 : BitToolSettingsHelper.isHollowShape(getHeldStackNBT(), tool.removeBits()) ? 0 : 1;
		}
		
		@Override
		protected void setValue(EntityPlayer player, int value)
		{
			ItemSculptingTool tool = getSculptingTool();
			if (tool != null)
				BitToolSettingsHelper.setHollowShape(player, player.getHeldItemMainhand(), tool.removeBits(),
						value == 0, tool.removeBits() ? Configs.sculptHollowShapeWire : Configs.sculptHollowShapeSpade);
		}
		
	}
	
	public static class OpenEnds extends ButtonsSetting
	{
		
		@Override
		protected int getValue()
		{
			return BitToolSettingsHelper.areEndsOpen(getHeldStackNBT()) ? 0 : 1;
		}
		
		@Override
		protected void setValue(EntityPlayer player, int value)
		{
			BitToolSettingsHelper.setEndsOpen(player, player.getHeldItemMainhand(), value == 0, Configs.sculptOpenEnds);
		}
		
	}
	
	public static class OffsetShape extends ButtonsSetting
	{
		
		@Override
		protected int getValue()
		{
			return BitToolSettingsHelper.isShapeOffset(getHeldStackNBT()) ? 0 : 1;
		}
		
		@Override
		protected void setValue(EntityPlayer player, int value)
		{
			BitToolSettingsHelper.setShapeOffset(player, player.getHeldItemMainhand(), value == 0, Configs.sculptOffsetShape);
		}
		
	}
	
}