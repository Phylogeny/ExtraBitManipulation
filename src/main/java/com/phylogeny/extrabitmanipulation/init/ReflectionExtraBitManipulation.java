package com.phylogeny.extrabitmanipulation.init;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelPlayer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ReflectionExtraBitManipulation
{
	private static Field oldMouseX, oldMouseY, smallArms, buttonList, guiLeft, guiTop;
	
	public static void initReflectionFieldsClient()
	{
		oldMouseX = findField(GuiInventory.class, "oldMouseX", "field_147048_u");
		oldMouseY = findField(GuiInventory.class, "oldMouseY", "field_147047_v");
		smallArms = findField(ModelPlayer.class, "smallArms", "field_178735_y");
		buttonList = findField(GuiScreen.class, "buttonList", "field_146292_n");
		guiLeft = findField(GuiContainer.class, "guiLeft", "field_147003_i");
		guiTop = findField(GuiContainer.class, "guiTop", "field_147009_r");
	}
	
	private static Field findField(Class<?> clazz, String... fieldNames)
	{
		Field field = ReflectionHelper.findField(clazz, fieldNames);
		field.setAccessible(true);
		return field;
	}
	
	private static int getInt(Field field, Object instance)
	{
		try
		{
			return field.getInt(instance);
		}
		catch (IllegalArgumentException e) {}
		catch (IllegalAccessException e) {}
		return 0;
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
	
	public static int getGuiLeft(GuiContainer gui)
	{
		return getInt(guiLeft, gui);
	}
	
	public static int getGuiTop(GuiContainer gui)
	{
		return getInt(guiTop, gui);
	}
	
}