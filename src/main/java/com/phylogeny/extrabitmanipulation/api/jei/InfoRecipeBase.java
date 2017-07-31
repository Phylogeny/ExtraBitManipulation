package com.phylogeny.extrabitmanipulation.api.jei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.phylogeny.extrabitmanipulation.reference.Reference;

public class InfoRecipeBase extends BlankRecipeWrapper
{
	protected List<ItemStack> itemStacks;
	protected List<String> tooltipLines = new ArrayList<String>();
	protected ResourceLocation image;
	protected int imageWidth, imageHeight;
	protected IDrawable slotDrawable;
	protected String name;
	protected Rectangle imageBox;
	
	public InfoRecipeBase(IGuiHelper guiHelper, List<ItemStack> itemStacks, int imageWidth, int imageHeight, String recipeName,
			String imageName, String tooltipName, int imageLeft, int imageTop, int imageRight, int imageBottom, String catagoryName)
	{
		this.itemStacks = itemStacks;
		this.image = new ResourceLocation(Reference.MOD_ID, "textures/jei/images/" + imageName + ".png");
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.slotDrawable = guiHelper.getSlotDrawable();
		this.name = JustEnoughItemsPlugin.translate(catagoryName + ".name." + recipeName);
		String toolTip = ".tooltip";
		if (!tooltipName.contains("."))
			tooltipName += ".";
		else
			toolTip += ".";
		
		tooltipLines.addAll(Arrays.asList(JustEnoughItemsPlugin.translate(tooltipName.replaceFirst("[.]", toolTip)).split("\\\\n")));
		imageBox = new Rectangle(imageLeft, imageTop, imageRight - imageLeft, imageBottom - imageTop);
	}
	
	@Override
	public void getIngredients(IIngredients ingredients)
	{
		ingredients.setInputLists(ItemStack.class, Collections.singletonList(itemStacks));
		ingredients.setOutputs(ItemStack.class, itemStacks);
	}
	
	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY)
	{
		List<String> getTooltips = new ArrayList<String>();
		if (imageBox.contains(mouseX, mouseY))
			getTooltips.addAll(tooltipLines);
		
		return getTooltips;
	}
	
	protected String translateName(String catagoryName, String name)
	{
		return JustEnoughItemsPlugin.translate(catagoryName + ".name." + name);
	}
	
}