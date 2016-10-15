package com.phylogeny.extrabitmanipulation.capability;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public interface IBitToolSettingsHandler
{
	public void syncAllData(EntityPlayerMP player);
	
	public int getModelAreaMode();
	public void setModelAreaMode(int mode);
	
	public int getModelSnapMode();
	public void setModelSnapMode(int mode);
	
	public boolean getModelGuiOpen();
	public void setModelGuiOpen(boolean modelGuiOpen);
	
	public int getSculptMode();
	public void setSculptMode(int mode);
	
	public int getDirection();
	public void setDirection(int direction);
	
	public int getShapeTypeCurved();
	public void setShapeTypeCurved(int shapeTypeCurved);
	
	public int getShapeTypeFlat();
	public void setShapeTypeFlat(int shapeTypeFlat);
	
	public int getSculptSemiDiameter();
	public void setSculptSemiDiameter(int sculptSemiDiameter);
	
	public int getWallThickness();
	public void setWallThickness(int wallThickness);
	
	public boolean isBitGridTargeted();
	public void setBitGridTargeted(boolean targetBitGridVertexes);
	
	public boolean isShapeHollowWire();
	public void setShapeHollowWire(boolean sculptHollowShapeWire);
	
	public boolean isShapeHollowSpade();
	public void setShapeHollowSpade(boolean sculptHollowShapeSpade);
	
	public boolean areEndsOpen();
	public void setEndsOpen(boolean openEnds);
	
	public ItemStack getBitStackWire();
	public void setBitStackWire(ItemStack setBitWire);
	
	public ItemStack getBitStackSpade();
	public void setBitStackSpade(ItemStack setBitSpade);
	
}