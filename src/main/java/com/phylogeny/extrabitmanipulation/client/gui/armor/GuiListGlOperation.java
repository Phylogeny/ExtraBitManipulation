package com.phylogeny.extrabitmanipulation.client.gui.armor;

import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.text.TextFormatting;

import com.phylogeny.extrabitmanipulation.armor.DataChiseledArmorPiece;
import com.phylogeny.extrabitmanipulation.armor.GlOperation;

public abstract class GuiListGlOperation<L> extends GuiListChiseledArmor<GlOperation>
{
	private static final String[] glComponents = new String[]{TextFormatting.BOLD + "x", TextFormatting.BOLD + "y", TextFormatting.BOLD + "z", "\u03B1"};
	
	public GuiListGlOperation(GuiChiseledArmor guiChiseledArmor, int height, int top,
			int bottom, int slotHeight, int offsetX, int listWidth, DataChiseledArmorPiece armorPiece)
	{
		super(guiChiseledArmor, height, top, bottom, slotHeight, offsetX, listWidth, armorPiece);
		setHasListHeader(true, 11);
	}
	
	public abstract List<GlOperation> getGlOperations();
	
	public List<GlOperation> addGlOperation(int index, GlOperation glOperation)
	{
		List<GlOperation> glOperations = getGlOperations();
		glOperations.add(index, glOperation);
		return glOperations;
	}
	
	public List<GlOperation> removeGlOperation(int index)
	{
		List<GlOperation> glOperations = getGlOperations();
		glOperations.remove(index);
		return glOperations;
	}
	
	public List<GlOperation> moveGlOperation(int index, GlOperation glOperation, boolean moveUp)
	{
		List<GlOperation> glOperations = getGlOperations();
		glOperations.remove(index);
		index += (moveUp ? -1 : 1);
		glOperations.add(index, glOperation);
		return glOperations;
	}
	
	@Override
	public void refreshList()
	{
		super.refreshList();
		for (int i = 0; i < getGlOperations().size(); i++)
		{
			entries.add(new GuiListEntryGlOperation(this, getGlOperations().get(i), i));
		}
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent)
	{
		for (GuiListEntryChiseledArmor<GlOperation> entry : entries)
		{
			((GuiListEntryGlOperation) entry).formatDataFields(mouseX, mouseY);
		}
		return super.mouseClicked(mouseX, mouseY, mouseEvent);
	}
	
	@Override
	protected void drawListHeader(int insideLeft, int insideTop, Tessellator tessellatorIn)
	{
		for (int i = 0; i < glComponents.length; i++)
		{
			mc.fontRenderer.drawString(glComponents[i], (int) (insideLeft + 37 + i * (i == 3 ? 42.5 : 45)), top + 1, -1);
		}
	}
	
}