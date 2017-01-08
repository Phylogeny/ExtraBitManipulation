package com.phylogeny.extrabitmanipulation.api.jei.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.phylogeny.extrabitmanipulation.api.jei.InfoRecipeBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;

public class ModelInfoRecipe extends InfoRecipeBase
{
	public static final String[] GRAPHIC_NAMES = new String[]{"village_model", "village"};
	
	public static List<ModelInfoRecipe> create(List<ItemStack> sculptingStacks)
	{
		List<ModelInfoRecipe> recipes = new ArrayList<ModelInfoRecipe>();
		for (int i = 0; i < GRAPHIC_NAMES.length; i++)
			recipes.add(new ModelInfoRecipe(sculptingStacks, 854, 480, ModelInfoRecipeCategory.NAME, GRAPHIC_NAMES[i]));
		
		return recipes;
	}
	
	public ModelInfoRecipe(List<ItemStack> sculptingStacks, int imageWidth, int imageHeight, String tooltipName, String imageName)
	{
		super(sculptingStacks, imageWidth, imageHeight, imageName, imageName.toLowerCase().replace(" ", "_"),
				tooltipName, 2, 22, 158, 111, ModelInfoRecipeCategory.NAME);
	}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		minecraft.getTextureManager().bindTexture(image);
		float scaleFactor = 5.45F;
		int width = (int) (imageWidth / scaleFactor);
		int height = (int) (imageHeight / scaleFactor);
		int xPos = recipeWidth / 2 - width / 2;
		int yPos = 22;
		Gui.drawScaledCustomSizeModalRect(xPos, yPos, 0, 0, imageWidth, imageHeight, width, height, imageWidth, imageHeight);
		xPos = recipeWidth / 2 - minecraft.fontRendererObj.getStringWidth(name) / 2;
		yPos = 5;
		minecraft.fontRendererObj.drawString(name, xPos, yPos, Color.black.getRGB());
	}
	
}