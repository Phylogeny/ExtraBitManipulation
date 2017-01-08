package com.phylogeny.extrabitmanipulation.api.jei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.BlankRecipeWrapper;

public class InfoRecipeBase extends BlankRecipeWrapper
{
	protected List<ItemStack> itemStacks;
	protected List<String> tooltipLines = new ArrayList<String>();
	protected ResourceLocation image;
	protected int imageWidth, imageHeight;
	protected IDrawable slotDrawable;
	protected String name;
	protected Rectangle imageBox;
	
	public InfoRecipeBase(List<ItemStack> sculptingStacks, int imageWidth, int imageHeight, String recipeName,
			String imageName, String tooltipName, int imageLeft, int imageTop, int imageRight, int imageBottom, String catagoryName)
	{
		this.itemStacks = sculptingStacks;
		this.image = new ResourceLocation(Reference.GROUP_ID, "textures/jei/images/" + imageName + ".png");
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.name = JustEnoughItemsPlugin.translate(catagoryName + ".name." + recipeName);
		tooltipLines.addAll(Arrays.asList(JustEnoughItemsPlugin.translate(tooltipName + ".tooltip").split("\\\\n")));
		imageBox = new Rectangle(imageLeft, imageTop, imageRight - imageLeft, imageBottom - imageTop);
	}
	
	@Override
	public List<ItemStack> getInputs()
	{
		return itemStacks;
	}
	
	@Override
	public List<ItemStack> getOutputs()
	{
		return itemStacks;
	}
	
	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY)
	{
		List<String> getTooltips = new ArrayList<String>();
		if (imageBox.contains(mouseX, mouseY))
			getTooltips.addAll(tooltipLines);
		
		return getTooltips;
	}
	
}