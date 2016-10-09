package com.phylogeny.extrabitmanipulation.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.phylogeny.extrabitmanipulation.item.ItemModelMaker.BitCount;

import mod.chiselsandbits.api.IBitBrush;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;

public class GuiListBitMapping extends GuiListExtended
{
	private final GuiModelMaker guiModelMaker;
	private final List<GuiListBitMappingEntry> entries = Lists.<GuiListBitMappingEntry>newArrayList();
	
	public GuiListBitMapping(GuiModelMaker guiModelMaker, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
	{
		super(guiModelMaker.mc, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
		this.guiModelMaker = guiModelMaker;
		headerPadding += 1;
		left = guiModelMaker.getGuiLeft() + 18;
		right = guiModelMaker.getGuiLeft() + 93;
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
	
	public void refreshList(HashMap<IBlockState, IBitBrush> stateToBitMap, HashMap<IBlockState, IBitBrush> stateToBitMapPermanent,
			HashMap<IBlockState, ArrayList<BitCount>> stateToBitCountArray, String searchText, boolean stateMode)
	{
		entries.clear();
		if (stateToBitCountArray != null)
		{
			for (Map.Entry<IBlockState, ArrayList<BitCount>> entry : stateToBitCountArray.entrySet())
			{
				IBlockState state = entry.getKey();
				if (!searchText.isEmpty() && (stateMode ? state.toString()
						: Block.blockRegistry.getNameForObject(state.getBlock())).toString().indexOf(searchText) < 0)
					continue;
				
				entries.add(new GuiListBitMappingEntry(this, state, entry.getValue(), stateToBitMapPermanent.containsKey(state), false));
			}
		}
		else if (stateToBitMap != null)
		{
			for (Map.Entry<IBlockState, IBitBrush> entry : stateToBitMap.entrySet())
			{
				IBlockState state = entry.getKey();
				if (!searchText.isEmpty() && (stateMode ? state.toString()
						: Block.blockRegistry.getNameForObject(state.getBlock())).toString().indexOf(searchText) < 0)
					continue;
				
				ArrayList<BitCount> bitCountArray = new ArrayList<BitCount>();
				bitCountArray.add(new BitCount(entry.getValue(), 0));
				entries.add(new GuiListBitMappingEntry(this, state, bitCountArray, stateToBitMapPermanent.containsKey(state), true));
			}
		}
	}
	
	@Override
	public void drawScreen(int mouseXIn, int mouseYIn, float partialTicks)
	{
		if (field_178041_q)
		{
			mouseX = mouseXIn;
			mouseY = mouseYIn;
			drawBackground();
			int i = getScrollBarX();
			int j = i + 6;
			bindAmountScrolled();
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldRenderer = tessellator.getWorldRenderer();
			int k = left + width / 2 - getListWidth() / 2 + 2;
			int l = top + 4 - (int)amountScrolled;
			
			if (hasListHeader)
				drawListHeader(k, l, tessellator);
			
			drawSelectionBox(k, l, mouseXIn, mouseYIn);
			GlStateManager.disableDepth();
			drawOverlays();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
			GlStateManager.disableAlpha();
			GlStateManager.shadeModel(7425);
			GlStateManager.disableTexture2D();
			
			int j1 = func_148135_f();
			
			if (j1 > 0)
			{
				int k1 = (bottom - top) * (bottom - top) / getContentHeight();
				k1 = MathHelper.clamp_int(k1, 32, bottom - top - 8);
				int l1 = (int)amountScrolled * (bottom - top - k1) / j1 + top;
				
				if (l1 < top)
					l1 = top;
				
				worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				worldRenderer.pos(i, bottom + 1, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
				worldRenderer.pos(j, bottom + 1, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
				worldRenderer.pos(j, top - 1, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
				worldRenderer.pos(i, top - 1, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
				tessellator.draw();
				worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				worldRenderer.pos(i + 1, (l1 + k1), 0.0D).tex(0.0D, 1.0D).color(139, 139, 139, 255).endVertex();
				worldRenderer.pos(j - 1, (l1 + k1), 0.0D).tex(1.0D, 1.0D).color(139, 139, 139, 255).endVertex();
				worldRenderer.pos(j - 1, l1, 0.0D).tex(1.0D, 0.0D).color(139, 139, 139, 255).endVertex();
				worldRenderer.pos(i + 1, l1, 0.0D).tex(0.0D, 0.0D).color(139, 139, 139, 255).endVertex();
				tessellator.draw();
			}
			
			func_148142_b(mouseXIn, mouseYIn);
			GlStateManager.enableTexture2D();
			GlStateManager.shadeModel(7424);
			GlStateManager.enableAlpha();
			GlStateManager.disableBlend();
		}
	}
	
	protected void drawOverlays()
	{
		mc.getTextureManager().bindTexture(guiModelMaker.GUI_TEXTURE);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		int left = guiModelMaker.getGuiLeft() - 24;
		int top = guiModelMaker.getGuiTop();
		guiModelMaker.drawTexturedModalRect(left, top, 0, 0, 254, 21);
		int offsetY = 121;
		guiModelMaker.drawTexturedModalRect(left, top + offsetY, 0, offsetY, 254, 219 - offsetY);
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
		return guiModelMaker.getGuiLeft() + 85;
	}
	
	@Override
	protected int getContentHeight()
	{
		return super.getContentHeight() - 1;
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
	
	public GuiModelMaker getGuiModelMaker()
	{
		return guiModelMaker;
	}
	
}