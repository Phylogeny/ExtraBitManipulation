package com.phylogeny.extrabitmanipulation.client.gui.armor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Predicate;
import com.phylogeny.extrabitmanipulation.armor.GlOperation;
import com.phylogeny.extrabitmanipulation.armor.GlOperation.GlOperationType;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.client.GuiHelper;
import com.phylogeny.extrabitmanipulation.client.gui.GuiButtonBase;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.Reference;
import com.phylogeny.extrabitmanipulation.reference.Utility;

public class GuiListEntryGlOperation<L> extends GuiListEntryChiseledArmor<GlOperation>
{
	private static final ResourceLocation TEXTURE_ROTATION = new ResourceLocation(Reference.MOD_ID, "textures/guis/rotation.png");
	private static final ResourceLocation TEXTURE_TRANSLATION = new ResourceLocation(Reference.MOD_ID, "textures/guis/translation.png");
	private static final ResourceLocation TEXTURE_SCALE = new ResourceLocation(Reference.MOD_ID, "textures/guis/scale.png");
	private static int scaleFactor;
	private List<GuiTextField> dataFields = new ArrayList<GuiTextField>();
	private GuiButtonExt buttonPlus, buttonMinus;
	private ResourceLocation textureIcon;
	private boolean iconHovered;
	private int index;
	private final Predicate<String> numberFilter = new Predicate<String>()
	{
		@SuppressWarnings("null")
		@Override
		public boolean apply(@Nullable String text)
		{
			if (StringUtils.isNullOrEmpty(text) || text.equals("-"))
				return true;
			
			try
			{
				Float.parseFloat(text);
				return true;
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		}
		
		@Override
		public boolean equals(@Nullable Object object)
		{
			return super.equals(object);
		}
	};
	
	public GuiListEntryGlOperation(GuiListChiseledArmor<GlOperation> listChiseledArmor, GlOperation entryObject, int index)
	{
		super(listChiseledArmor, entryObject);
		int len = entryObject.getType() == GlOperationType.ROTATION ? 4 : 3;
		for (int i = 0; i < len; i++)
		{
			float data = (i == 0 ? entryObject.getX() : (i == 1 ? entryObject.getY() : (i == 2 ? entryObject.getZ() : entryObject.getAngle())));
			if (entryObject.getType() == GlOperationType.TRANSLATION && i < 3 && listChiseledArmor.guiChiseledArmor.scalePixel())
				data /= Utility.PIXEL_F;
			
			dataFields.add(createDataField(listChiseledArmor, 20 + i * 45, i == 3 ? 21 : 35, getDataFieldString(data)));
		}
		buttonPlus = createPlusMinusButton(listChiseledArmor, 184, "+");
		buttonMinus = createPlusMinusButton(listChiseledArmor, 198, "-");
		switch (entryObject.getType())
		{
			case ROTATION:		textureIcon = TEXTURE_ROTATION;
								break;
			case TRANSLATION:	textureIcon = TEXTURE_TRANSLATION;
								break;
			case SCALE:			textureIcon = TEXTURE_SCALE;
		}
		this.index = index;
		scaleFactor = GuiHelper.getScaleFactor();
	}
	
	public boolean fieldIsFocused()
	{
		for (GuiTextField field : dataFields)
		{
			if (field.isFocused())
				return true;
		}
		return false;
	}
	
	private String getDataFieldString(float data)
	{
		return "" + (data == (int) data ? Integer.toString((int) data) : Float.toString(data));
	}
	
	private GuiButtonExt createPlusMinusButton( GuiListChiseledArmor<GlOperation> listChiseledArmor, int offsetX, String text)
	{
		return new GuiButtonExt(0, listChiseledArmor.left + offsetX, 0, 12, 12, text);
	}
	
	private GuiTextField createDataField(GuiListChiseledArmor<GlOperation> listChiseledArmor, int offsetX, int width, String text)
	{
		GuiTextField field = new GuiTextField(6, mc.fontRendererObj, listChiseledArmor.left + offsetX, 0, width, 9);
		field.setValidator(numberFilter);
		field.setEnableBackgroundDrawing(false);
		field.setTextColor(-1);
		field.setText(text);
		field.setCursorPositionZero();
		return field;
	}
	
	public void formatDataFields(int mouseX, int mouseY)
	{
		for (int i = 0; i < dataFields.size(); i++)
		{
			GuiTextField field = dataFields.get(i);
			changeData(field, i, field.getText(), true);
			if (!buttonPlus.isMouseOver() && !buttonMinus.isMouseOver() && (mouseX < field.xPosition || mouseX > field.xPosition + field.width
					|| mouseY < field.yPosition || mouseY > field.yPosition + field.height))
				field.setFocused(false);
		}
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode)
	{
		boolean enterPressed = keyCode == Keyboard.KEY_RETURN;
		for (int i = 0; i < dataFields.size(); i++)
		{
			GuiTextField field = dataFields.get(i);
			String textInitial = field.getText();
			if (enterPressed || field.textboxKeyTyped(typedChar, keyCode))
			{
				if (field.isFocused() && keyCode != Keyboard.KEY_LEFT && keyCode != Keyboard.KEY_RIGHT
						&& !GuiScreen.isKeyComboCtrlA(keyCode) && !GuiScreen.isKeyComboCtrlC(keyCode))
					changeData(field, i, textInitial, enterPressed);
				
				if (enterPressed)
					field.setFocused(false);
			}
		}
	}
	
	private void changeData(GuiTextField field, int index, String textInitial, boolean changeText)
	{
		float data = 0;
		try
		{
			data = Float.parseFloat(field.getText());
		}
		catch (NumberFormatException e) {}
		int pos = field.getCursorPosition();
		if (changeText)
		{
			if (index == 3)
				data %= 360;
			
			field.setText(getDataFieldString(data));
		}
		field.setCursorPosition(pos);
		data %= 360;
		if (entryObject.getType() == GlOperationType.TRANSLATION && index < 3 && listChiseledArmor.guiChiseledArmor.scalePixel())
			data *= Utility.PIXEL_F;
		
		switch (index)
		{
			case 0:	entryObject.setX(data);
					break;
			case 1:	entryObject.setY(data);
					break;
			case 2:	entryObject.setZ(data);
					break;
			case 3:	entryObject.setAngle(data);
		}
		if (!textInitial.equals(field.getText()))
			listChiseledArmor.guiChiseledArmor.setGlOperationListData(-1, false);
	}
	
	@Override
	public void updateScreen(boolean isSelected)
	{
		int y = -MathHelper.clamp_int(listChiseledArmor.getAmountScrolled(), 0, listChiseledArmor.getMaxScroll())
				+ listChiseledArmor.slotHeight * index + listChiseledArmor.headerPadding + listChiseledArmor.top + 5;
		for (GuiTextField field : dataFields)
		{
			if (isSelected)
				field.updateCursorCounter();
			else
				field.setFocused(false);
			
			field.yPosition = y;
		}
		buttonPlus.yPosition = buttonMinus.yPosition = y - 2;
	}
	
	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
	{
		iconHovered = mouseX > x + 2 && mouseX < x + 16 && mouseY > y + 1 && mouseY < y + 14;
		ClientHelper.bindTexture(textureIcon);
		GlStateManager.color(1, 1, 1);
		GuiButtonBase.drawRect(x + 2, y + 2, x + 15, y + 14, -38400);
		GlStateManager.color(1, 1, 1);
		GlStateManager.pushMatrix();
		double iconX = x + 2;
		double iconY = y + 2;
		double iconWidth = 13;
		double iconHieght = 12;
		if (scaleFactor == 3)
		{
			iconWidth *= 2 / 3.0F;
			iconHieght *= 2 / 3.0F;
			iconX += 2;
			iconY += 2;
		}
		GuiHelper.drawTexturedRect(iconX, iconY, iconX + iconWidth, iconY + iconHieght);
		GlStateManager.popMatrix();
		for (GuiTextField field : dataFields)
		{
			GuiUtils.drawContinuousTexturedBox(GuiChiseledArmor.TEXTURE_GUI, field.xPosition - 3,
					field.yPosition - 2, 78, 115, field.width + 9, field.height + 3, 13, 12, 2, 100);
			field.drawTextBox();
		}
		buttonPlus.drawButton(mc, mouseX, mouseY);
		buttonMinus.drawButton(mc, mouseX, mouseY);
	}
	
	@Override
	public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY)
	{
		float inc = 0.0F;
		boolean shiftDown = GuiScreen.isShiftKeyDown();
		if (buttonPlus.isMouseOver() || buttonMinus.isMouseOver())
		{
			inc = buttonPlus.isMouseOver() ? 1 : -1;
			if (entryObject.getType() == GlOperationType.TRANSLATION)
			{
				boolean scalePixel = listChiseledArmor.guiChiseledArmor.scalePixel();
				if (scalePixel && shiftDown)
					inc *= 16;
				
				if (!scalePixel && !shiftDown)
					inc *= Utility.PIXEL_F;
			}
			inc = alterIncrementAmount(inc);
			buttonPlus.playPressSound(mc.getSoundHandler());
		}
		for (int i = 0; i < dataFields.size(); i++)
		{
			GuiTextField field = dataFields.get(i);
			if (inc == 0)
			{
				field.mouseClicked(mouseX, mouseY, mouseEvent);
				if (field.isFocused())
				{
					float data = 0;
					try
					{
						data = Float.parseFloat(field.getText());
					}
					catch (NumberFormatException e) {}
					if (data == 0)
						field.setText("");
					
					break;
				}
			}
			else if (field.isFocused())
			{
				if (i == 3)
				{
					inc = buttonPlus.isMouseOver() ? 1 : -1;
					if (shiftDown)
						inc *= 90;
					
					inc = alterIncrementAmount(inc);
				}
				String textInitial = field.getText();
				try
				{
					float data = Float.parseFloat(field.getText()) + inc;
					if (i == 3)
						data %= 360;
					
					field.setText(getDataFieldString(data));
				}
				catch (NumberFormatException e) {}
				changeData(field, i, textInitial, true);
				break;
			}
		}
		return super.mousePressed(slotIndex, mouseX, mouseY, mouseEvent, relativeX, relativeY);
	}
	
	private float alterIncrementAmount(float inc)
	{
		if (Minecraft.IS_RUNNING_ON_MAC ? Keyboard.isKeyDown(Keyboard.KEY_LMETA) : Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
			inc *= 0.5F;
		
		if (Minecraft.IS_RUNNING_ON_MAC ? Keyboard.isKeyDown(Keyboard.KEY_RMETA) : Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
			inc *= 0.1F;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LMENU))
			inc *= 0.25F;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_RMENU))
			inc *= 10.0F;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_Z))
			inc *= Math.round(Configs.armorZFightingBufferScale * 100000.0) / 100000.0;
		
		return inc;
	}
	
	public boolean isElementHovered(boolean isHelpMode)
	{
		return iconHovered || (isHelpMode && (buttonPlus.isMouseOver() || buttonMinus.isMouseOver()));
	}
	
	public String getElementHoverText(@Nullable String hoverHelpText)
	{
		if (iconHovered)
		{
			String name = entryObject.getType().getName();
			return hoverHelpText == null ? name : "GL Operation: " + name + "\n\n" + hoverHelpText;
		}
		return "If one of the fields to the left is focused, pressing these buttons will increase or decrease " +
				"its contents.\n\nHolding shift affects the base value of the change for the following GL operation fields:\n" +
				getBaseValueText(GlOperationType.ROTATION, "angle value", "1\u00B0", "90\u00B0") + "\n" +
				getBaseValueText(GlOperationType.TRANSLATION, "x/y/z values", "1 pixel", "1 meter") + "\n\nHolding the following buttons " +
				"multiplies the base value by:\n" + TextFormatting.AQUA + "All fields:" + TextFormatting.RESET + "\n" +
				GuiChiseledArmor.getPointSub("Left Control-click") + " 0.5\n" + GuiChiseledArmor.getPointSub("Left Alt-click") + " 0.25\n" +
				GuiChiseledArmor.getPointSub("Right Control-click") + " 0.1\n" + GuiChiseledArmor.getPointSub("Right Alt-click") + " 10\n" + 
				GuiChiseledArmor.getPointSub("Z-click") + " Z-fighting buffer (default = 1/20th pixel)";
	}
	
	private String getBaseValueText(GlOperationType operationType, String fieldType, String click, String shiftClick)
	{
		return TextFormatting.AQUA + operationType.getName() + " " + fieldType + ":" + TextFormatting.RESET + "\n" + GuiChiseledArmor.getPointSub("Click") +
				" " + click + "\n" + GuiChiseledArmor.getPointSub("Shift-click") + " " + shiftClick;
	}
	
}