package com.phylogeny.extrabitmanipulation.client.gui.armor;

import java.util.List;

import com.phylogeny.extrabitmanipulation.armor.ArmorItem;
import com.phylogeny.extrabitmanipulation.armor.DataChiseledArmorPiece;

public class GuiListArmorItem extends GuiListChiseledArmor<ArmorItem>
{
	private int partIndex;
	
	public GuiListArmorItem(GuiChiseledArmor guiChiseledArmor, int heightIn, int topIn, int bottomIn,
			int slotHeightIn, int offsetX, int listWidth, DataChiseledArmorPiece armorPiece, int partIndex)
	{
		super(guiChiseledArmor, heightIn, topIn, bottomIn, slotHeightIn, offsetX, listWidth, armorPiece);
		this.partIndex = partIndex;
	}
	
	@Override
	public void refreshList()
	{
		super.refreshList();
		List<ArmorItem> armorItems = armorPiece.getArmorItemsForPart(partIndex);
		for (int i = 0; i < armorItems.size(); i++)
		{
			ArmorItem armorItem = armorItems.get(i);
			entries.add(new GuiListEntryArmorItem(this, armorItem));
		}
	}
	
}