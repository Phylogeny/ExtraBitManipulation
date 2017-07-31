package com.phylogeny.extrabitmanipulation.api.jei.shape;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.phylogeny.extrabitmanipulation.api.jei.InfoRecipeBase;
import com.phylogeny.extrabitmanipulation.api.jei.JustEnoughItemsPlugin;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class ShapeInfoRecipe extends InfoRecipeBase
{
	public static final String[] GRAPHIC_NAMES = new String[]{"sphere", "ellipsoid", "circular_cylinder", "elliptic_cylinder", "circular_cone",
																"elliptic_cone", "cube", "cuboid", "square_pyramid", "rectangular_pyramid"};
	private ResourceLocation graphicImageSymetrical, graphicImageAsymetrical;
	private int imageIndex;
	public static String textSolid, textHollow, textClosed, textOpen;
	
	public static List<ShapeInfoRecipe> create(List<ItemStack> sculptingStacks)
	{
		List<ShapeInfoRecipe> recipes = new ArrayList<ShapeInfoRecipe>();
		for (int i = 0; i < GRAPHIC_NAMES.length / 2; i++)
		{
			boolean isNarrow = i % 3 == 0;
			recipes.add(new ShapeInfoRecipe(sculptingStacks, isNarrow ? 746 : 1070, isNarrow ? 615 : 690, ShapeInfoRecipeCategory.NAME, i, isNarrow));
		}
		return recipes;
	}
	
	public ShapeInfoRecipe(List<ItemStack> sculptingStacks, int imageWidth, int imageHeight, String tooltipName, int imageIndex, boolean isNarrow)
	{
		super(sculptingStacks, imageWidth, imageHeight, GRAPHIC_NAMES[imageIndex * 2 + 1], GRAPHIC_NAMES[imageIndex * 2 + 1].toLowerCase().replace(" ", "_"),
				tooltipName, isNarrow ? 19 : 2, 22, isNarrow ? 142 : 158, 124, ShapeInfoRecipeCategory.NAME);
		this.imageIndex = imageIndex;
		graphicImageSymetrical = new ResourceLocation(Reference.GROUP_ID, "textures/jei/graphics/" + GRAPHIC_NAMES[imageIndex * 2] + ".png");
		graphicImageAsymetrical = new ResourceLocation(Reference.GROUP_ID, "textures/jei/graphics/" + GRAPHIC_NAMES[imageIndex * 2 + 1] + ".png");
		textSolid = translateName("solid");
		textHollow = translateName("hollow");
		textClosed = translateName("closed");
		textOpen = translateName("open");
	}
	
	private String translateName(String name)
	{
		return JustEnoughItemsPlugin.translate(ShapeInfoRecipeCategory.NAME + ".name." + name);
	}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		int xPos = 2;
		int yPos = -3;
		minecraft.getTextureManager().bindTexture(graphicImageSymetrical);
		Gui.drawScaledCustomSizeModalRect(xPos, yPos, 0, 0, 870, 870, 24, 24, 870, 870);
		xPos = 27;
		minecraft.getTextureManager().bindTexture(graphicImageAsymetrical);
		Gui.drawScaledCustomSizeModalRect(xPos, yPos, 0, 0, 870, 870, 24, 24, 870, 870);
		minecraft.getTextureManager().bindTexture(image);
		boolean isNarrow = imageIndex % 3 == 0;
		float scaleFactor = isNarrow ? 6.06F : 6.83F;
		int width = (int) (imageWidth / scaleFactor);
		int height = (int) (imageHeight / scaleFactor);
		xPos = recipeWidth / 2 - width / 2;
		yPos = 22;
		Gui.drawScaledCustomSizeModalRect(xPos, yPos, 0, 0, imageWidth, imageHeight, width, height, imageWidth, imageHeight);
		xPos = 55;
		int nameWidth = minecraft.fontRendererObj.getStringWidth(name);
		if (nameWidth < 103)
			xPos += 52 - nameWidth * 0.5;
		
		yPos = 5;
		minecraft.fontRendererObj.drawString(name, xPos, yPos, Color.black.getRGB());
		boolean isSloped = imageIndex == 2 || imageIndex == 4;
		xPos = isNarrow ? 51 : 33;
		yPos = imageIndex == 3 ? 25 : (isSloped ? 30 : 28);
		minecraft.fontRendererObj.drawString(textSolid, xPos - minecraft.fontRendererObj.getStringWidth(textSolid) / 2, yPos, Color.black.getRGB());
		xPos = 109;
		minecraft.fontRendererObj.drawString(textHollow, xPos - minecraft.fontRendererObj.getStringWidth(textHollow) / 2, yPos, Color.black.getRGB());
		if (imageIndex % 3 != 0)
		{
			xPos = 79;
			yPos = (isSloped ? 42 : 36);
			minecraft.fontRendererObj.drawString(textClosed, xPos - minecraft.fontRendererObj.getStringWidth(textClosed) / 2, yPos, Color.black.getRGB());
			minecraft.fontRendererObj.drawString(textOpen, xPos + 55 - minecraft.fontRendererObj.getStringWidth(textOpen) / 2, yPos, Color.black.getRGB());
		}
	}
	
}