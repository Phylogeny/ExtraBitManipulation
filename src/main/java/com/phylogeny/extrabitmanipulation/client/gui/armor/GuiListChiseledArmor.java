package com.phylogeny.extrabitmanipulation.client.gui.armor;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.phylogeny.extrabitmanipulation.armor.DataChiseledArmorPiece;
import com.phylogeny.extrabitmanipulation.client.GuiHelper;

public class GuiListChiseledArmor<E> extends GuiListExtended
{
	protected final GuiChiseledArmor guiChiseledArmor;
	protected final List<GuiListEntryChiseledArmor<E>> entries = Lists.<GuiListEntryChiseledArmor<E>>newArrayList();
	protected DataChiseledArmorPiece armorPiece;
	private int selectedIndex = 0;
	private boolean drawEntries;
	
	public GuiListChiseledArmor(GuiChiseledArmor guiChiseledArmor, int height,
			int top, int bottom, int slotHeight, int offsetX, int listWidth, DataChiseledArmorPiece armorPiece)
	{
		super(guiChiseledArmor.mc, 150, height, top, bottom, slotHeight);
		this.guiChiseledArmor = guiChiseledArmor;
		drawEntries = true;
		headerPadding -= 1;
		left = guiChiseledArmor.getGuiLeft() + offsetX;
		right = left + listWidth;
		this.armorPiece = armorPiece;
	}
	
	public void refreshList()
	{
		entries.clear();
	}
	
	public void setDrawEntries(boolean drawEntries)
	{
		this.drawEntries = drawEntries;
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent)
	{
		if (!isMouseYWithinSlotBounds(mouseY))
			return false;
		
		int i = getSlotIndexFromScreenCoords(mouseX, mouseY);
		if (i < 0)
			return false;
		
		if (getListEntry(i).mousePressed(i, mouseX, mouseY, mouseEvent, mouseX - left,
				mouseY - top + getAmountScrolled() - i * slotHeight - headerPadding - 1))
		{
			setEnabled(false);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int mouseEvent)
	{
		int relativeX = mouseX - left;
		int relativeY = mouseY - top + getAmountScrolled() - headerPadding - 1;
		for (int i = 0; i < getSize(); ++i)
		{
			getListEntry(i).mouseReleased(i, mouseX, mouseY, mouseEvent, relativeX, relativeY - i * slotHeight);
		}
		setEnabled(true);
		return false;
	}
	
	@Override
	public int getSlotIndexFromScreenCoords(int posX, int posY)
	{
		int y = posY - top - headerPadding + (int)amountScrolled - 1;
		int index = y / slotHeight;
		return posX < getScrollBarX() && posX >= left && posX <= left + getListWidth() && y >= 0 && index >= 0 && index < getSize() ? index : -1;
	}
	
	public void updateScreen()
	{
		for (int i = 0; i < getSize(); i++)
		{
			entries.get(i).updateScreen(isSelected(i));
		}
	}
	
	public void keyTyped(char typedChar, int keyCode)
	{
		GuiListEntryChiseledArmor<E> selectedEntry = getSelectedListEntry();
		if (selectedEntry != null)
			selectedEntry.keyTyped(typedChar, keyCode);
	}
	
	public void drawScreen(int mouseXIn, int mouseYIn)
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
		int k = left;
		int l = top + 2 - (int) amountScrolled;
		
		if (hasListHeader)
			drawListHeader(k, l, tessellator);
		
		int top = this.top + headerPadding + 1;
		if (drawEntries)
		{
			GuiHelper.glScissor(left, top, getListWidth() + 5, bottom - top);
			drawSelectionBox(k, l, mouseXIn, mouseYIn);
			GuiHelper.glScissorDisable();
		}
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
		GlStateManager.disableAlpha();
		GlStateManager.shadeModel(7425);
		GlStateManager.disableTexture2D();
		int j1 = getMaxScroll();
		if (j1 > 0)
		{
			int k1 = (bottom - top) * (bottom - top) / getContentHeight();
			k1 = MathHelper.clamp_int(k1, 32, bottom - top - 8);
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
	
	@Override
	protected void drawSelectionBox(int insideLeft, int insideTop, int mouseX, int mouseY)
	{
		int i = getSize();
		for (int j = 0; j < i; ++j)
		{
			int k = insideTop + j * slotHeight + headerPadding;
			int l = slotHeight - 4;
			if (k > bottom || k + l < top)
				updateItemPos(j, insideLeft, k);
			
			if (showSelectionBox && isSelected(j))
			{
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableTexture2D();
				int color = (255 << 24) + (120 << 16) + (120 << 8) + 200;
				Gui.drawRect(insideLeft, k - 1, insideLeft + getListWidth() - 8, k + slotHeight - 1, color);
				GlStateManager.enableBlend();
				GlStateManager.enableTexture2D();
			}
			drawSlot(j, insideLeft, k - 1, l, mouseX, mouseY);
		}
	}
	
	@Override
	protected boolean isSelected(int index)
	{
		return index == selectedIndex;
	}
	
	public void selectListEntry(int index)
	{
		selectedIndex = index;
	}
	
	public int getSelectListEntryIndex()
	{
		return selectedIndex;
	}
	
	@Nullable
	public GuiListEntryChiseledArmor<E> getSelectedListEntry()
	{
		return selectedIndex >= 0 && selectedIndex < getSize() ? getListEntry(selectedIndex) : null;
	}
	
	@Override
	public GuiListEntryChiseledArmor<E> getListEntry(int index)
	{
		return entries.get(index);
	}
	
	@Override
	public int getSize()
	{
		return entries.size();
	}
	
	@Override
	protected int getScrollBarX()
	{
		return right - 8;
	}
	
	@Override
	protected int getContentHeight()
	{
		return super.getContentHeight() - 3;
	}
	
	@Override
	public int getListWidth()
	{
		return right - left;
	}
	
	public GuiChiseledArmor getParentGui()
	{
		return guiChiseledArmor;
	}
	
}