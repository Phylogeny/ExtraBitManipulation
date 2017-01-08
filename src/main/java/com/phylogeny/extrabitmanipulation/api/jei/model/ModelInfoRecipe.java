package com.phylogeny.extrabitmanipulation.api.jei.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.phylogeny.extrabitmanipulation.api.jei.InfoRecipeBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import mezz.jei.api.IGuiHelper;

public class ModelInfoRecipe extends InfoRecipeBase
{
	public static final String[] GRAPHIC_NAMES = new String[]{"village_model", "village"};
	
	public static List<ModelInfoRecipe> create(IGuiHelper guiHelper, List<ItemStack> sculptingStacks)
	{
		List<ModelInfoRecipe> recipes = new ArrayList<ModelInfoRecipe>();
		for (int i = 0; i < GRAPHIC_NAMES.length; i++)
			recipes.add(new ModelInfoRecipe(guiHelper, sculptingStacks, 854, 480, ModelInfoRecipeCategory.NAME, GRAPHIC_NAMES[i]));
		
		return recipes;
	}
	
	public ModelInfoRecipe(IGuiHelper guiHelper, List<ItemStack> sculptingStacks, int imageWidth, int imageHeight, String tooltipName, String imageName)
	{
		super(guiHelper, sculptingStacks, imageWidth, imageHeight, imageName,
				imageName.toLowerCase().replace(" ", "_"), tooltipName, 0, 22, 178, 123, ModelInfoRecipeCategory.NAME);
	}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		int xPos = 47;
		int yPos = 0;
		slotDrawable.draw(minecraft, xPos, yPos);
		minecraft.getTextureManager().bindTexture(image);
		float scaleFactor = 4.79F;
		int width = (int) (imageWidth / scaleFactor);
		int height = (int) (imageHeight / scaleFactor);
		xPos = recipeWidth / 2 - width / 2;
		yPos += slotDrawable.getHeight() + 4;
		Gui.drawScaledCustomSizeModalRect(xPos, yPos, 0, 0, imageWidth, imageHeight, width, height, imageWidth, imageHeight);
		xPos = 69;
		int nameWidth = minecraft.fontRendererObj.getStringWidth(name);
		if (nameWidth < 103)
			xPos += 52 - nameWidth * 0.5;
		
		yPos = slotDrawable.getHeight() / 2 - minecraft.fontRendererObj.FONT_HEIGHT / 2;
		minecraft.fontRendererObj.drawString(name, xPos, yPos, Color.black.getRGB());
	}
	
}