package com.phylogeny.extrabitmanipulation.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mod.chiselsandbits.api.IBitBrush;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool.BitCount;

public class GuiListBitMapping extends GuiListExtended
{
	private final GuiBitMapping guiBitMapping;
	private final List<GuiListBitMappingEntry> entries = Lists.<GuiListBitMappingEntry>newArrayList();
	private boolean designMode;
	
	public GuiListBitMapping(GuiBitMapping guiModelingTool, int width, int height, int top, int bottom, int slotHeight, boolean designMode)
	{
		super(guiModelingTool.mc, width, height, top, bottom, slotHeight);
		this.guiBitMapping = guiModelingTool;
		headerPadding += 1;
		left = guiModelingTool.getGuiLeft() + 18;
		right = guiModelingTool.getGuiLeft() + 93;
		this.designMode = designMode;
	}
	
	@Override
	public int getSlotIndexFromScreenCoords(int posX, int posY)
	{
		int i = left;
		int j = left + width / 2 + getListWidth() / 2;
		int k = posY - top - headerPadding + (int)amountScrolled - 4;
		int l = k / slotHeight;
		return posX < getScrollBarX() && posX >= i && posX <= j && l >= 0 && k >= 0 && l < getSize() ? l : -1;
	}
	
	public void refreshList(Map<IBlockState, IBitBrush> stateToBitMap, Map<IBlockState, IBitBrush> stateToBitMapPermanent,
			Map<IBlockState, ArrayList<BitCount>> stateToBitCountArray, String searchText, boolean stateMode)
	{
		searchText = searchText.toLowerCase();
		entries.clear();
		if (!designMode && stateToBitCountArray != null)
		{
			for (Map.Entry<IBlockState, ArrayList<BitCount>> entry : stateToBitCountArray.entrySet())
			{
				IBlockState state = entry.getKey();
				if (searchTextMismatch(searchText, stateMode, state))
					continue;
				
				entries.add(new GuiListBitMappingEntry(this, state, entry.getValue(), stateToBitMapPermanent.containsKey(state), false));
			}
		}
		else if (stateToBitMap != null)
		{
			for (Map.Entry<IBlockState, IBitBrush> entry : stateToBitMap.entrySet())
			{
				IBlockState state = entry.getKey();
				if (searchTextMismatch(searchText, stateMode, state))
					continue;
				
				ArrayList<BitCount> bitCountArray = new ArrayList<BitCount>();
				bitCountArray.add(new BitCount(entry.getValue(), designMode ? stateToBitCountArray.get(state).get(0).getCount() : 0));
				entries.add(new GuiListBitMappingEntry(this, state, bitCountArray, stateToBitMapPermanent.containsKey(state), true));
			}
		}
	}
	
	private boolean searchTextMismatch(String text, boolean stateMode, IBlockState state)
	{
		return !text.isEmpty() && (stateMode ? state : Block.REGISTRY.getNameForObject(state.getBlock())).toString().toLowerCase().indexOf(text) < 0;
	}
	
	@Override
	public void drawScreen(int mouseXIn, int mouseYIn, float partialTicks)
	{
		if (!visible)
			return;
		
		mouseX = mouseXIn;
		mouseY = mouseYIn;
		drawBackground();
		int i = getScrollBarX();
		int j = i + 6;
		bindAmountScrolled();
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		int k = left + width / 2 - getListWidth() / 2 + 2;
		int l = top + 4 - (int)amountScrolled;
		
		if (hasListHeader)
			drawListHeader(k, l, tessellator);
		
		drawSelectionBox(k, l, mouseXIn, mouseYIn);
		GlStateManager.disableDepth();
		drawOverlays();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
		GlStateManager.disableAlpha();
		GlStateManager.shadeModel(7425);
		GlStateManager.disableTexture2D();
		
		int j1 = getMaxScroll();
		
		if (j1 > 0)
		{
			int k1 = (bottom - top) * (bottom - top) / getContentHeight();
			k1 = MathHelper.clamp(k1, 32, bottom - top - 8);
			int l1 = (int)amountScrolled * (bottom - top - k1) / j1 + top;
			
			if (l1 < top)
				l1 = top;
			
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			buffer.pos(i, bottom + 1, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			buffer.pos(j, bottom + 1, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			buffer.pos(j, top - 1, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
			buffer.pos(i, top - 1, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
			tessellator.draw();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			buffer.pos(i + 1, (l1 + k1), 0.0D).tex(0.0D, 1.0D).color(139, 139, 139, 255).endVertex();
			buffer.pos(j - 1, (l1 + k1), 0.0D).tex(1.0D, 1.0D).color(139, 139, 139, 255).endVertex();
			buffer.pos(j - 1, l1, 0.0D).tex(1.0D, 0.0D).color(139, 139, 139, 255).endVertex();
			buffer.pos(i + 1, l1, 0.0D).tex(0.0D, 0.0D).color(139, 139, 139, 255).endVertex();
			tessellator.draw();
		}
		
		renderDecorations(mouseXIn, mouseYIn);
		GlStateManager.enableTexture2D();
		GlStateManager.shadeModel(7424);
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
	}
	
	protected void drawOverlays()
	{
		ClientHelper.bindTexture(guiBitMapping.GUI_TEXTURE);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		int left = guiBitMapping.getGuiLeft() - 24;
		int top = guiBitMapping.getGuiTop();
		guiBitMapping.drawTexturedModalRect(left, top, 0, 0, 254, 21);
		int offsetY = 121;
		if (designMode)
		{
			guiBitMapping.drawTexturedModalRect(left + 24, top + offsetY, 24, offsetY, 254 - 24, 219 - offsetY);
		}
		else
		{
			guiBitMapping.drawTexturedModalRect(left, top + offsetY, 0, offsetY, 254, 219 - offsetY);
		}
	}
	
	@Override
	public GuiListBitMappingEntry getListEntry(int index)
	{
		return entries.get(index);
	}
	
	@Override
	protected int getSize()
	{
		return entries.size();
	}
	
	@Override
	protected int getScrollBarX()
	{
		return guiBitMapping.getGuiLeft() + 85;
	}
	
	@Override
	protected int getContentHeight()
	{
		return super.getContentHeight() - 25;
	}
	
	@Override
	public int getListWidth()
	{
		return 66;
	}
	
	@Override
	protected boolean isSelected(int slotIndex)
	{
		return false;
	}
	
	public GuiBitMapping getGuiModelingTool()
	{
		return guiBitMapping;
	}
	
}