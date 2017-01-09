package com.phylogeny.extrabitmanipulation.client.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.client.RenderState;
import com.phylogeny.extrabitmanipulation.helper.BitIOHelper;
import com.phylogeny.extrabitmanipulation.helper.BitInventoryHelper;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool.BitCount;
import com.phylogeny.extrabitmanipulation.packet.PacketCursorStack;

import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class GuiListBitMappingEntry implements GuiListExtended.IGuiListEntry
{
	private final Minecraft mc;
	private final GuiBitMapping bitMappingScreen;
	private IBlockState state;
	private ArrayList<BitCount> bitCountArray;
	private boolean isManuallyMapped, isInteractive;
	private int frameCounter;
	
	public GuiListBitMappingEntry(GuiListBitMapping listBitMapping, IBlockState state,
			ArrayList<BitCount> bitCountArray, boolean isManuallyMapped, boolean isInteractive)
	{
		bitMappingScreen = listBitMapping.getGuiModelingTool();
		mc = bitMappingScreen.mc;
		this.state = state;
		this.bitCountArray = bitCountArray;
		this.isManuallyMapped = isManuallyMapped;
		this.isInteractive = isInteractive;
	}
	
	public boolean isInteractive()
	{
		return isInteractive;
	}
	
	public IBlockState getState()
	{
		return state;
	}
	
	private BitCount getBitCountObject()
	{
		if (bitCountArray.isEmpty())
			return null;
		
		return bitCountArray.get(frameCounter % (bitCountArray.size() * 120) / 120);
	}
	
	public ArrayList<BitCount> getBitCountArray()
	{
		return bitCountArray;
	}
	
	private IBitBrush getBit()
	{
		BitCount bitCount = getBitCountObject();
		if (bitCount == null)
			return null;
		
		return bitCount.getBit();
	}
	
	public ItemStack getBitStack()
	{
		IBitBrush bit = getBit();
		return bit == null ? null : bit.getItemStack(1);
	}
	
	public boolean isAir()
	{
		IBitBrush bit = getBit();
		return bit != null && bit.isAir();
	}
	
	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
	{
		frameCounter++;
		x -= 43;
		y -= 1;
		int guiTop = bitMappingScreen.getGuiTop();
		if (y > guiTop && y < guiTop + 125)
		{
			if (isManuallyMapped)
			{
				bitMappingScreen.drawRect(x, y - 1, x + listWidth, y + slotHeight + 1, 2110310655);
				GlStateManager.enableBlend();
			}
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			mc.getTextureManager().bindTexture(bitMappingScreen.GUI_TEXTURE);
			bitMappingScreen.drawTexturedModalRect(x, y, 0, 219, listWidth, slotHeight);
			RenderHelper.enableGUIStandardItemLighting();
			if (getBitStack() != null)
			{
				mc.getRenderItem().renderItemIntoGUI(getBitStack(), x + 44, y + 2);
			}
			else if (getBit() == null)
			{
				drawCross(x, y);
			}
			RenderState.renderStateIntoGUI(state, x, y);
			RenderHelper.disableStandardItemLighting();
		}
	}
	
	private void drawCross(int x, int y)
	{
		GlStateManager.pushMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.color(1, 0, 0);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		ScaledResolution scaledresolution = new ScaledResolution(mc);
		GL11.glLineWidth(scaledresolution.getScaleFactor() * 2);
		vertexbuffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
		int x2 = x + 44;
		int y2 = y + 2;
		int x3 = x2 + 16;
		int y3 = y2 + 16;
		vertexbuffer.pos(x2, y2, 0).color(255, 0, 0, 255).endVertex();
		vertexbuffer.pos(x3, y3, 0).color(255, 0, 0, 255).endVertex();
		tessellator.draw();
		vertexbuffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos(x3, y2, 0).color(255, 0, 0, 255).endVertex();
		vertexbuffer.pos(x2, y3, 0).color(255, 0, 0, 255).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		x2 -= 1;
		y2 -= 1;
		bitMappingScreen.drawTexturedModalRect(x2, y2, 43, 220, 18, 1);
		bitMappingScreen.drawTexturedModalRect(x2, y2 + 17, 43, 237, 18, 1);
	}
	
	@Override
	public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY)
	{
		ItemStack cursorStack = mc.thePlayer.inventory.getItemStack();
		boolean inSlotVerticalRange = relativeY >= 0 && relativeY < 18;
		boolean stateSlotClicked = relativeX > -39 && relativeX < -20 && inSlotVerticalRange;
		boolean bitSlotClicked = relativeX >= 0 && relativeX < 18 && inSlotVerticalRange;
		if (cursorStack == null && mouseEvent == 2 && mc.thePlayer.capabilities.isCreativeMode && (stateSlotClicked || bitSlotClicked))
		{
			ItemStack stack = null;
			if (stateSlotClicked)
			{
				Item item = Item.getItemFromBlock(state.getBlock());
				if (item != null && item instanceof ItemBlock)
				{
					stack = new ItemStack(item, 64, item.getHasSubtypes() ? state.getBlock().getMetaFromState(state) : 0);
					stack.stackSize = stack.getMaxStackSize();
				}
			}
			if (bitSlotClicked && getBitStack() != null)
			{
				stack = getBitStack().copy();
				stack.stackSize = stack.getMaxStackSize();
			}
			if (stack != null)
			{
				mc.thePlayer.inventory.setItemStack(stack);
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketCursorStack(stack));
			}
		}
		
		if (!isInteractive || !bitSlotClicked || bitCountArray.size() != 1)
			return false;
		
		IBitBrush bit = bitCountArray.get(0).getBit();
		IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
		boolean changed = false;
		if (cursorStack != null)
		{
			if (BitInventoryHelper.isBitStack(api, cursorStack))
			{
				try
				{
					bit = api.createBrush(cursorStack);
					changed = true;
				}
				catch (InvalidBitItem e) {}
			}
			else if (cursorStack.getItem() != null)
			{
				Block block = Block.getBlockFromItem(cursorStack.getItem());
				if (block != null)
				{
					try
					{
						bit = api.createBrushFromState(BitIOHelper.getStateFromMeta(block, cursorStack.getMetadata()));
						changed = true;
					}
					catch (InvalidBitItem e) {}
				}
			}
		}
		else if (GuiScreen.isCtrlKeyDown())
		{
			bit = null;
			changed = true;
		}
		else if (GuiScreen.isShiftKeyDown())
		{
			try
			{
				bit = api.createBrushFromState(null);
				changed = true;
			}
			catch (InvalidBitItem e) {}
		}
		if (changed)
		{
			bitCountArray.get(0).setBit(bit);
			bitMappingScreen.addPermanentMapping(state, bit);
		}
		return changed;
	}
	
	@Override
	public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {}
	
	@Override
	public void setSelected(int entryID, int insideLeft, int yPos) {}
	
}