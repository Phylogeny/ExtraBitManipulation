package com.phylogeny.extrabitmanipulation.client.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingTool;
import com.phylogeny.extrabitmanipulation.shape.Shape;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.client.config.GuiSlider.ISlider;

public class GuiBitToolSettingsMenu extends GuiScreen implements ISlider
{
	private List<ButtonsSetting> buttonsSettingList = Lists.<ButtonsSetting>newArrayList();
	private List<SliderSetting> sliderSettingList = Lists.<SliderSetting>newArrayList();
	private int buttonCount, lineCount;
	private boolean closing;
	private float visibility;
	private Stopwatch timer = Stopwatch.createStarted();
	
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
	
	@Override
	public void initGui()
	{
		ItemStack stack = ClientHelper.getHeldItemMainhand();
		if (stack == null)
			return;
		
		if (ItemStackHelper.isBitWrenchStack(stack))
		{
			lineCount = 1;
			addButtonsSettings(new ButtonsSetting.WrenchMode(), ItemBitWrench.MODE_TEXT, "Mode");
		}
		else if (ItemStackHelper.isModelingToolStack(stack))
		{
			lineCount = 3;
			addButtonsSettings(new ButtonsSetting.ModelAreaMode(), ItemModelingTool.AREA_MODE_TITLES, "Area Mode");
			String[] snapTexts = ItemModelingTool.SNAP_MODE_TITLES;
			String[] snapTextsNew = new String[snapTexts.length];
			for (int i = 0; i < snapTexts.length; i++)
			{
				snapTextsNew[i] = snapTexts[i].replace("Snap-to-Chunk ", "");
			}
			addButtonsSettings(new ButtonsSetting.ModelSnapMode(), snapTextsNew, "Chunk Snap");
			addButtonsSettings(new ButtonsSetting.ModelGuiOpen(), new String[]{"On Read", "Off"}, "Open GUI");
		}
		else if (ItemStackHelper.isSculptingToolStack(stack))
		{
			lineCount = 8; // TODO increment when triangular shapes are implemented
			ItemSculptingTool sculptingTool = (ItemSculptingTool) stack.getItem();
			if (!sculptingTool.removeBits())
				lineCount++;
			
			addButtonsSettings(new ButtonsSetting.SculptMode(), ItemSculptingTool.MODE_TITLES, "Mode");
			String[] texts = sculptingTool.isCurved() ? Arrays.copyOfRange(Shape.SHAPE_NAMES, 0, 3)
					: new String[]{Shape.SHAPE_NAMES[3], Shape.SHAPE_NAMES[6]};
			//Arrays.copyOfRange(Shape.SHAPE_NAMES, 3, 7) TODO
			addButtonsSettings(new ButtonsSetting.ShapeType(), texts, "Shape");
			addButtonsSettings(new ButtonsSetting.Direction(), BitToolSettingsHelper.getDirectionNames(), "Direction");
			addButtonsSettings(new ButtonsSetting.BitGridTargeted(), new String[]{"Bits", "Bit Grid"}, "Target");
			addButtonsSettings(new ButtonsSetting.HollowShape(), new String[]{"Hollow", "Solid"}, "Interior");
			addButtonsSettings(new ButtonsSetting.OpenEnds(), new String[]{"Open", "Closed"}, "Ends");
			if (!sculptingTool.removeBits())
				addButtonsSettings(new ButtonsSetting.OffsetShape(), new String[]{"Offset", "Centered"}, "Shape Placement");
			
			addSliderSetting(new SliderSetting.SemiDiameter(), "Semi Diameter");
			addSliderSetting(new SliderSetting.WallThickness(), "Wall Thickness");
		}
	}
	
	protected void addSliderSetting(SliderSetting sliderSetting, String title)
	{
		int x = getX();
		int y = getY();
		createLabel(title, x, y);
		GuiSliderSetting slider = new GuiSliderSetting(buttonCount++, x + 13, y - 1, 100, 14, "", " Bits", 0,
				sliderSetting.getMaxValue(), sliderSetting.getValue(), false, true, this);
		sliderSetting.createElements(slider);
		sliderSetting.addAllElements(buttonList);
		sliderSettingList.add(sliderSetting);
		lineCount -= 2;
	}
	
	private void addButtonsSettings(ButtonsSetting buttons, String[] buttonTexts, String title)
	{
		int x = getX();
		int y = getY();
		createLabel(title, x, y);
		for (int i = 0; i < buttonTexts.length; i++)
		{
			int buttonWidth = fontRendererObj.getStringWidth(buttonTexts[i]) + 6;
			buttons.addButton(new GuiButtonSetting(buttonCount++, x, y, buttonWidth, 12, buttonTexts[i], "", -16726016, -8882056));
			x += buttonWidth + 4;
		}
		buttons.initButtons();
		buttonList.addAll(buttons.getButtons());
		buttonsSettingList.add(buttons);
	}
	
	protected void createLabel(String title, int x, int y)
	{
		GuiLabel label = new GuiLabel(fontRendererObj, buttonCount, x - fontRendererObj.getStringWidth(title) - 10, y + 1, width, 13, -1);
		label.addLine(title + "");
		labelList.add(label);
	}
	
	private int getX()
	{
		return width / 2 + 5;
	}
	
	private int getY()
	{
		return height / 2 - lineCount * 10 + buttonsSettingList.size() * 20 + 4;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		if (timer == null)
			timer = Stopwatch.createStarted();
		
		drawGradientRect(0, 0, width, height, (int) (visibility * 98) << 24, (int) (visibility * 128) << 24);
		MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.BackgroundDrawnEvent(this));
		super.drawScreen(mouseX, mouseY, partialTicks);
		visibility = Math.min(timer.elapsed(TimeUnit.MILLISECONDS) * 0.01F, 1.0F);
		if (closing)
			visibility = 1 - visibility;
		
		if (visibility == 0)
		{
			for (SliderSetting sliderSetting : sliderSettingList)
			{
				sliderSetting.getSlider().mousePressed(mc, mouseX, mouseY);
			}
			setToolValuesIfDiffrent();
			mc.thePlayer.closeScreen();
		}
	}
	
	@Override
	public void handleKeyboardInput() throws IOException
	{
		if (!Keyboard.getEventKeyState())
		{
			closing = !Keyboard.getEventKeyState();
			timer = Stopwatch.createStarted();
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		setToolValuesIfDiffrent();
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		for (ButtonsSetting buttonsSetting : buttonsSettingList)
		{
			for (int i = 0; i < buttonsSetting.buttons.size(); i++)
			{
				GuiButtonBase button2 = buttonsSetting.buttons.get(i);
				if (button2.id == button.id)
				{
					for (int i2 = 0; i2 < buttonsSetting.buttons.size(); i2++)
					{
						buttonsSetting.buttons.get(i2).selected = false;
					}
					button2.selected = true;
				}
			}
		}
		for (SliderSetting sliderSetting : sliderSettingList)
		{
			sliderSetting.increment(button);
		}
	}
	
	private void setToolValuesIfDiffrent()
	{
		for (ButtonsSetting buttonsSetting : buttonsSettingList)
		{
			buttonsSetting.setValueIfDiffrent();
		}
		for (SliderSetting sliderSetting : sliderSettingList)
		{
			sliderSetting.setValueIfDiffrent();
		}
	}
	
	@Override
	public void onChangeSliderValue(GuiSlider slider)
	{
		setToolValuesIfDiffrent();
	}
	
	public class GuiButtonSetting extends GuiButtonSelect
	{
		private List<GuiButtonSetting> buttons;
		
		public GuiButtonSetting(int buttonId, int x, int y, int widthIn, int heightIn, String text, String hoverText, int colorFirst, int colorSecond)
		{
			super(buttonId, x, y, widthIn, heightIn, text, hoverText, colorFirst, colorSecond);
		}
		
		@Override
		protected void drawCustomRect()
		{
			boolean noneElseHovered = true;
			for (GuiButtonSetting button : buttons)
			{
				if (id != button.id && button.isMouseOver())
				{
					noneElseHovered = false;
					break;
				}
			}
			boolean selected = (this.selected && noneElseHovered) || isMouseOver();
			drawRect(xPosition, yPosition, xPosition + width, yPosition + height, selected ? colorFirst : colorSecond);
		}
		
		public void setButtonList(List<GuiButtonSetting> buttons)
		{
			this.buttons = buttons;
		}
		
	}
	
	public class GuiSliderSetting extends GuiSlider
	{
		
		public GuiSliderSetting(int id, int xPos, int yPos, int width, int height, String prefix, String suf,
				double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, ISlider par)
		{
			super(id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, par);
		}
		
		@Override
		protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
		{
			if (visible)
			{
				if (dragging)
				{
					sliderValue = (mouseX - (xPosition + 4)) / (float)(width - 8);
					updateSlider();
				}
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, xPosition + (int)(sliderValue * (width - 8)),
						yPosition, 0, 66, 8, height, 200, 20, 2, 3, 2, 2, zLevel);
			}
		}
		
	}
	
}