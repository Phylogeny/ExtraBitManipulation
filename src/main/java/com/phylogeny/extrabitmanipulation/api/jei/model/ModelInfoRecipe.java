package com.phylogeny.extrabitmanipulation.api.jei.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IGuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import com.phylogeny.extrabitmanipulation.api.jei.InfoRecipeBase;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.client.GuiHelper;

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
	
	public ModelInfoRecipe(IGuiHelper guiHelper, List<ItemStack> sculptingStacks, int imageWidth, int imageHeight, String catagoryName, String imageName)
	{
		super(guiHelper, sculptingStacks, imageWidth, imageHeight, imageName,
				imageName.toLowerCase().replace(" ", "_"), catagoryName, 0, 22, 178, 122, catagoryName);
	}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		int xPos = 47;
		int yPos = 0;
		slotDrawable.draw(minecraft, xPos, yPos);
		ClientHelper.bindTexture(image);
		GuiHelper.drawTexturedRect(imageBox.getMinX(), imageBox.getMinY(), imageBox.getMaxX(), imageBox.getMaxY());
		xPos = 69;
		int nameWidth = minecraft.fontRenderer.getStringWidth(name);
		if (nameWidth < 103)
			xPos += 52 - nameWidth * 0.5;
		
		yPos = slotDrawable.getHeight() / 2 - minecraft.fontRenderer.FONT_HEIGHT / 2;
		minecraft.fontRenderer.drawString(name, xPos, yPos, Color.black.getRGB());
	}
	
}