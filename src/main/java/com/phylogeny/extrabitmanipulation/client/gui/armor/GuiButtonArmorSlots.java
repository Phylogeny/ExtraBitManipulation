package com.phylogeny.extrabitmanipulation.client.gui.armor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.client.gui.button.GuiButtonBase;
import com.phylogeny.extrabitmanipulation.client.render.RenderState;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorMovingPart;
import com.phylogeny.extrabitmanipulation.packet.PacketOpenInventoryGui;

public class GuiButtonArmorSlots extends GuiButtonBase
{
	private GuiContainer gui;
	private int mouseInitialX, offsetX, offsetY, posX, posY;
	
	public GuiButtonArmorSlots(GuiContainer gui, String buttonText)
	{
		super(384736845, 0, 0, 12, 10, buttonText, "");
		setHoverHelpText("While holding SHIFT + CONTROL + ALT:\n" + GuiChiseledArmor.getPointSub("1) ") +
				"Click & drag to change position.\n" + GuiChiseledArmor.getPointSub("2) ") + "Press R to reset position.");
		this.gui = gui;
		setPosition();
	}
	
	public void setPosition()
	{
		resetOffsets();
		Pair<Integer, Integer> pos = BitToolSettingsHelper.getArmorButtonPosition();
		posX = pos.getLeft();
		posY = pos.getRight();
		setPosisionAbsolute();
	}
	
	public static boolean shouldMoveButton()
	{
		return GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown() && GuiScreen.isAltKeyDown();
	}
	
	private void setPosisionAbsolute()
	{
		xPosition = gui.getGuiLeft() + posX;
		yPosition = gui.getGuiTop() + posY;
	}
	
	private void resetOffsets()
	{
		offsetX = offsetY = mouseInitialX = 0;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (ClientHelper.getPlayer().capabilities.isCreativeMode)
		{
			visible = false;
			return;
		}
		setPosisionAbsolute();
		hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, -200);
		GlStateManager.enableDepth();
		RenderState.renderStateModelIntoGUI(null, ArmorMovingPart.HEAD.getIconModels()[0],
				ItemStack.EMPTY, hovered ? 1.0F : 0.5F, true, false, xPosition - 8, yPosition - 1, 0, 0, 1);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.translate(0, 0, 500);
		mouseDragged(mc, mouseX, mouseY);
		if (hovered)
		{
			int y = yPosition + 2;
			for (String string : mc.fontRendererObj.listFormattedStringToWidth(displayString, 45))
				drawCenteredString(mc.fontRendererObj, string, xPosition + 6, y += mc.fontRendererObj.FONT_HEIGHT, 14737632);
		}
		GlStateManager.popMatrix();
	}
	
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
	{
		boolean pressed = super.mousePressed(mc, mouseX, mouseY);
		if (pressed)
		{
			offsetX = mouseX - posX;
			offsetY = mouseY - posY;
			if (shouldMoveButton())
			{
				mouseInitialX = mouseX;
			}
			else
			{
				boolean openVanilla = mc.currentScreen instanceof GuiInventoryArmorSlots;
				if (openVanilla)
					((GuiInventoryArmorSlots) gui).openVanillaInventory(mouseX, mouseY);
				
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketOpenInventoryGui(openVanilla));
			}
		}
		else
		{
			resetOffsets();
		}
		return pressed;
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY)
	{
		resetOffsets();
		BitToolSettingsHelper.setArmorButtonPosition(posX, posY);
	}
	
	@Override
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
	{
		if (mouseInitialX > 0 && shouldMoveButton())
		{
			posX = mouseX - offsetX;
			posY = mouseY - offsetY;
		}
	}
	
	@Override
	public void playPressSound(SoundHandler soundHandlerIn)
	{
		if (!shouldMoveButton())
			super.playPressSound(soundHandlerIn);
	}
	
}