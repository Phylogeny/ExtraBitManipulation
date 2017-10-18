package com.phylogeny.extrabitmanipulation.init;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.command.CommandReplaceItem;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ReflectionExtraBitManipulation
{
	private static Field oldMouseX, oldMouseY, smallArms, buttonList, shortcuts;
	
	public static void initReflectionFieldsClient()
	{
		oldMouseX = ReflectionHelper.findField(GuiInventory.class, "oldMouseX", "field_147048_u");
		oldMouseY = ReflectionHelper.findField(GuiInventory.class, "oldMouseY", "field_147047_v");
		smallArms = ReflectionHelper.findField(ModelPlayer.class, "smallArms", "field_178735_y");
		buttonList = ReflectionHelper.findField(GuiScreen.class, "buttonList", "field_146292_n");
	}
	
	public static void initReflectionFieldsCommon()
	{
		shortcuts = ReflectionHelper.findField(CommandReplaceItem.class, "SHORTCUTS", "field_175785_a");
	}
	
	private static void setFloat(Field field, Object instance, float value)
	{
		try
		{
			field.setFloat(instance, value);
		}
		catch (IllegalArgumentException e) {}
		catch (IllegalAccessException e) {}
	}
	
	public static void setCursorPosition(GuiInventory gui, float mouseX, float mouseY)
	{
		setFloat(oldMouseX, gui, mouseX);
		setFloat(oldMouseY, gui, mouseY);
	}
	
	public static boolean areArmsSmall(ModelPlayer model)
	{
		try
		{
			return smallArms.getBoolean(model);
		}
		catch (IllegalArgumentException e) {}
		catch (IllegalAccessException e) {}
		return false;
	}
	
	public static List<GuiButton> getButtonList(GuiScreen gui)
	{
		try
		{
			return (List<GuiButton>) buttonList.get(gui);
		}
		catch (IllegalArgumentException e) {}
		catch (IllegalAccessException e) {}
		return Collections.<GuiButton>emptyList();
	}
	
	public static void addShortcutsToCommandReplaceItem(CommandReplaceItem command, Map<String, Integer> shortcutsNew)
	{
		try
		{
			Map<String, Integer> SHORTCUTS = (Map<String, Integer>) shortcuts.get(command);
			SHORTCUTS.putAll(shortcutsNew);
			shortcuts.set(command, SHORTCUTS);
		}
		catch (IllegalArgumentException e) {}
		catch (IllegalAccessException e) {}
	}
	
}