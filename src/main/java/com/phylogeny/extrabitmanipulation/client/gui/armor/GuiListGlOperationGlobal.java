package com.phylogeny.extrabitmanipulation.client.gui.armor;

import java.util.List;

import com.phylogeny.extrabitmanipulation.armor.DataChiseledArmorPiece;
import com.phylogeny.extrabitmanipulation.armor.GlOperation;

public class GuiListGlOperationGlobal extends GuiListGlOperation<DataChiseledArmorPiece>
{
	private boolean isPre;
	
	public GuiListGlOperationGlobal(GuiChiseledArmor guiChiseledArmor, int height, int top, int bottom,
			int slotHeight, int offsetX, int listWidth, DataChiseledArmorPiece armorPiece, boolean isPre)
	{
		super(guiChiseledArmor, height, top, bottom, slotHeight, offsetX, listWidth, armorPiece);
		this.isPre = isPre;
	}
	
	@Override
	public List<GlOperation> getGlOperations()
	{
		return armorPiece.getGlobalGlOperations(isPre);
	}
	
}