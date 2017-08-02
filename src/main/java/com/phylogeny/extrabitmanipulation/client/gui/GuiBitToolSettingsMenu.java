package com.phylogeny.extrabitmanipulation.client.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiSlider.ISlider;
import net.minecraftforge.fml.client.config.GuiUtils;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingTool;
import com.phylogeny.extrabitmanipulation.shape.Shape;

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
		if (stack.isEmpty())
			return;
		
		if (ItemStackHelper.isBitWrenchStack(stack))
		{
			lineCount = 1;
			addButtonsSettings(new ButtonsSetting.WrenchMode(), "Mode", ItemBitWrench.MODE_TEXT);
		}
		else if (ItemStackHelper.isModelingToolStack(stack))
		{
			lineCount = 3;
			addButtonsSettings(new ButtonsSetting.ModelAreaMode(), "Area Mode", ItemModelingTool.AREA_MODE_TITLES);
			String[] snapTexts = ItemModelingTool.SNAP_MODE_TITLES;
			String[] snapTextsNew = new String[snapTexts.length];
			for (int i = 0; i < snapTexts.length; i++)
				snapTextsNew[i] = snapTexts[i].replace("Snap-to-Chunk ", "");
			
			addButtonsSettings(new ButtonsSetting.ModelSnapMode(), "Chunk Snap", snapTextsNew);
			addButtonsSettings(new ButtonsSetting.ModelGuiOpen(), "Open GUI", "On Read", "Off");
		}
		else if (ItemStackHelper.isSculptingToolStack(stack))
		{
			lineCount = 8; // TODO increment when triangular shapes are implemented
			ItemSculptingTool sculptingTool = (ItemSculptingTool) stack.getItem();
			if (!sculptingTool.removeBits())
				lineCount++;
			
			addButtonsSettings(new ButtonsSetting.SculptMode(), "Mode", ItemSculptingTool.MODE_TITLES);
			String[] texts = sculptingTool.isCurved() ? Arrays.copyOfRange(Shape.SHAPE_NAMES, 0, 3)
					: new String[]{Shape.SHAPE_NAMES[3], Shape.SHAPE_NAMES[6]};
			//Arrays.copyOfRange(Shape.SHAPE_NAMES, 3, 7) TODO
			addButtonsSettings(new ButtonsSetting.ShapeType(), "Shape", texts);
			addButtonsSettings(new ButtonsSetting.Direction(), "Direction", BitToolSettingsHelper.getDirectionNames());
			addButtonsSettings(new ButtonsSetting.BitGridTargeted(), "Target", "Bits", "Bit Grid");
			addButtonsSettings(new ButtonsSetting.HollowShape(), "Interior", "Hollow", "Solid");
			addButtonsSettings(new ButtonsSetting.OpenEnds(), "Ends", "Open", "Closed");
			if (!sculptingTool.removeBits())
				addButtonsSettings(new ButtonsSetting.OffsetShape(), "Shape Placement", "Offset", "Centered");
			
			addSliderSetting(new SliderSetting.SemiDiameter(), "Semi Diameter");
			addSliderSetting(new SliderSetting.WallThickness(), "Wall Thickness");
		}
		else if (ItemStackHelper.isChiseledArmorStack(stack))
		{
			lineCount = 4;
			addButtonsSettings(new ButtonsSetting.ArmorMode(), "Mode", ItemChiseledArmor.MODE_TITLES);
			addButtonsSettings(new ButtonsSetting.ArmorScale(), "Scale", ItemChiseledArmor.SCALE_TITLES);
			addButtonsSettings(new ButtonsSetting.ArmorGridTarget(), "Collection Grid", "Blocks", "Bits");
			addButtonsSettings(new ButtonsSetting.ArmorMovingPart(), "Moving Part", ((ItemChiseledArmor) stack.getItem()).MOVING_PART_TITLES);
		}
	}
	
	protected void addSliderSetting(SliderSetting sliderSetting, String title)
	{
		int x = getX();
		int y = getY();
		createLabel(title, x, y);
		GuiSliderSetting slider = new GuiSliderSetting(buttonCount++, x + 13, y - 1, " Bits", sliderSetting.getMaxValue(), sliderSetting.getValue(), this);
		sliderSetting.createElements(slider);
		sliderSetting.addAllElements(buttonList);
		sliderSettingList.add(sliderSetting);
		lineCount -= 2;
	}
	
	private void addButtonsSettings(ButtonsSetting buttons, String title, String... buttonTexts)
	{
		int x = getX();
		int y = getY();
		createLabel(title, x, y);
		for (int i = 0; i < buttonTexts.length; i++)
		{
			int buttonWidth = fontRenderer.getStringWidth(buttonTexts[i]) + 6;
			buttons.addButton(new GuiButtonSetting(buttonCount++, x, y, buttonWidth, buttonTexts[i]));
			x += buttonWidth + 4;
		}
		buttons.initButtons();
		buttonList.addAll(buttons.getButtons());
		buttonsSettingList.add(buttons);
	}
	
	protected void createLabel(String title, int x, int y)
	{
		GuiLabel label = new GuiLabel(fontRenderer, buttonCount, x - fontRenderer.getStringWidth(title) - 10, y + 1, width, 13, -1);
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
				sliderSetting.getSlider().mousePressed(mc, mouseX, mouseY);
			
			setToolValuesIfDiffrent();
			mc.player.closeScreen();
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
						buttonsSetting.buttons.get(i2).selected = false;
					
					button2.selected = true;
				}
			}
		}
		for (SliderSetting sliderSetting : sliderSettingList)
			sliderSetting.increment(button);
		
	}
	
	private void setToolValuesIfDiffrent()
	{
		for (ButtonsSetting buttonsSetting : buttonsSettingList)
			buttonsSetting.setValueIfDiffrent();
		
		for (SliderSetting sliderSetting : sliderSettingList)
			sliderSetting.setValueIfDiffrent();
	}
	
	@Override
	public void onChangeSliderValue(GuiSlider slider)
	{
		setToolValuesIfDiffrent();
	}
	
	protected class GuiButtonSetting extends GuiButtonSelect
	{
		private List<GuiButtonSetting> buttons;
		
		public GuiButtonSetting(int buttonId, int x, int y, int width, String text)
		{
			super(buttonId, x, y, width, 12, text, "", -16726016, -8882056);
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
			drawRect(x, y, x + width, y + height, selected ? colorFirst : colorSecond);
		}
		
		public void setButtonList(List<GuiButtonSetting> buttons)
		{
			this.buttons = buttons;
		}
		
	}
	
	protected class GuiSliderSetting extends GuiSlider
	{
		
		public GuiSliderSetting(int id, int xPos, int yPos, String suf, double maxVal, double currentVal, ISlider par)
		{
			super(id, xPos, yPos, 100, 14, "", suf, 0, maxVal, currentVal, false, true, par);
		}
		
		@Override
		protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
		{
			if (!visible)
				return;
			
			if (dragging)
			{
				sliderValue = (mouseX - (x + 4)) / (float)(width - 8);
				updateSlider();
			}
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, x + (int)(sliderValue * (width - 8)), y, 0, 66, 8, height, 200, 20, 2, 3, 2, 2, zLevel);
		}
		
	}
	
}